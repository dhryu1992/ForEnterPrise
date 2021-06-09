package com.awesome.appstore.activity.fragment

import android.app.Activity.RESULT_OK
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesome.appstore.BuildConfig
import com.awesome.appstore.activity.DetailActivity
import com.awesome.appstore.R
import com.awesome.appstore.activity.adapter.AppListAdapter
import com.awesome.appstore.activity.adapter.ItemMoveCallback
import com.awesome.appstore.activity.viewmodel.TabAllFragmentViewModel
import com.awesome.appstore.config.StoreConst
import com.awesome.appstore.config.StringTAG
import com.awesome.appstore.config.StringTAG.Companion.EXTRA_KEY_APP_INFO
import com.awesome.appstore.database.AppInfo
import com.awesome.appstore.database.DatabaseStatus
import com.awesome.appstore.databinding.FragmentTabAllBinding
import com.awesome.appstore.module.viewmodels.ViewModelFactory
import com.awesome.appstore.util.DateUtil
import com.awesome.appstore.util.PackageUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.orhanobut.logger.Logger
import dagger.android.support.DaggerFragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import kotlin.system.exitProcess

class TabAllFragment : DaggerFragment() {
    private lateinit var viewModel: TabAllFragmentViewModel
    private lateinit var adapter: AppListAdapter
    private var index: Int? = null
    lateinit var binding: FragmentTabAllBinding
    private var itemList: List<AppInfo?> = ArrayList()
    lateinit var targetApp: AppInfo
    var appPosition: Int = -1

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var executorService: ExecutorService

    @Inject
    lateinit var packageUtil: PackageUtil

    companion object {
        fun newInstance(index: Int): TabAllFragment {
            val fragment = TabAllFragment()
            val bundle = Bundle()
            bundle.putInt(StringTAG.ARG_SECTION_NUMBER, index)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), this.viewModelFactory).get(
            TabAllFragmentViewModel::class.java
        )
        viewModel.allAppList.observe(this, {
            itemList = it
            adapter.items = (it as ArrayList<AppInfo?>)
            if (itemList.isEmpty()) binding.imageNoData.visibility = View.VISIBLE
            else binding.imageNoData.visibility = View.GONE
            adapter.notifyDataSetChanged()
//            parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
        })
        var index = 1
        if (arguments != null) {
            index = requireArguments().getInt(StringTAG.ARG_SECTION_NUMBER)
        }
        if (this.index == null) this.index = index
        Logger.d("set index ==> $index")

        viewModel.appDownloadComplete.observe(this, {
//            아이템 업데이트
            adapter.items[appPosition]?.progress = -1
            adapter.notifyItemChanged(appPosition)

            val intentFilter = IntentFilter()
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
            intentFilter.addDataScheme("package")
            //앱 설치 리시버
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    Logger.d("onReceive: $intent")
                    adapter.items[appPosition]?.installStatus = "Y"
                    val installDate =
                        packageUtil.getInstalledPackageInfo(it)?.firstInstallTime.toString()
                            ?: DateUtil().getCurrentDateToString()!!
                    val replaced = intent?.getBooleanExtra(Intent.EXTRA_REPLACING, false);
                    adapter.items[appPosition]?.installDate = installDate
                    if (replaced == true) {
                        //앱 업데이트
                        Logger.d("Replaced ? $replaced")
                        adapter.items[appPosition]?.updateStatus = "N"
                        viewModel.appStatusUpdate(it, "U")
                    } else viewModel.appStatusUpdate(it, "I")
                    adapter.notifyItemChanged(appPosition)
                    requireActivity().unregisterReceiver(this)
                }
            }
            requireActivity().registerReceiver(receiver, intentFilter)

