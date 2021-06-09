package com.awesome.appstore.security.mdm

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.widget.Toast
import com.awesome.appstore.config.StringTAG.Companion.MDM
import com.awesome.appstore.util.PackageUtil
import com.gaia.mobikit.android.api.MdmApi
import com.gaia.mobikit.android.api.MdmApiHelper
import com.orhanobut.logger.Logger
import com.scottyab.rootbeer.Const

class MDMHelper {
    var isRunning = false
    var mdmApi: MdmApi? = null

    private var activity: Activity? = null
    private var context: Context? = null

    private fun MDMHelper() {
        Logger.d("MDMHelper constructor")
    }

    var mdmActivityServiceConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {}
        override fun onServiceDisconnected(name: ComponentName) {}
    }

    var mdmServiceConn: ServiceConnection = object : ServiceConnection {
        var status = MdmApiHelper.STATUS_UNINSTALLED
        override fun onServiceDisconnected(name: ComponentName) {
            mdmApi = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mdmApi = MdmApi.Stub.asInterface(service)
            try {
                val mdmStatus = mdmApi!!.getRichStatus(activity!!.packageName)
                status = mdmApi!!.getStatus(activity!!.packageName)
                val rooted = mdmStatus!!.isRootedDevice
                if (rooted) {
                    Toast.makeText(context, "루팅된 시스템에서는 실행하실 수 없습니다.", Toast.LENGTH_SHORT).show()
                    activity!!.finish()
                } else {
                    if (status == MdmApiHelper.STATUS_UNREGISTERED) {
                        // 로그인이 안되어 있으면 MDM을 구동시켜 로그인 하도록 한다.
                        Toast.makeText(context, "MDM 로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                        val LaunchIntent = activity!!.packageManager.getLaunchIntentForPackage(MdmApiHelper.PACKAGE_NAME)
                        activity!!.startActivity(LaunchIntent)
                        activity!!.finish()
                    }
                    if (status != MdmApiHelper.STATUS_FINE) {
                        Toast.makeText(context, "MDM 상태가 적절하지 않습니다.", Toast.LENGTH_SHORT).show()
                        activity!!.finish()
                    }
                }
            } catch (e: IllegalArgumentException) {
                Toast.makeText(context, "MDM 과 통신에 오류가 있습니다.\n최신 MDM 인지 확인해 주시기 바랍니다.", Toast.LENGTH_SHORT).show()
                activity!!.finish()
            } catch (e: Exception) {
                Toast.makeText(context, "MDM 과 통신에 오류가 있습니다.\n최신 MDM 인지 확인해 주시기 바랍니다.", Toast.LENGTH_SHORT).show()
                activity!!.finish()
            }
        }
    }

    fun getMDMStatus(packageName: String?): Boolean {
        var mdmStatus = 0
        return try {
            mdmStatus = mdmApi!!.getStatus(packageName)
            mdmStatus == 2
        } catch (e: RemoteException) {
            false
        }
    }

    fun startMDM() {
        try {
            if (!this.isRunning && this.mdmApi != null) {
                this.mdmApi!!.enterWorkApp("com.awesome.appstore")
                this.isRunning = true
                Logger.d("start mdm")
            }
        } catch (e: Exception) {
            Logger.d("startMDM Exception :" + e.message)
        }
    }

    fun stopMDM() {
        try {
            this.mdmApi!!.exitWorkApp("com.awesome.appstore")
            this.isRunning = false
            Logger.d("stop mdm")
        } catch (e: Exception) {
            Logger.d("stopMDM Exception :" + e.message)
        }
    }

    fun checkMdmStatus(context: Context, activity: Activity?): Boolean {
        this.context = context
        this.activity = activity
        if (PackageUtil(context).isInstallPackage(MDM)) {
            val i = Intent(MdmApiHelper.ACTION_MDM_API)
            i.setPackage(MdmApiHelper.PACKAGE_NAME)
            context.bindService(i, mdmServiceConn, Service.BIND_AUTO_CREATE)
        }
        return true
    }

    fun checkMdmStatusNoActivity(context: Context): Boolean {
        this.context = context
        if (PackageUtil(context).isInstallPackage(MDM)) {
            val i = Intent(MdmApiHelper.ACTION_MDM_API)
            i.setPackage(MdmApiHelper.PACKAGE_NAME)
            context.bindService(i, mdmServiceConn, Service.BIND_AUTO_CREATE)
        }
        return true
    }
}