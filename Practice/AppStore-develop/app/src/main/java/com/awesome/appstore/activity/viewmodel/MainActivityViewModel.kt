package com.awesome.appstore.activity.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesome.appstore.database.DatabaseStatus
import com.awesome.appstore.database.Notice
import com.awesome.appstore.event.Event
import com.awesome.appstore.protocol.request.RequestAppStatusChange
import com.awesome.appstore.repository.Repository
import com.awesome.appstore.util.FileDirectoryInfo
import com.awesome.appstore.util.PackageUtil
import com.awesome.appstore.util.StorePreference
import com.orhanobut.logger.Logger
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.util.concurrent.ExecutorService
import javax.inject.Inject


class MainActivityViewModel @Inject constructor(
//    private val storePreference: StorePreference,
//    private val repository: Repository,
//    private val packageUtil: PackageUtil
) :
    ViewModel() {
//
//    private val _startLoginActivity: MutableLiveData<Event<Boolean>> =
//        MutableLiveData<Event<Boolean>>()
//
//    var startLoginActivity: LiveData<Event<Boolean>> = _startLoginActivity
//
//    private val _startTabActivity: MutableLiveData<Event<Boolean>> =
//        MutableLiveData<Event<Boolean>>()
//    var startTabActivity: LiveData<Event<Boolean>> = _startTabActivity
//
//
//    lateinit var noticeList: LiveData<List<Notice?>?>
//
//    init {
////        storePreference.saveIdSavePreference("kksmho")
//        Logger.d(storePreference.loadIdSavePreference())
//        Logger.d("MainViewModel")
//    }
//
//    fun start(){
//        viewModelScope.launch {
//            val appInfoList = repository.getAppList()
//            appInfoList.data?.data?.apps?.forEach {
////              해당앱이 설치되어 있나 판단해서 설치유무 저장
//                if (packageUtil.findInstalledPackage(it.packageName)) {
//                    val packageInfo = packageUtil.getInstalledPackageInfo(it.packageName)
//                    it.installStatus = "Y"
//                    val installedAppVersionCode =
//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
//                            packageInfo?.longVersionCode
//                        } else {
//                            packageInfo?.versionCode
//                        }
//                    if (it.versionCode.toInt() > installedAppVersionCode?.toInt()!!) {
//                        it.updateStatus = "Y"
//                    } else it.updateStatus = "N"
//                    it.installDate = packageInfo?.firstInstallTime.toString()
//                    it.updateDate = packageInfo?.lastUpdateTime.toString()
//                } else {
//                    it.installStatus = "N"
//                    it.installDate = ""
//                    it.updateDate = ""
//                    it.updateStatus = "N"
//                }
//                it.progress = -1
//                Logger.d(it)
//
//                val result = repository.insertAppInfo(it)
//                when (result.databaseStatus) {
//                    DatabaseStatus.SUCCESS -> Logger.d(result.data)
//                    DatabaseStatus.ERROR -> Logger.d(result.message)
//                    DatabaseStatus.LOADING -> Logger.d(result.message)
//                }
//            }
//        }
//    }
//
//    override fun onCleared() {
//        super.onCleared()
////        fetch.close()
//    }
//
//    fun loginTabClick() {
//        _startLoginActivity.value = Event(true)
//    }
//
//    fun tabActivityClick() {
//        _startTabActivity.value = Event(true)
//    }
}