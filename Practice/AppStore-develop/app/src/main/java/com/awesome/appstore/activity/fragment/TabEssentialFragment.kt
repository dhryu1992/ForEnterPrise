package com.awesome.appstore.activity.fragment

import android.app.Activity
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesome.appstore.BuildConfig
import com.awesome.appstore.activity.DetailActivity
import com.awesome.appstore.R
import com.awesome.appstore.activity.adapter.AppListAdapter
import com.awesome.appstore.activity.adapter.ItemMoveCallback
import com.awesome.appstore.activity.viewmodel.TabEssentialFragmentViewModel
import com.awesome.appstore.config.StoreConst.Companion.REQUEST_INSTALL_PERMISSION
import com.awesome.appstore.config.StringTAG
import com.awesome.appstore.config.StringTAG.Companion.ACTION_APP_LIST_REFRESH
import com.awesome.appstore.config.StringTAG.Companion.EXTRA_INSTALL_APP
import com.awesome.appstore.config.StringTAG.Companion.EXTRA_KEY_APP_INFO
import com.awesome.appstore.config.StringTAG.Companion.EXTRA_REFRESH_FROM_INDEX
import com.awesome.appstore.database.AppInfo
import com.awesome.appstore.database.DatabaseStatus
import com.awesome.appstore.databinding.FragmentTabEssentialBinding
import com.awesome.appstore.module.viewmodels.ViewModelFactory
import com.awesome.appstore.util.DateUtil
import com.awesome.appstore.util.FileDirectoryInfo
import com.awesome.appstore.util.PackageUtil
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.orhanobut.logger.Logger
import dagger.android.support.DaggerFragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import kotlin.system.exitProcess

class TabEssentialFragment : DaggerFragment() {
    private lateinit var viewModel: TabEssentialFragmentViewModel
    private lateinit var adapter: AppListAdapter
    private var index: Int? = null
    lateinit var binding: FragmentTabEssentialBinding
    private var itemList: List<AppInfo?> = ArrayList()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var executorService: ExecutorService

    @Inject
    lateinit var packageUtil: PackageUtil

    @Inject
    lateinit var fileDirectoryInfo: FileDirectoryInfo

    lateinit var targetApp: AppInfo

    var appPosition: Int = -1

    companion object {
        fun newInstance(index: Int): TabEssentialFragment {
            val fragment = TabEssentialFragment()
            val bundle = Bundle()
            bundle.putInt(StringTAG.ARG_SECTION_NUMBER, index)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), this.viewModelFactory).get(
            TabEssentialFragmentViewModel::class.java
        )
        viewModel.essentialAppList.observe(this, {
            itemList = it
            adapter.items = (it as ArrayList<AppInfo?>)
            if (itemList.isEmpty()) binding.imageNoData.visibility = View.VISIBLE
            else binding.imageNoData.visibility = View.GONE
            adapter.notifyDataSetChanged()
//            parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
        })
        viewModel.appDownloadComplete.observe(this, {
//            ????????? ????????????
            adapter.items[appPosition]?.progress = -1
            adapter.notifyItemChanged(appPosition)

            val intentFilter = IntentFilter()
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
            intentFilter.addDataScheme("package")
            //??? ?????? ?????????
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    Logger.d("onReceive: $intent")
                    adapter.items[appPosition]?.installStatus = "Y"
                    val installDate =
                        packageUtil.getInstalledPackageInfo(it)?.firstInstallTime.toString()
                            ?: DateUtil().getCurrentDateToString()!!
                    val replaced = intent?.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                    adapter.items[appPosition]?.installDate = installDate
                    if (replaced == true) {
                        //??? ????????????
                        Logger.d("Replaced ? $replaced")
                        adapter.items[appPosition]?.updateStatus = "N"
                        viewModel.appStatusUpdate(it, "U")
                    } else viewModel.appStatusUpdate(it, "I")
                    adapter.notifyItemChanged(appPosition)
                    requireActivity().unregisterReceiver(this)
                }
            }
            requireActivity().registerReceiver(receiver, intentFilter)

            Logger.d(it)
            Logger.d("download complete and install start")
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
                startActivityForResult(intent, REQUEST_INSTALL_PERMISSION)
                Toast.makeText(requireActivity(), "???????????? ??????", Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.appDownloadProgress.observe(this, {
            adapter.items[appPosition]?.progress = it
            adapter.notifyItemChanged(appPosition)
        })
        viewModel.appStatusUpdateComplete.observe(this, {
            Logger.d("refresh broadcast")
            Intent().also { intent ->
                intent.action = ACTION_APP_LIST_REFRESH
                intent.putExtra("EXTRA_REFRESH_FROM_INDEX", "$index")
                intent.setPackage(requireActivity().packageName)
                requireActivity().sendBroadcast(intent)
            }
        })
        viewModel.tokenExpireDialog.observe(this, {
            MaterialAlertDialogBuilder(requireActivity())
                .setCancelable(false)
                .setMessage("????????? ???????????? ??????????????? ???????????????.")
                .setPositiveButton("??????", DialogInterface.OnClickListener { _, _ ->
                    val i = requireActivity().packageManager.getLaunchIntentForPackage(requireContext().packageName);
                    val componentName = i?.component
                    requireActivity().startActivity(Intent.makeRestartActivityTask(componentName));
                    exitProcess(0);
                })
                .show()
        })
        var index = 1
        if (arguments != null) {
            index = requireArguments().getInt(StringTAG.ARG_SECTION_NUMBER)
        }
        if (this.index == null) this.index = index
        Logger.d("set index ==> $index")

        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_APP_LIST_REFRESH)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Logger.d("onReceive: $intent")
                if (intent?.getStringExtra(EXTRA_REFRESH_FROM_INDEX) != "$index") {
                    Logger.d("refresh Broadcast Receive")
                    viewModel.LoadAppList()
                }
            }
        }
        requireActivity().registerReceiver(receiver, intentFilter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_INSTALL_PERMISSION) {
            packageUtil.startInstallPackage(
                requireActivity(),
                "/${targetApp.packageName}.apk",
                1000,
                true
            )
        }
    }

    override fun onResume() {
        super.onResume()
        index?.let { viewModel.setIndex(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_tab_essential, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()
        val root: View = binding.root
        val appList: RecyclerView = binding.recyclerAppList
        index?.let { viewModel.setIndex(it) }
        Logger.d("this fragment index ==> $index")
//        if (viewModel.essentialAppList.value.isNullOrEmpty()) viewModel.LoadAppList()
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
            }
//            override fun onClickDelete(appInfo: AppInfo?) {
//            }
        })

//        val items = viewModel.getAppList()
        adapter.items = itemList as ArrayList<AppInfo?>
        Logger.d(adapter.items)
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
