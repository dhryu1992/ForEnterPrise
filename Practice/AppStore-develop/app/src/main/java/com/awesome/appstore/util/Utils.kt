package com.awesome.appstore.util

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import com.orhanobut.logger.Logger
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class Utils {
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            Logger.e(e.message.toString())
            null
        }
    }

    fun isRootedDevice(context: Context): Boolean {
        var rootedDevice = false
        val buildTags = Build.TAGS
//        테스트 키 체크
        if (buildTags != null && buildTags.contains("test-keys")) {
            Logger.e("Root Detected 1")
            rootedDevice = true
        }

        // 수퍼유저 앱 체크
        try {
            val file = File("/system/app/Superuser.apk")
            if (file.exists()) {
                Logger.e("Root Detected 2")
                rootedDevice = true
            }
        } catch (e1: Throwable) {}

        //su 체크
        try {
            Runtime.getRuntime().exec("su")
            Logger.e("Root Detected 3")
            rootedDevice = true
        } catch (localIOException: IOException) {}

//        루팅 어플인 busybox 설치 여부 체크
        val packageName = "stericson.busybox"
        val pm: PackageManager = context.packageManager
        try {
            Logger.e("Root Detected 4")
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            rootedDevice = true
        } catch (e: PackageManager.NameNotFoundException) {}
        return rootedDevice
    }
}