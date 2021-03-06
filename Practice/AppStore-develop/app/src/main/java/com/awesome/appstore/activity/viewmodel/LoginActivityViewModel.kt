package com.awesome.appstore.activity.viewmodel

import android.os.RemoteException
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesome.appstore.BuildConfig
import com.awesome.appstore.config.StoreConfig.Companion.MDM_ENABLE
import com.awesome.appstore.config.StoreConfig.Companion.SSLVPN_ENABLE
import com.awesome.appstore.config.StoreConfig.Companion.SSLVPN_PATH
import com.awesome.appstore.config.StoreConfig.Companion.SSLVPN_SERVER_ADDRESS
import com.awesome.appstore.config.StoreConfig.Companion.V3_ENABLE
import com.awesome.appstore.config.StringTAG.Companion.MDM
import com.awesome.appstore.config.StringTAG.Companion.SSLVPN
import com.awesome.appstore.config.StringTAG.Companion.SSLVPN_SERVICE
import com.awesome.appstore.config.StringTAG.Companion.V3_MOBILE
import com.awesome.appstore.database.DatabaseStatus
import com.awesome.appstore.event.Event
import com.awesome.appstore.network.NetworkStatus
import com.awesome.appstore.repository.Repository
import com.awesome.appstore.security.mdm.MDMHelper
import com.awesome.appstore.security.sslvpn.SecuwaySSLHelper
import com.awesome.appstore.util.DateUtil
import com.awesome.appstore.util.PackageUtil
import com.awesome.appstore.util.StorePreference
import com.orhanobut.logger.Logger
import com.scottyab.rootbeer.Const
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.secuwiz.SecuwaySSL.Api.MobileApi
import javax.inject.Inject

