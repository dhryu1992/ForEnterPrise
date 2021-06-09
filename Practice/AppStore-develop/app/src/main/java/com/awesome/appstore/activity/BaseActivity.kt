package com.awesome.appstore.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.master.permissionhelper.PermissionHelper
import com.orhanobut.logger.Logger
import dagger.android.support.DaggerAppCompatActivity
import kotlin.system.exitProcess

open class BaseActivity : DaggerAppCompatActivity() {

    private lateinit var permissionHelper: PermissionHelper
    val permissionList = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        arrayOf(
            Manifest.permission.QUERY_ALL_PACKAGES,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    } else {
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    }

    @SuppressLint("BatteryLife")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d("BaseActivity-onCreate")
        Logger.d("BaseActivity-onCreate")
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName))
            startActivityForResult(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply { data = Uri.parse("package:$packageName") }, 119)
        checkPermission()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 119 ){
            if(resultCode != -1){
                Toast.makeText(this, "앱 사용을 위해 배터리 권한이 필요 합니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    fun tokenExpireDialog() {
        MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setMessage("세션이 만료되어 재로그인이 필요합니다.")
            .setPositiveButton("확인", DialogInterface.OnClickListener { _, _ ->
                val i = packageManager.getLaunchIntentForPackage(packageName);
                val componentName = i?.component
                startActivity(Intent.makeRestartActivityTask(componentName));
                exitProcess(0);
            })
            .show()
    }

    private fun checkPermission() {
        permissionHelper = PermissionHelper(
            this, permissionList, 100
        )
        permissionHelper.request(object : PermissionHelper.PermissionCallback {
            override fun onPermissionGranted() {
                Logger.d("onPermissionGranted() called")
            }

            override fun onIndividualPermissionGranted(grantedPermission: Array<String>) {
                Logger.d(
                    "onIndividualPermissionGranted() called with: grantedPermission = [" + TextUtils.join(
                        ",",
                        grantedPermission
                    ) + "]"
                )
            }

            override fun onPermissionDenied() {
//                    Logger.d("onPermissionDenied() called");
//                    ConfirmMsgDialog confirmMsgDialog = new ConfirmMsgDialog(mContext, getString(R.string.permission_deny_info), new DialogInterface.OnDismissListener() {
//                        @Override
//                        public void onDismiss(DialogInterface dialog) {
//                            finish();
//                        }
//                    });
            }

            override fun onPermissionDeniedBySystem() {
                Logger.d("onPermissionDeniedBySystem() called")
            }
        })
    }
}