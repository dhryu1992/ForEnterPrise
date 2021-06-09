package com.showorld.data

import android.util.Log
import java.text.SimpleDateFormat


class AppConstants {
    val TAG:String = "AppConstants"

    val REQ_LOCATION_BY_ADDRESS = 101
    val REQ_WEATHER_BY_GRID = 102

    val REQ_PHOTO_CAPTURE = 103
    val REQ_PHOTO_SELECTION = 104

    val CONTENT_PHOTO = 105
    val CONTENT_PHOTO_EX = 106

    val FOLDER_PHOTO =""

    val KEY_URI_PHOTO = "URI_PHOTO"

    val DATABASE_NAME = "note.db"

    val MODE_INSERT = 1
    val MODE_MODIFY = 2

    val dateFormat = SimpleDateFormat("yyyyMMddHHmm")
    val dateFormat2 = SimpleDateFormat("yyyy-MM-dd HH시")
    val dateFormat3 = SimpleDateFormat("MM월 dd일")
    val dateFormat4 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val dateFormat5 = SimpleDateFormat("yyyy-MM-dd")


    private val handler = android.os.Handler()
    fun println(data: String) {
        handler.post(Runnable {
            run() {
                Log.d(TAG, data)
            }
        })
    }
}