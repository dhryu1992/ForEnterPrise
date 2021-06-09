package com.awesome.appstore.activity.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesome.appstore.config.StoreConst
import com.awesome.appstore.config.StringTAG
import com.awesome.appstore.database.AppInfo
import com.awesome.appstore.database.DatabaseStatus
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
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import javax.inject.Inject
import kotlin.Error
import kotlin.math.round

class TabEssentialFragmentViewModel @Inject constructor(
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
    private val _essentialAppList: MutableLiveData<List<AppInfo?>> = MutableLiveData<List<AppInfo?>>()
    private val _appDownloadComplete: MutableLiveData<String> = MutableLiveData<String>()
    private val _appDownloadProgress: MutableLiveData<Int> = MutableLiveData<Int>()
    private val _appStatusUpdateComplete: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _tokenExpireDialog: MutableLiveData<Event<Boolean>> = MutableLiveData()

    var tokenExpireDialog: LiveData<Event<Boolean>> = _tokenExpireDialog
    var appStatusUpdateComplete: LiveData<Event<Boolean>> = _appStatusUpdateComplete
    var appDownloadProgress: LiveData<Int> = _appDownloadProgress
    var essentialAppList: LiveData<List<AppInfo?>> = _essentialAppList
    var appDownloadComplete: LiveData<String> = _appDownloadComplete
    fun getmIndex(): MutableLiveData<Int> {
        return mIndex
    }
    var token = preference.loadTokenPreference()

    fun LoadAppList() {
        executorService.submit {
            viewModelScope.launch {
                _essentialAppList.postValue(
                    repository.selectEssentialAppInfo().data ?: ArrayList<AppInfo?>()
                )
            }
        }
    }

    fun setIndex(index: Int) {
        mIndex.value = index
    }

    fun appListRefresh() {
        viewModelScope.launch {
            val netResult = repository.getAppList()
            when (netResult.networkStatus) {
                NetworkStatus.SUCCESS -> {
                    netResult.data?.data?.apps?.forEach {
                        if (packageUtil.findInstalledPackage(it.packageName)) {
                            val packageInfo = packageUtil.getInstalledPackageInfo(it.packageName)
                            it.installStatus = "Y"
                            val installedAppVersionCode =
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                    packageInfo?.longVersionCode
                                } else {
                                    packageInfo?.versionCode
                                }
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
                            DatabaseStatus.ERROR -> Logger.d("db insert fail")
                        }
                    }
                    repository.selectAllAppInfo().data?.forEach {
                        if (!netResult.data?.data?.apps?.contains(it)!!){
                            repository.insertAppInfo(it!!.apply { appStatus = "D" })
                        }
                    }
                }
                NetworkStatus.ERROR -> {
                    if (netResult.message == StringTAG.ERROR_CODE_401) {
                        _tokenExpireDialog.postValue(Event(true))
                    }
                    Logger.d("갱신실패")
                }
            }
        }
    }

    fun onClickInstall(model: AppInfo) {
        apkDownload(model)
    }

    fun appStatusUpdate(pkgName: String, installType: String) {
        viewModelScope.launch {
            val app: AppInfo
            val result = repository.selectAppInfoByPackageName(pkgName)
            val packInfo = packageUtil.getInstalledPackageInfo(pkgName)
            when (result.databaseStatus) {
                DatabaseStatus.SUCCESS -> {
                    app = result.data!!
                    app.installDate = packInfo?.firstInstallTime.toString()
                    app.installStatus = "Y"
                    if (installType == "U") {
                        app.updateDate = packInfo?.lastUpdateTime.toString()
                        val appResult = repository.appStatusChangeCallback(
                            RequestAppStatusChange(
                                RequestAppStatusChange.StatusChange(
                                    preference.loadIdSavePreference()!!,
                                    app.appId,
                                    app.versionId,
                                    installType
                                )
                            )
                        )
                        if (appResult.networkStatus == NetworkStatus.ERROR && appResult.message == StringTAG.ERROR_CODE_401) {
                            _tokenExpireDialog.postValue(Event(true))
                        }
                    } else if (installType == "I") {
                        val appResult = repository.appStatusChangeCallback(
                            RequestAppStatusChange(
                                RequestAppStatusChange.StatusChange(
                                    preference.loadIdSavePreference()!!,
                                    app.appId,
                                    app.versionId,
                                    installType
                                )
                            )
                        )
                        if (appResult.networkStatus == NetworkStatus.ERROR && appResult.message == StringTAG.ERROR_CODE_401) {
                            _tokenExpireDialog.postValue(Event(true))
                        }
                    }
                    if (repository.insertAppInfo(app).databaseStatus == DatabaseStatus.SUCCESS) {
                        _appStatusUpdateComplete.postValue(Event(true))
                    }
                }
            }
        }
    }

    private fun apkDownload(model: AppInfo) {
        var id = PRDownloader.download(
            model.downloadUrl,
            fileDirectoryInfo.internalDownLoads,
            "${model.packageName}.apk"
        )
            .setHeader("Authorization", "Bearer $token")
            .setHeader("Accept", "application/json")
            .build()
            .setOnProgressListener {
//                Logger.d("currentBytes = ${it.currentBytes}, totalBytes = ${it.totalBytes}")
                _appDownloadProgress.value = round(
                    (it.currentBytes.toDouble().div(it.totalBytes.toDouble())).times(100)
                ).toInt()
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
}