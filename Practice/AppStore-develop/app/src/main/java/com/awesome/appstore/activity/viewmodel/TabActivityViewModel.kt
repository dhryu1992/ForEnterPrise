package com.awesome.appstore.activity.viewmodel

import android.os.Handler
import androidx.lifecycle.*
import com.awesome.appstore.AppStoreApplication
import com.awesome.appstore.config.StringTAG
import com.awesome.appstore.database.AppInfo
import com.awesome.appstore.event.Event
import com.awesome.appstore.network.NetworkStatus
import com.awesome.appstore.protocol.response.Profile
import com.awesome.appstore.repository.Repository
import com.awesome.appstore.util.FileDirectoryInfo
import com.awesome.appstore.util.StorePreference
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.math.round


class TabActivityViewModel @Inject constructor(
    private val repository: Repository,
    private val preference: StorePreference,
    private val fileDirectoryInfo: FileDirectoryInfo
) : ViewModel() {
    init {
        badgeRefresh()
        getSettings()
    }

    private val _goNotice: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _sideNavOpen: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _setPattern: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _setPin: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _badge1: MutableLiveData<Int> = MutableLiveData(0)
    private val _badge2: MutableLiveData<Int> = MutableLiveData(0)
    private val _badge3: MutableLiveData<Int> = MutableLiveData(0)
    private val _badge4: MutableLiveData<Int> = MutableLiveData(0)
    private val _badgeNotice: MutableLiveData<Int> = MutableLiveData(0)
    private val _allAppList: MutableLiveData<List<AppInfo?>> = MutableLiveData()
    private val _goSetting: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val _goLogout: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val _tokenExpireDialog: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val _userProfile: MutableLiveData<Profile> = MutableLiveData()
    private val _title: MutableLiveData<String> = MutableLiveData()
    private val _storeInfo: MutableLiveData<Map<String, Any>> = MutableLiveData()
    private val _storeDownloadProgress: MutableLiveData<Int> = MutableLiveData<Int>()
    private val _storeDownloadComplete: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _toastMsg: MutableLiveData<String> = MutableLiveData()

    var toastMsg: LiveData<String> = _toastMsg
    var storeDownloadComplete: LiveData<Event<Boolean>> = _storeDownloadComplete
    var storeDownloadProgress: LiveData<Int> = _storeDownloadProgress
    var storeInfo: LiveData<Map<String, Any>> = _storeInfo
    var title: LiveData<String> = _title
    var userProfile: LiveData<Profile> = _userProfile
    var tokenExpireDialog: LiveData<Event<Boolean>> = _tokenExpireDialog
    var goLogout: LiveData<Event<Boolean>> = _goLogout
    var goSetting: LiveData<Event<Boolean>> = _goSetting
    var allApplist: LiveData<List<AppInfo?>> = _allAppList
    var sideNavOpen: LiveData<Event<Boolean>> = _sideNavOpen
    var goNotice: LiveData<Event<Boolean>> = _goNotice
    var setPattern: LiveData<Event<Boolean>> = _setPattern
    var setPin: LiveData<Event<Boolean>> = _setPin

    //탭 1,2,3,4번 상단 뱃지의 값
    var badge1: LiveData<Int> = _badge1 // todo push 앱에서 브로드 캐스트로 넘겨줄 예정
    var badge2: LiveData<Int> = _badge2// 미설치된 앱 총 개수
    var badge3: LiveData<Int> = _badge3 // 미설치된 필수 앱 개수
    var badge4: LiveData<Int> = _badge4 // 업데이트 필요한 앱 총 개수
    var badge5: LiveData<Int> = _badgeNotice // 업데이트 필요한 앱 총 개수

    var token = preference.loadTokenPreference()

    fun badgeRefresh() {
        viewModelScope.launch {
            updateBadge1()
            updateBadge2()
            updateBadge3()
            updateBadge4()
        }
    }

    suspend fun updateBadge1() {
        var count = 0
        repository.selectAllAppInfo().data?.forEach {
            if (it?.installStatus.equals("Y")) {
                it!!.packageName.let { pkgName ->
                    count += repository.getPushCount(pkgName).data?.count ?: 0
                }
            }
        }
//        repository.getAllPushCount().data?.forEach {
//            count += it?.count ?: 0
//        }
        _badge1.postValue(count)
    }

    suspend fun updateBadge2() {
        var count = 0
        val appList = repository.selectAllAppInfo().data
        appList?.forEach {
            if (it?.installStatus.equals("N") && it?.appStatus == "A") {
                count++
            }
        }
        Logger.d(count)
        _badge2.postValue(count)
        _allAppList.postValue(appList)
    }

    suspend fun updateBadge3() {
        var count = 0
        repository.selectAllAppInfo().data?.forEach {
            if (it?.installStatus.equals("N") && it?.releaseType.equals("M") && it?.appStatus == "A") {
                count++
            }
        }
        Logger.d(count)
        _badge3.postValue(count)
    }

    suspend fun updateBadge4() {
        var count = 0
        repository.selectUpdateAppInfo().data?.forEach {
            if (it?.updateStatus.equals("Y") && it?.appStatus == "A") {
                count++
            }
        }
        Logger.d(count)
        _badge4.postValue(count)
    }

    fun sideNavOpen() {
        _sideNavOpen.value = Event(true)
    }

    fun goNotice() {
        _goNotice.value = Event(true)
    }

    //에러리포트 전송 테스트용 메소드
    fun testError() {
        val handler = Handler()
        handler.post { throw RuntimeException() }
    }

    //로그아웃
    fun onClickLogout() {
//        preference.saveIdSavePreference("")
//        preference.saveTokenPreference("")
        _goLogout.value = Event(true)
    }

    fun setPatternLock() {
        _setPattern.value = Event(true)
    }

    fun setPinLock() {
        _setPin.value = Event(true)
    }

    fun onClickSetting() {
        _goSetting.value = Event(true)
    }

    fun getSettings() {
        viewModelScope.launch {
            val result = repository.getSettings()
            when (result.networkStatus) {
                NetworkStatus.SUCCESS -> {

                    val data = result.data?.data

                    data?.store?.let { _storeInfo.postValue(it) }

                    if (data?.lockScreen == "Y") {
//                        preference.saveLockScreenSetting("1")
                        preference.saveLockScreenEnable("1")
                    } else if (data?.lockScreen == "N") {
                        preference.saveLockScreenSetting("0")
                        preference.saveLockScreenEnable("0")
                    }

                    if (data?.push?.isUse == "Y")
                        preference.savePushSetting("1")
                    else if (data?.push?.isUse == "N")
                        preference.savePushSetting("0")

                    _userProfile.postValue(result.data?.data?.profile)
                    Logger.d(result.data?.data)

                    _title.postValue(result.data?.data?.theme?.title)

                    _badgeNotice.postValue(result.data?.data?.unreadCount ?: 0)
                }
                NetworkStatus.ERROR -> {
                    if (result.message == StringTAG.ERROR_CODE_401) {
                        _tokenExpireDialog.postValue(Event(true))
                    }
                    Logger.d("fail to load user settings")
                }
            }
        }
    }

    fun storeUpdate() {

        var id = PRDownloader.download(
            storeInfo.value?.get("downloadUrl").toString(),
            fileDirectoryInfo.internalDownLoads,
            "${storeInfo.value?.get("packageName").toString()}.apk"
        )
            .setHeader("Authorization", "Bearer $token")
            .setHeader("Accept", "application/json")
            .build()
            .setOnProgressListener {
                _storeDownloadProgress.value = round(
                    (it.currentBytes.toDouble().div(it.totalBytes.toDouble())).times(100)
                ).toInt()
            }
            .setOnCancelListener {
                Logger.d("OnCancel")
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    Logger.d(fileDirectoryInfo.internalDownLoads)
                    Logger.d(
                        "onDownloadComplete ==> file isExist? : ${
                            File(
                                fileDirectoryInfo.internalDownLoads + "/${
                                    storeInfo.value?.get("packageName").toString()
                                }.apk"
                            ).exists()
                        }"
                    )
                    _storeDownloadComplete.value = Event(true)
                }

                override fun onError(error: com.downloader.Error?) {
                    Logger.d(error?.serverErrorMessage)
                    _toastMsg.postValue("다운로드중 문제가 발생했습니다. 잠시후 다시 시도해 주세요.")

                }
            })
    }

}