class LoginActivityViewModel @Inject constructor(
    private val repository: Repository,
    private val preference: StorePreference,
    private val packageUtil: PackageUtil,
    private val secuwaySSLHelper: SecuwaySSLHelper,
    private val mdmHelper: MDMHelper
) : ViewModel() {
    var userId = MutableLiveData<String>()
    var userPassword = MutableLiveData<String>()
    var sslvpnPassword = MutableLiveData<String>()
    lateinit var requestErrorMessage: String

    private val _showUserIdToast: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _showPasswordToast: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _showSSLVPNPassword: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _showRequestMessage: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _startTabActivity: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _showLoadingProgress: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _showAppInstallDialog: MutableLiveData<String> = MutableLiveData<String>("")

    var showAppInstallDialog: LiveData<String> = _showAppInstallDialog
    var showLoadingProgress: LiveData<Event<Boolean>> = _showLoadingProgress
    var startTabActivity: LiveData<Event<Boolean>> = _startTabActivity
    var showUserIdToast: LiveData<Event<Boolean>> = _showUserIdToast
    var showPasswordToast: LiveData<Event<Boolean>> = _showPasswordToast
    var showSSLVPNPassword: LiveData<Event<Boolean>> = _showSSLVPNPassword
    var showRequestMessage: LiveData<Event<Boolean>> = _showRequestMessage

    var isLinearFadIn: LiveData<Boolean> = MutableLiveData(false)
    var isLinearFadOut: LiveData<Boolean> = MutableLiveData(false)
    var showProgressDialog: LiveData<Boolean> = MutableLiveData(false)

    init {
        if (BuildConfig.DEBUG) {
            userId.setValue("A1010")
            userPassword.setValue("aaaaaaaaaaaaa")
            sslvpnPassword.setValue("a123456789")
        }
        isLinearFadOut = MutableLiveData(true)
        isLinearFadIn = MutableLiveData(true)
    }

    fun loginClick() {
        Logger.d("loginClick userID: " + userId.value)
        Logger.d("loginClick userPassword: " + userPassword.value)
        Logger.d("loginClick sslvpnPassword: " + sslvpnPassword.value)

        if (TextUtils.isEmpty(userId.value)) {
            _showUserIdToast.value = Event(true)
            return
        }
        if (TextUtils.isEmpty(userPassword.value)) {
            _showPasswordToast.value = Event(true)
            return
        }
        if (TextUtils.isEmpty(sslvpnPassword.value)) {
            _showSSLVPNPassword.value = Event(true)
            return
        }

        _showLoadingProgress.value = Event(true)

        viewModelScope.launch(Dispatchers.Default) {
            val responseUserAuth =
                repository.getUserAuth(userId.value.toString(), userPassword.value.toString())
            when (responseUserAuth.networkStatus) {
                NetworkStatus.SUCCESS -> {
                    Logger.d(responseUserAuth.data.toString())
                    preference.saveTokenPreference(responseUserAuth.data?.data?.token)
                    preference.saveIdSavePreference(userId.value)
                    preference.saveExpiredTime(DateUtil().getTimestampToDate(responseUserAuth.data?.data?.expiredTime!!).toString())
                    val appInfoList = repository.getAppList()
                    Logger.d(appInfoList)
                    appInfoList.data?.data?.apps?.forEach {
//              ???????????? ???????????? ?????? ???????????? ???????????? ??????
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
                        val result = repository.insertAppInfo(it)
                        when (result.databaseStatus) {
                            DatabaseStatus.SUCCESS -> {
                                Logger.d(result.data)
//                                _startTabActivity.postValue(Event(true))
                            }
                            DatabaseStatus.ERROR -> Logger.d(result.message)
                            DatabaseStatus.LOADING -> Logger.d(result.message)
                        }
                    }
                    repository.selectAllAppInfo().data?.forEach {
                        if (!appInfoList.data?.data?.apps?.contains(it)!!){
                            repository.insertAppInfo(it!!.apply { appStatus = "D" })
                        }
                    }
                    _startTabActivity.postValue(Event(true))
                }
                NetworkStatus.ERROR -> {
                    _showRequestMessage.postValue(Event(true))
                    requestErrorMessage = responseUserAuth.message.toString()
                    Logger.d(responseUserAuth.message.toString())
                }
            }
        }
    }

    fun checkInstallV3() {
        if (packageUtil.isInstallPackage(V3_MOBILE)) {
            Logger.d("V3 is installed")
            checkInstallMdm()
        } else {
            Logger.d("V3 is not installed")
            if (V3_ENABLE) _showAppInstallDialog.value = V3_MOBILE
            else checkInstallMdm()
        }
    }

    private fun checkInstallMdm() {
        if (packageUtil.isInstallPackage(MDM)) {
            Logger.d("mdm is installed")
            checkInstallSSLVPN()
        } else {
            Logger.d("mdm is not installed")
            if (MDM_ENABLE) _showAppInstallDialog.value = MDM
            else checkInstallSSLVPN()

        }
    }

    private fun checkInstallSSLVPN() {
        if (packageUtil.isInstallPackage(SSLVPN)) {
            Logger.d("SSLVPN is installed")
            checkInstallSSLVPNMobile()
        } else {
            Logger.d("SSLVPN is not installed")
            if (SSLVPN_ENABLE) _showAppInstallDialog.value = SSLVPN
        }
    }

    private fun checkInstallSSLVPNMobile() {
        if (packageUtil.isInstallPackage(SSLVPN_SERVICE)) {
            Logger.d("SecuwaySSL is installed")
        } else {
            Logger.d("SecuwaySSL is not installed")
            if (SSLVPN_ENABLE) _showAppInstallDialog.value = SSLVPN_SERVICE
        }
    }

    fun startVPN() {
        viewModelScope.launch {
            var strResult = "error"
            val intnCertType = 12 //11 : NPKI, 12 : SecuwaySSL ????????? (defult : 12)

            if (secuwaySSLHelper.secuwayService != null) {
                val objAidl = MobileApi.Stub.asInterface(secuwaySSLHelper.secuwayService)
                try {
                    //?????????, ????????? ??????, ????????? ??????????????? ???????????? vpn ??????
                    //	strResult = objAidl.StartVpn(strAddr, "test1", "1234qwer!", 1);
                    objAidl.CertLogin(SSLVPN_SERVER_ADDRESS, userId.value, SSLVPN_PATH, sslvpnPassword.value, intnCertType)
                } catch (e: RemoteException) {
                    "?????? ?????? ????????? ?????????????????????. (RemoteException) ${e.stackTrace}"
                }.also { strResult = it }

                if (strResult == "0") {
                    //"0" ??? ???????????? ?????? ?????? - main thread ??? ????????? ????????? ?????? ????????? ??????????????? ????????? ??????.
                    Logger.d("VPN ????????? ??????????????????.")
                    checkVPN()
                } else {
                    Logger.d(strResult)
                }
            }

        }
    }

    fun checkVPN() {
        //bindService ??? ??????????????? ?????? onServiceConnected() ?????? ????????? ?????? ??????
        //???????????? ?????? ???????????? vpn ?????????

        if (secuwaySSLHelper.secuwayService != null) {
            var nStatus = 100
            val objAidl = MobileApi.Stub.asInterface(secuwaySSLHelper.secuwayService)
            try {
                nStatus = objAidl.VpnStatus()

                //nStatus 0 ?????? ?????? ??????
                //nStatus 1 ?????? ?????????
                //nStatus 2 ??? ?????? ???
                //vpn ????????? 0 ?????? ???????????? 2??? ??????????????? 1??? ????????? ????????? ??? ?????????
                Logger.d("VPN =======> status: $nStatus")
                if (nStatus == 1) {
                    Logger.d("VPN ???????????? ?????????????????????.")
                    _startTabActivity.postValue(Event(true))
                    return
                } else if (nStatus == 2 || nStatus == 0) {
                    Logger.d("VPN =======> ?????? ??????")
                    Thread.sleep(3000)
                    return checkVPN()
                } else {
                    Logger.d(" status Error")
                    Logger.d("VPN ???????????? ?????????????????????.")
                    return
                }
            } catch (e: RemoteException) {
                Logger.d("Exception", e)
                return
            } catch (e: InterruptedException) {
                Logger.d("Exception", e)
                return
            }
        } else {
            Logger.d("VPN ???????????? ?????????????????????.")
            return
        }
    }

    fun vpnStop() {
        try {
            secuwaySSLHelper.vpnConnectionStop()
//            unbindService(secuwaySSLHelper.mConnection)
        } catch (e: Exception) {
            Logger.d("launcher3 unbindService :", e.toString())
        }
    }
}


