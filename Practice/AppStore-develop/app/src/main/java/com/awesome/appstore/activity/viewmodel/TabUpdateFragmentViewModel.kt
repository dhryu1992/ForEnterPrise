package com.awesome.appstore.activity.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesome.appstore.config.StringTAG
import com.awesome.appstore.database.AppInfo
import com.awesome.appstore.database.AppInfoDao
import com.awesome.appstore.database.DatabaseStatus
import com.awesome.appstore.database.StoreDatabase
import com.awesome.appstore.event.Event
import com.awesome.appstore.network.NetworkStatus
import com.awesome.appstore.protocol.request.RequestAppStatusChange
import com.awesome.appstore.repository.Repository
import com.awesome.appstore.util.FileDirectoryInfo
import com.awesome.appstore.util.PackageUtil
import com.awesome.appstore.util.StorePreference
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.orhanobut.logger.Logger
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import javax.inject.Inject
import kotlin.math.round

class TabUpdateFragmentViewModel @Inject constructor(
    private val repository: Repository,
    private val executorService: ExecutorService,
    private val packageUtil: PackageUtil,
    private val fileDirectoryInfo: FileDirectoryInfo,
    private val preference: StorePreference
) :
    ViewModel() {
    init {
        LoadAppList()
    }

    private val mIndex = MutableLiveData<Int>()
    private val _updateAppList: MutableLiveData<List<AppInfo?>> = MutableLiveData<List<AppInfo?>>()
    private val _appDownloadComplete: MutableLiveData<String> = MutableLiveData<String>()
    private val _appDownloadProgress: MutableLiveData<Int> = MutableLiveData<Int>()
    private val _appStatusUpdateComplete: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _tokenExpireDialog: MutableLiveData<Event<Boolean>> = MutableLiveData()

    var tokenExpireDialog: LiveData<Event<Boolean>> = _tokenExpireDialog
    var appStatusUpdateComplete: LiveData<Event<Boolean>> = _appStatusUpdateComplete
    var appDownloadProgress: LiveData<Int> = _appDownloadProgress
    var appDownloadComplete: LiveData<String> = _appDownloadComplete
    var updateAppList: LiveData<List<AppInfo?>> = _updateAppList
    fun getmIndex(): MutableLiveData<Int> {
        return mIndex
    }

    fun LoadAppList() {
        executorService.submit {
            viewModelScope.launch {
                _updateAppList.postValue(repository.selectUpdateAppInfo().data ?: ArrayList<AppInfo?>())
            }
        }
    }

    fun setIndex(index: Int) {
        mIndex.value = index
    }

    fun appListRefresh() {
        viewModelScope.launch {
            val result = repository.getAppList()
            when (result.networkStatus) {
                NetworkStatus.SUCCESS -> {
                    result.data?.data?.apps?.forEach {
                        if (packageUtil.findInstalledPackage(it.packageName)) {
                            val packageInfo = packageUtil.getInstalledPackageInfo(it.packageName)
                            it.installStatus = "Y"
                            val installedAppVersionCode =
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                    packageInfo?.longVersionCode
                                } else packageInfo?.versionCode
                            if (it.versionCode.toInt() > installedAppVersionCode?.toInt()!!) {
                                it.updateStatus = "Y"
                            } else it.updateStatus = "N"
                            it.installDate = packageInfo?.firstInstallTime.toString()
                            it.updateDate = packageInfo?.lastUpdateTime.toString()
                        } else {
                            it.installStatus = "N"
                            it.installDate = ""
                            it.updateDate = ""
                            it.updateStatus = "N"
                        }
                        it.progress = -1
                        Logger.d(it)

                        val dbResult = repository.insertAppInfo(it)
                        when (dbResult.databaseStatus) {
                            DatabaseStatus.SUCCESS -> {
                                Logger.d("appListRefresh SUCCESS")
                            }
                            DatabaseStatus.ERROR -> {
                                Logger.d("appListRefresh ERROR")
                            }
                            DatabaseStatus.LOADING -> {
                            }
                        }
                    }
                    repository.selectAllAppInfo().data?.forEach {
                        if (!result.data?.data?.apps?.contains(it)!!){
                            repository.insertAppInfo(it!!.apply { appStatus = "D" })
                        }
                    }
                }
                NetworkStatus.ERROR -> {
                    if (result.message == StringTAG.ERROR_CODE_401) {
                        _tokenExpireDialog.postValue(Event(true))
                    }
                }
            }

        }
    }

    fun onClickInstall(model: AppInfo) {
        apkDownload(model)
    }

    private fun apkDownload(model: AppInfo) {
        var id = PRDownloader
            .download(model.downloadUrl, fileDirectoryInfo.internalDownLoads, "${model.packageName}.apk")
//          .setHeader("", "")
            .build()
            .setOnProgressListener {
//                Logger.d("currentBytes = ${it.currentBytes}, totalBytes = ${it.totalBytes}")
                _appDownloadProgress.value = round((it.currentBytes.toDouble().div(it.totalBytes.toDouble())).times(100))
                    .toInt()
            }
            .setOnCancelListener {
                Logger.d("OnCancel")
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    Logger.d(fileDirectoryInfo.internalDownLoads)
                    Logger.d("onDownloadComplete ==> file isExist? : ${File(fileDirectoryInfo.internalDownLoads + "/${model.packageName}.apk").exists()}")
                    _appDownloadComplete.value = model.packageName
                }

                override fun onError(error: com.downloader.Error?) {
                    Logger.d(error)
                }
            })
    }

    fun appStatusUpdate(pkgName: String) {
        viewModelScope.launch {
            val app: AppInfo
            val result = repository.selectAppInfoByPackageName(pkgName)
            when (result.databaseStatus) {
                DatabaseStatus.SUCCESS -> {
                    app = result.data!!
                    app.updateStatus = "N"
                    app.updateDate = packageUtil.getInstalledPackageInfo(pkgName)?.lastUpdateTime.toString()
                    if (repository.insertAppInfo(app).databaseStatus == DatabaseStatus.SUCCESS) {
                        _appStatusUpdateComplete.postValue(Event(true))
                    }
                    val appResult = repository.appStatusChangeCallback(
                        RequestAppStatusChange(
                            RequestAppStatusChange.StatusChange(
                                preference.loadIdSavePreference()!!,
                                app.appId,
                                app.versionId,
                                "U"
                            )
                        )
                    )
                    if (appResult.networkStatus == NetworkStatus.ERROR && appResult.message == StringTAG.ERROR_CODE_401) {
                        _tokenExpireDialog.postValue(Event(true))
                    }
                }
            }
        }
    }
}