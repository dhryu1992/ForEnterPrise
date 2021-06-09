package com.awesome.appstore.activity.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesome.appstore.config.StringTAG
import com.awesome.appstore.database.*
import com.awesome.appstore.event.Event
import com.awesome.appstore.network.NetworkStatus
import com.awesome.appstore.protocol.request.RequestAppStatusChange
import com.awesome.appstore.repository.Repository
import com.awesome.appstore.util.StorePreference
import com.google.android.material.snackbar.Snackbar
import com.orhanobut.logger.Logger
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import javax.inject.Inject

class TabExecuteFragmentViewModel @Inject constructor(
    private val repository: Repository,
    private val executorService: ExecutorService,
    private val preference: StorePreference
) : ViewModel() {

    private val mIndex = MutableLiveData<Int>()
    private val _executeAppList: MutableLiveData<List<AppInfo?>> = MutableLiveData<List<AppInfo?>>()
    private val _appStatusUpdateComplete: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _tokenExpireDialog: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val _pushCountList: MutableLiveData<List<PushCount?>> = MutableLiveData(ArrayList<PushCount?>())

    var tokenExpireDialog: LiveData<Event<Boolean>> = _tokenExpireDialog
    var appStatusUpdateComplete: LiveData<Event<Boolean>> = _appStatusUpdateComplete
    var executeAppList: LiveData<List<AppInfo?>> = _executeAppList
    var pushCountList: LiveData<List<PushCount?>> = _pushCountList

    init {
        loadAppList()
    }

    fun getmIndex(): MutableLiveData<Int> {
        return mIndex
    }

    fun loadPushCount() {
        viewModelScope.launch {
            _pushCountList.postValue(repository.getAllPushCount().data ?: ArrayList<PushCount?>())
        }
    }

    fun pushCountReset(pkgName: String) {
        viewModelScope.launch {
            repository.insertPushCount(PushCount(pkgName))
            loadAppList()
            _appStatusUpdateComplete.postValue(Event(true))
        }
    }

    fun loadAppList() {
        loadPushCount()
        viewModelScope.launch {
            val installedAppList: List<AppInfo?> = repository.selectInstalledAppInfo().data?.filter { it?.appStatus == "A" } ?: ArrayList<AppInfo?>()
            val sortedAppList = ArrayList<AppInfo?>(installedAppList.size)
            Logger.d(installedAppList)

            val indexData = (repository.selectAllLauncherIndex().data ?: ArrayList<LauncherIndex?>()).sortedBy { it?.index }
            Logger.d(indexData)

            indexData.forEach { index ->
                (installedAppList.first { it?.packageName == index?.packageName })?.let { appInfo ->
                    sortedAppList.add(appInfo)
                }
            }

            if (sortedAppList.size != installedAppList.size) {
                Logger.d(installedAppList - sortedAppList)
                sortedAppList.addAll(installedAppList - sortedAppList)
            }

            sortedAppList.forEach {
                it?.let { repository.insertLauncherIndex(LauncherIndex(sortedAppList.indexOf(it), it.packageName)) }
            }
            _executeAppList.postValue(sortedAppList)
            Logger.d(sortedAppList)
        }
    }

    fun setAppPosition(appInfoList: List<AppInfo?>) {
        viewModelScope.launch {
            appInfoList.forEach {
                repository.insertLauncherIndex(LauncherIndex(appInfoList.indexOf(it), it?.packageName!!))
            }
        }
    }

    fun setIndex(index: Int) {
        mIndex.value = index
    }

    fun deleteApp(pkgName: String) {
        viewModelScope.launch {
            val app: AppInfo
            val result = repository.selectAppInfoByPackageName(pkgName)
            when (result.databaseStatus) {
                DatabaseStatus.SUCCESS -> {
                    repository.insertPushCount(PushCount(pkgName))
                    app = result.data!!
                    app.installStatus = "N"
                    app.installDate = ""
                    app.updateDate = ""
                    app.updateStatus = "N"
                    if (repository.insertAppInfo(app).databaseStatus == DatabaseStatus.SUCCESS) {
                        _appStatusUpdateComplete.postValue(Event(true))
                    }
                    repository.deleteLauncherIndexByPkgName(app.packageName)
                    val appResult = repository.appStatusChangeCallback(
                        RequestAppStatusChange(
                            RequestAppStatusChange.StatusChange(
                                preference.loadIdSavePreference()!!,
                                app.appId,
                                app.versionId,
                                "D"
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