            Logger.d("$it download complete and install start")
            if (requireActivity().packageManager.canRequestPackageInstalls()) {
                Logger.d("canRequestPackageInstalls")
                packageUtil.startInstallPackage(
                    requireActivity(),
                    "/$it.apk",
                    1000,
                    true
                )
            } else {
                val intent = Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:${context?.packageName}")
                )
                startActivityForResult(intent, StoreConst.REQUEST_INSTALL_PERMISSION)
                Toast.makeText(requireActivity(), "설치권한 없음", Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.appStatusUpdateComplete.observe(this, {
            Logger.d("refresh broadcast")
            Intent().also { intent ->
                intent.action = StringTAG.ACTION_APP_LIST_REFRESH
                intent.putExtra("EXTRA_REFRESH_FROM_INDEX", "$index")
                intent.setPackage(requireActivity().packageName)
                requireActivity().sendBroadcast(intent)
            }
        })
        viewModel.appDownloadProgress.observe(this, {
            adapter.items[appPosition]?.progress = it
            adapter.notifyItemChanged(appPosition)
        })
        viewModel.tokenExpireDialog.observe(this,{
            MaterialAlertDialogBuilder(requireActivity())
                .setCancelable(false)
                .setMessage("세션이 만료되어 재로그인이 필요합니다.")
                .setPositiveButton("확인", DialogInterface.OnClickListener { _, _ ->
                    val i = requireActivity().packageManager.getLaunchIntentForPackage(requireContext().packageName);
                    val componentName = i?.component
                    requireActivity().startActivity(Intent.makeRestartActivityTask(componentName));
                    exitProcess(0);
                })
                .show()
        })
        viewModel.toastMsg.observe(this,{
            Toast.makeText(requireActivity(),it,Toast.LENGTH_SHORT).show()
        })
        val intentFilter = IntentFilter()
        intentFilter.addAction(StringTAG.ACTION_APP_LIST_REFRESH)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Logger.d("onReceive: $intent")
                if (intent?.getStringExtra(StringTAG.EXTRA_REFRESH_FROM_INDEX) != "$index") {
                    Logger.d("refresh Broadcast Receive")
                    viewModel.LoadAppList()
                }
            }
        }
        requireActivity().registerReceiver(receiver, intentFilter)
    }

    override fun onResume() {
        super.onResume()
        index?.let { viewModel.setIndex(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logger.d("onActivityResult resultCode: $resultCode \n/${viewModel.appDownloadComplete.value}.apk")
        if (requestCode == StoreConst.REQUEST_INSTALL_PERMISSION && resultCode == RESULT_OK) {
            packageUtil.startInstallPackage(
                requireActivity(),
                "/${viewModel.appDownloadComplete.value}.apk",
                1000,
                true
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab_all, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()
        val root: View = binding.root
        val appList: RecyclerView = binding.recyclerAppList
        index?.let { viewModel.setIndex(it) }
        Logger.d("this fragment index ==> $index")
//        if(viewModel.allAppList.value.isNullOrEmpty()) viewModel.LoadAppList()
        adapter = AppListAdapter(object : AppListAdapter.TouchListener {
            override fun onClickApp(model: AppInfo?) {
                Toast.makeText(requireContext(), model?.appName, Toast.LENGTH_SHORT).show();
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra(EXTRA_KEY_APP_INFO, model)
                requireContext().startActivity(intent)
                Logger.d(model.toString())
            }

            override fun onClickInstall(model: AppInfo?, position: Int) {
                targetApp = model!!
                appPosition = position
                viewModel.onClickInstall(model)

//                  asset 내부의 apk 파일을 설치
//                val assetManager = context!!.assets
//                try {
//                    val inputStream = assetManager.open("sample.apk")
//                    val outPath = context!!.filesDir.absolutePath + "/sample.apk"
//                    val outPathStream = FileOutputStream(outPath)
//                    val bytes = ByteArray(1024)
//                    while (true) {
//                        val data = inputStream.read(bytes)
//                        if (data == -1) {
//                            break
//                        }
//                        outPathStream.write(bytes, 0, data)
//                    }
//                    inputStream.close()
//                    outPathStream.close()
//                    Logger.d(File(context!!.filesDir.absolutePath + "/sample.apk").exists())
//                    val apkPath = context!!.filesDir.absolutePath + "/sample.apk"
//                    val file = File(apkPath)
//                    val apkUri = FileProvider.getUriForFile(
//                        context!!.applicationContext,
//                        BuildConfig.APPLICATION_ID.toString() + ".fileprovider",
//                        file
//                    )
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
//                    context!!.startActivity(intent)
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
            }
//            override fun onClickDelete(appInfo: AppInfo?) {
//                val packageUri = Uri.parse(appInfo?.packageName)
//                val deleteIntent = Intent(Intent.ACTION_DELETE, packageUri)
//                startActivity(deleteIntent)
//            }
        })
//        val items = viewModel.getAppList()
        adapter.items = (itemList as ArrayList<AppInfo?>)
        Logger.d(itemList)
        if (itemList.isEmpty()) binding.imageNoData.visibility = View.VISIBLE
        else binding.imageNoData.visibility = View.GONE
        appList.adapter = adapter
        val helper = ItemTouchHelper(ItemMoveCallback(adapter))
        helper.attachToRecyclerView(appList)
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.VERTICAL
        appList.layoutManager = layoutManager
        appList.visibility = View.VISIBLE

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.appListRefresh()
            binding.swipeRefresh.isRefreshing = false
            viewModel.LoadAppList()
        }
        return root
    }
}
