package com.awesome.appstore.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.awesome.appstore.BuildConfig
import com.awesome.appstore.config.StoreConst
import com.orhanobut.logger.Logger
import java.io.*
import java.security.MessageDigest
import java.util.*


class PackageUtil(private val context: Context) {
    fun getVersionName(): String {
        var versionName = ""
        try {
            val pi = context.packageManager.getPackageInfo(context.packageName, 0)
            versionName = pi.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionName
    }

    /**
     * 단말기에 패키지의 설치여부를 검색하여 리턴한다.
     * @param context
     * @param packageName
     * @return true(설치됨), false(설치안됨)
     */
    fun findInstalledPackage(packageName: String): Boolean {
        val manager = context.packageManager
        val apps = manager.getInstalledPackages(0)
        if (apps != null) {
            val appSize = apps.size
            for (i in 0 until appSize) {
                if (apps[i].packageName == packageName) {
                    return true
                }
            }
        }
        return false
    }

    fun isInstallPackage(pkg: String?): Boolean {
        return try {
            context.packageManager.getPackageInfo(pkg!!, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            Logger.d(e.message)
            false
        }
    }

    /**
     * 설치되어 있는 app중에 조건에 맞는 app이 있는지 찾는다.
     *
     * @param context context 객체
     * @param packageName 찾을 app의 패키지 명
     * @param intentAction 찾을 app이 가지고 있는 intent action
     * @return 설치되어 있으면 true, 아니면 false
     */
    fun findInstalledPackage(
        packageName: String,
        intentAction: String?
    ): Boolean {
        val manager = context.packageManager
        val mainIntent = Intent(intentAction, null)
        val apps = manager.queryIntentActivities(mainIntent, 0)
        Collections.sort(apps, ResolveInfo.DisplayNameComparator(manager))
        if (apps != null) {
            val count = apps.size
            for (i in 0 until count) {
                val info = apps[i]
                if (info.activityInfo.applicationInfo.packageName == packageName) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 단말기에 설치된 패키지의 정보를 검색하여 결과값을 리턴한다.
     * 름
     * @return PackageInfo : 설치된 패키지일 경우 리턴값. 없을 경우 null을 리턴한다.
     */
    fun getInstalledPackageInfo(pkgName: String): PackageInfo? {
        val installedList = context.packageManager
            .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES)
        val installedListSize = installedList.size
        for (i in 0 until installedListSize) {
            Logger.d("installedList :${installedList[i]}")
            val tmp = installedList[i]
            if (pkgName.equals(tmp.packageName, ignoreCase = true)) {
                return tmp
            }
        }
        return null
    }

    fun startApplication(packageName: String): Boolean {
        val pm = context.packageManager
        var intent: Intent? = Intent()
        if (pm != null) {
            Logger.d("packageName :$packageName")
            Logger.d("startApplication")
            intent!!.action = "$packageName.action.MAIN"
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                intent = pm.getLaunchIntentForPackage(packageName)
                try {
                    context.startActivity(intent)
                } catch (e2: Exception) {
                    return false
                }
            }
        } else {
            return false
        }
        return true
    }

    fun startInstallPackage(activity: Activity, fileName: String?, isStore: Boolean) {
        startInstallPackage(activity, fileName, StoreConst.REQUESTCODE_UPGRADE, isStore)

    }

    fun startInstallPackage(
        activity: Activity,
        fileName: String?,
        resultCode: Int,
        isPES: Boolean
    ) {
        val file = File(
            context.filesDir.absolutePath + "/${Environment.DIRECTORY_DOWNLOADS}"
                    + "$fileName"
        )
        val fileUri = FileProvider.getUriForFile(
            activity,
            BuildConfig.APPLICATION_ID.toString() + ".fileprovider",
            file
        )
        val installIntent = Intent(Intent.ACTION_VIEW)
        installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        installIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        installIntent.setDataAndType(fileUri, "application/vnd.android.package-archive")
        Logger.d("fileInstallStart")
        if (isPES) activity.startActivity(installIntent) else activity.startActivityForResult(
            installIntent,
            resultCode
        )
    }

    fun isPackageMatch(packageName: String, filePath: String?): Boolean {
        val name = getPackageName(filePath)
        return if (name == packageName) true else false
    }

    fun getPackageName(filePath: String?): String {
        val pm = context.packageManager
        val info = pm.getPackageArchiveInfo(filePath!!, PackageManager.GET_SIGNATURES)
        var name = ""
        if (info != null) {
            name = info.packageName
        }
        return name
    }

    fun getInstalledPackageList(): List<PackageInfo> {
        val manager = context.packageManager
        return manager.getInstalledPackages(0)
    }

    fun getInstalledPackages(): List<ResolveInfo> {
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        return context.packageManager.queryIntentActivities(mainIntent, 0)
    }

    fun copyToSDCard(filename: String?): Boolean {
        val assetManager = context.assets
        var `is`: InputStream? = null
        var file: File? = null
        try {
            `is` = assetManager.open(filename!!)
            val SDCardRoot = Environment.getExternalStorageDirectory()
            if (!SDCardRoot.canWrite()) {
                return false
            }
            file = File(SDCardRoot, filename)
            var fos: FileOutputStream? = null
            var bos: BufferedOutputStream? = null
            val bis = BufferedInputStream(`is`)

            // 만약에 파일이 있다면 지우고 다시 생성
            if (file.exists()) {
                file.delete()
                file.createNewFile()
            }
            fos = FileOutputStream(file)
            bos = BufferedOutputStream(fos)
            var read = -1
            val buffer = ByteArray(1024)
            while (bis.read(buffer, 0, 1024).also { read = it } != -1) {
                bos.write(buffer, 0, read)
            }
            bos.flush()
            fos.close()
            bos.close()
            `is`.close()
            bis.close()
            Log.i("djshin == ", Uri.fromFile(file).path!!)
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } finally {
            try {
                `is`!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }
        }
        return true
    }

    fun checkVersion(localVer: String, remoteVer: String): Int {
        val components1 = localVer.split("\\.").toTypedArray()
        val components2 = remoteVer.split("\\.").toTypedArray()
        val length = components1.size.coerceAtMost(components2.size)
        for (i in 0 until length) {
            val result = components1[i].toInt().compareTo(components2[i].toInt())
            if (result != 0) {
                return result
            }
        }
        return 0
    }

    fun setBadge(count: Int) {
        val launcherClassName = getLauncherClassName() ?: return
        val intent = Intent("android.intent.action.BADGE_COUNT_UPDATE")
        intent.putExtra("badge_count", count)
        intent.putExtra("badge_count_package_name", context.packageName)
        intent.putExtra("badge_count_class_name", launcherClassName)
        context.sendBroadcast(intent)
    }

    fun getLauncherClassName(): String? {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        for (resolveInfo in resolveInfos) {
            val pkgName = resolveInfo.activityInfo.applicationInfo.packageName
            if (pkgName.equals(context.packageName, ignoreCase = true)) {
                return resolveInfo.activityInfo.name
            }
        }
        return null
    }

    fun getPackageNameHashString(packageName: String): String {
        val packageInfo =
            context.packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
        val appPath = packageInfo.applicationInfo.sourceDir;
        Logger.d(appPath)
        val fileInputStream = FileInputStream(appPath)
        val messageDigest = MessageDigest.getInstance("SHA")
        messageDigest.reset()
        val bytes = ByteArray(8192)
        while (true) {
            val i = fileInputStream.read(bytes)
            if (i == -1) break;
            messageDigest.update(bytes)
        }
        val crcBuffer=messageDigest.digest()

        val sb: StringBuffer = StringBuffer(crcBuffer.size * 2)

        for (i in 0 until crcBuffer.size) {
            val hexChar = StringBuilder("0").append(Integer.toHexString(crcBuffer[i].toInt())).toString()
            sb.append(hexChar.substring(hexChar.length - 2))
        }
        return sb.toString().toUpperCase(Locale.getDefault())
    }

    private fun hashString(input: String, algorithm: String): String {
        return MessageDigest
            .getInstance(algorithm)
            .digest(input.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
    }
}



