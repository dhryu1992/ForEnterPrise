package com.awesome.appstore.security.sslvpn

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageInstaller
import android.os.IBinder
import android.os.RemoteException
import com.awesome.appstore.config.StringTAG.Companion.SSLVPN
import com.awesome.appstore.config.StringTAG.Companion.SSLVPN_SERVICE
import com.awesome.appstore.config.StringTAG.Companion.SSL_SERVICE_APK
import com.orhanobut.logger.Logger
import net.secuwiz.SecuwaySSL.Api.MobileApi
import java.net.NetworkInterface
import java.net.SocketException

class SecuwaySSLHelper {

    var secuwayService: IBinder? = null

    private val status = 0

    var isBindService = false

    private fun SecuwaySSLHelper() {
        Logger.d("SecuwaySSLHelper constructor")
    }

    var mConnection = SecuwayServiceConnection()

    inner class SecuwayServiceConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            //서비스 바인더  멤버변수로 저장
            secuwayService = service
            Logger.d("SecuwayServiceConnection connected")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            secuwayService = null
        }
    }

    fun vpnConnectionStop() {
        class StopRunnable : Runnable {
            override fun run() {
                //bindService 가 정상적으로 되고 onServiceConnected() 까지 호출된 다음 실행
                //서비스에 연결 되었으면 vpn 로그아웃
                if (secuwayService != null) {
                    val objAidl = MobileApi.Stub.asInterface(secuwayService)
                    try {
                        objAidl.StopVpn()
                    } catch (e: RemoteException) {
                    }
                }
            }
        }

        val a = StopRunnable()
        val thread = Thread(a)
        thread.start()
    }

    /**
     * 네트워크에서 DiscoReason.VPN_IP 에 해당하는 VPN 서비스 동작하는지 확인.
     *
     * @return
     */
    fun isVPNService(): Boolean {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        val address = inetAddress.hostAddress.toString()
                        if (intf.displayName.startsWith("tun")) {
                            return true
                        }
                    }
                }
            }
        } catch (ex: SocketException) {
            return false
        }
        return false
    }

//    fun onCreateInit(activity: Activity): Boolean {
//        //test
//        val secuwaySSLPackageInstaller = PackageInstaller(PackageInstaller.TYPE_SECUWAYSSL)
//        return secuwaySSLPackageInstaller.checkAndInstall(activity.applicationContext)
//    }

    fun bindService(context: Context): Boolean {
        val i = Intent(SSL_SERVICE_APK)
        i.setPackage(SSLVPN_SERVICE)
        return if (context.bindService(i, mConnection, Service.BIND_AUTO_CREATE)) {
            isBindService = true
            true
        } else {
            println("bind service error")
            false
        }
    }

    protected fun checkVPNStatus(): Boolean {
        return if (secuwayService != null) {
            var nStatus = 100
            val objAidl = MobileApi.Stub.asInterface(secuwayService)
            try {
                nStatus = objAidl.VpnStatus()
                nStatus == 1
            } catch (e: Exception) {
                Logger.d("Exception", e)
                false
            }
        } else {
            false
        }
    }

}