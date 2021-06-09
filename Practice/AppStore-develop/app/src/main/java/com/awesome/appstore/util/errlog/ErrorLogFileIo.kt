package com.awesome.appstore.util.errlog

import android.content.Context
import com.orhanobut.logger.Logger
import java.io.*

class ErrorLogFileIo {
    fun writeErrLog(title: String, data: String?, context: Context) {
        try {
            val outputStreamWriter = OutputStreamWriter(
                context.openFileOutput(
                    "$title.txt", Context.MODE_PRIVATE
                )
            )
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Logger.e("Exception", "File write failed: ${e.message}")
        }
    }

    fun readErrLog(title: String, context: Context): String? {
        var ret = ""
        try {
            val inputStream: InputStream? = context.openFileInput("$title.txt")
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = ""
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
}