package com.awesome.appstore.activity.fragment

import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesome.appstore.R
import com.awesome.appstore.activity.DetailActivity
import com.awesome.appstore.activity.TabActivity
import com.awesome.appstore.activity.adapter.AppLauncherAdapter
import com.awesome.appstore.activity.adapter.LauncherItemMoveCallback
import com.awesome.appstore.activity.viewmodel.TabExecuteFragmentViewModel
import com.awesome.appstore.config.*
import com.awesome.appstore.database.AppInfo
import com.awesome.appstore.database.PushCount
import com.awesome.appstore.databinding.FragmentTabExecuteBinding
import com.awesome.appstore.module.viewmodels.ViewModelFactory
import com.awesome.appstore.util.PackageUtil
import com.awesome.appstore.util.StorePreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.make
import com.orhanobut.logger.Logger
import dagger.android.support.DaggerFragment
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import kotlin.system.exitProcess

class TabExecuteFragment : DaggerFragment() {
    private lateinit var viewModel: TabExecuteFragmentViewModel
    private lateinit var adapter: AppLauncherAdapter
    private var index: Int? = null
    lateinit var binding: FragmentTabExecuteBinding
    private var mVelocityTracker: VelocityTracker? = null
    private var itemList: List<AppInfo?> = ArrayList()
    lateinit var targetApp: AppInfo

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var executorService: ExecutorService

    @Inject
    lateinit var storePreference: StorePreference

    @Inject
    lateinit var packageUtil: PackageUtil

    companion object {
        fun newInstance(index: Int): TabExecuteFragment {
            val fragment = TabExecuteFragment()
            val bundle = Bundle()
            bundle.putInt(StringTAG.ARG_SECTION_NUMBER, index)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(TabExecuteFragmentViewModel::class.java)
        viewModel.executeAppList.observe(this, {
            itemList = it
            adapter.items = (it as ArrayList<AppInfo?>)
            adapter.badgeCountList = viewModel.pushCountList.value as ArrayList<PushCount?>
            adapter.notifyDataSetChanged()
//            parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
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
        var index = 1
        if (arguments != null) {
            index = requireArguments().getInt(StringTAG.ARG_SECTION_NUMBER)
        }
        if (this.index == null) this.index = index
        Logger.d("set index ==> $index")

        val intentFilter = IntentFilter()
        intentFilter.addAction(StringTAG.ACTION_APP_LIST_REFRESH)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Logger.d("onReceive: $intent")
                Logger.d("refresh Broadcast Receive")
                viewModel.loadAppList()
            }
        }
        requireActivity().registerReceiver(receiver, intentFilter)
    }

    override fun onResume() {
        super.onResume()
        index?.let { viewModel.setIndex(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab_execute, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()
        val root: View = binding.root
        val appList: RecyclerView = binding.recyclerAppList
        index?.let { viewModel.setIndex(it) }
        Logger.d("this fragment index ==> $index")
//        if(viewModel.executeAppList.value.isNullOrEmpty()) viewModel.LoadAppList()
        adapter = AppLauncherAdapter(object : AppLauncherAdapter.LauncherListener {
            override fun onClickApp(model: AppInfo?) {
                Logger.d(packageUtil.getPackageNameHashString(model?.packageName!!))
                if (TabActivity.badgeEssential <= 0) {
                    if (model.updateStatus == "Y" && model.updateType == "M") {
                        Toast.makeText(requireContext(), "필수 업데이트가 있습니다. 업데이트를 진행해 주세요", Toast.LENGTH_SHORT).show()
                        Intent(requireContext(), DetailActivity::class.java)
                            .apply { putExtra(StringTAG.EXTRA_KEY_APP_INFO, model) }
                            .let { startActivity(it) }
                    } else {
                        if (packageUtil.getPackageNameHashString(model.packageName) == model.checksum) {
                            viewModel.pushCountReset(model.packageName)
                            packageUtil.startApplication(model.packageName)
                            Logger.d("${model.appName} run")
                        } else {
                            Snackbar.make(binding.root, "앱 위변조 검사 통과 실패!!", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Intent().also { intent ->
                        intent.action = StringTAG.ACTION_ESSENTIAL_APP_EXIST
                        intent.setPackage(requireActivity().packageName)
                        requireActivity().sendBroadcast(intent)
                    }
                }
            }

            override fun onClickDelete(appInfo: AppInfo?) {
                targetApp = appInfo!!
                val packageUri = Uri.parse("package:${appInfo.packageName}")
                val deleteIntent = Intent(Intent.ACTION_DELETE, packageUri)
                requireActivity().startActivity(deleteIntent)

                val removeIntentFilter = IntentFilter()
                removeIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
                removeIntentFilter.addDataScheme("package")
                val removeReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        Logger.d("onReceive: $intent")
                        Logger.d("remove Broadcast Receive")
                        viewModel.deleteApp(targetApp.packageName)
                        requireActivity().unregisterReceiver(this)
                    }
                }
                requireActivity().registerReceiver(removeReceiver, removeIntentFilter)
            }

            override fun onAppMove(appInfo: ArrayList<AppInfo?>) {
                viewModel.setAppPosition(appInfo)
            }
        })
        adapter.items = itemList as ArrayList<AppInfo?>
        adapter.badgeCountList = viewModel.pushCountList.value as ArrayList<PushCount?>
        Logger.d(adapter.items)
        appList.adapter = adapter
        val helper = ItemTouchHelper(LauncherItemMoveCallback(adapter, this))
        helper.attachToRecyclerView(appList)
        val layoutManager: LinearLayoutManager = GridLayoutManager(context, 4)
        appList.layoutManager = layoutManager
        appList.visibility = View.VISIBLE
        return root
    }
}
