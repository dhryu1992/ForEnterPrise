package com.awesomebly.template.android.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.os.StatFs
import com.awesomebly.template.android.util.config.Constants.Companion.Setting.DATE_FORMATTER
import com.awesomebly.template.android.util.config.Constants.Companion.Setting.TIME_ZONE
import com.orhanobut.logger.Logger
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.PeriodType
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : Utils
 * Date : 2021-05-03, 오후 6:12
 * History
seq   date          contents      programmer
01.   2021-05-03                   차태준
02.
03.
 */
class Utils @Inject constructor(val appContext: Context) {

    /**
     * Get current time
     * 현재 시간을 반환 한다
     * @return
     */
    fun getCurrentTime(): DateTime {
        return DateTime(TIME_ZONE)
    }

    /**
     * String date to date time
     * 문자형 형식으로된 날짜를 DateTime 형식으로 변환 한다
     * yyyy-MM-dd HH:mm:ss
     * @param TIME
     * @return
     */
    fun stringDateToDateTime(TIME: String?): DateTime? {
        return DateTime.parse(TIME, DATE_FORMATTER)
    }

    /**
     * Get current date to string
     * 현재 날짜를 문자열로 반환 한다
     * yyyy-MM-dd HH:mm:ss
     * @return
     */
    fun getCurrentDateToString(): String {
        val dateTime = DateTime(TIME_ZONE)
        return dateTime.toString(DATE_FORMATTER)
    }

    /**
     * Get diff dates
     * 입력한 두 날짜의 간격을 분으로 반환 한다
     * @param time1
     * @param time2
     * @return
     */
    fun getDiffDates(time1: DateTime?, time2: DateTime?): Int {
        val period = Period(time1, time2, PeriodType.minutes())
        return period.minutes
    }

    /**
     * Timestamp to date
     * 타임스탬프를 DateTime 형식으로 반환 한다
     * @param timestamp
     * @return
     */
    fun timestampToDate(timestamp: Long): DateTime {
        return DateTime(Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()))
    }

    /**
     * Get date day int
     * 지정한 날짜의 요일을 반환 한다
     * @param date
     * @return
     */
    fun getDateDayInt(date: DateTime): Int {
        val cal = Calendar.getInstance()
        cal.time = date.toDate()
        return cal[Calendar.DAY_OF_WEEK]
    }

    /**
     * Get bitmap from url
     * 이미지 url을 Bitmap 형식으로 반환 한다.
     * png 형식 이외에는 변환을 보증하지 않음
     *
     * @param src
     * @return
     * 변환에 실패한 경우 null을 리턴 한다
     */
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

    /**
     * Is rooted device
     * 해당 단말의 루팅 여부를 4단계로 검사 한다
     * 1. 테스트 키 체크
     * 2. 슈퍼유저 앱 체크
     * 3. su 체크
     * 4. busybox(루팅 어플) 설치 여부 체크
     *
     * @return
     * true -> 루팅
     * false -> 정상
     */
    fun isRootedDevice(): Boolean {
        var rootedDevice = false
        val buildTags = Build.TAGS
        //테스트 키 체크
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
        } catch (e1: Throwable) {
        }

        //su 체크
        try {
            Runtime.getRuntime().exec("su")
            Logger.e("Root Detected 3")
            rootedDevice = true
        } catch (localIOException: IOException) {
        }

        //루팅 어플인 busybox 설치 여부 체크
        val packageName = "stericson.busybox"
        val pm: PackageManager = appContext.packageManager
        try {
            Logger.e("Root Detected 4")
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            rootedDevice = true
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return rootedDevice
    }

    /**
     * Is network available
     * 해당 단말기가 네트워크 이용 가능 상태인지 여부를 반환 한다
     * @return
     * true -> 가능
     * false -> 불가능
     */
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }

    /**
     * External memory available
     * 외장 메모리 이용 가능 여부를 반환 한다
     * @return
     */
    fun externalMemoryAvailable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /**
     * Get total external memory size
     * 외장 메모리 총 용량을 반환 한다
     * @return
     * 외장 메모리가 이용 불가능할 경우 -1을 반환
     */
    @Suppress("DEPRECATION")
    fun getTotalExternalMemorySize(): Long {
        return if (externalMemoryAvailable()) {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val totalBlocks = stat.blockCount.toLong()
            totalBlocks * blockSize
        } else -1
    }

    /**
     * Get total internal memory size
     * 내장 메모리 총 용량을 반환 한다
     * @return
     */
    @Suppress("DEPRECATION")
    fun getTotalInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val totalBlocks = stat.blockCount.toLong()
        return totalBlocks * blockSize
    }

    /**
     * Get available internal memory size
     * 남은 내장 메모리 용량을 반환 한다
     * @return
     */
    @Suppress("DEPRECATION")
    fun getAvailableInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val availableBlocks = stat.availableBlocks.toLong()
        return availableBlocks * blockSize
    }

    /**
     * Write text
     * Internal Storage에 텍스트 파일을 저장 한다
     *
     * @param title 제목
     * @param data 내용
     */
    fun writeText(title: String, data: String?) {
        try {
            val outputStreamWriter = OutputStreamWriter(
                appContext.openFileOutput(
                    "$title.txt", Context.MODE_PRIVATE
                )
            )
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Logger.e("Exception", "File write failed: ${e.message}")
        }
    }

    /**
     * Read text
     * Internal Storage에 저장된 파일을 읽어온다
     *
     * @param title
     * 읽어올 파일의 제목
     * @return
     */
    fun readText(title: String): String {
        var ret = ""
        try {
            val inputStream: InputStream? = appContext.openFileInput("$title.txt")
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String?
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also { receiveString = it } != null) {
                    stringBuilder.append("\n").append(receiveString)
                }
                inputStream.close()
                ret = stringBuilder.toString()
            }
        } catch (e: FileNotFoundException) {
            Logger.e("login activity", "File not found: ${e.message}")
        } catch (e: IOException) {
            Logger.e("login activity", "Can not read file: ${e.message}")
        }
        return ret
    }

    /**
     * To num format
     * 숫자의 천 단위 마다 ,를 추가 한다
     * @param num
     * @return
     */
    fun toNumFormat(num: Int): String? {
        val df = DecimalFormat("#,###")
        return df.format(num.toLong())
    }
}