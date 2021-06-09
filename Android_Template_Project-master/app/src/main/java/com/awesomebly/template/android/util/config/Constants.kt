package com.awesomebly.template.android.util.config

import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.time.ZoneId
import java.util.*

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : Constants
 * Date : 2021-05-03, 오후 2:25
 * History
seq   date          contents      programmer
01.   2021-05-03                    차태준
02.
03.
 */
class Constants {
    companion object {
        object Name {
            const val DATABASE_NAME = "template.db"
            const val PREFERENCE_NAME = "template.preference"
        }

        object Action {

        }

        object ExtraKey {

        }

        object Preference {
            const val PREFERENCE_SAMPLE = "template.preference.key.PREFERENCE_SAMPLE"

        }

        object Channel {

        }

        object Setting {
            // 10MB
            const val BASE_URL = "https://jsonplaceholder.typicode.com"
            const val CACHE_SIZE = 10 * 1024 * 1024
            const val OK_HTTP_CONNECTION_COUNT = 5
            const val OK_HTTP_CONNECTION_DURATION = 60L
            const val OK_HTTP_READ_TIMEOUT = 60L
            const val OK_HTTP_WRITE_TIMEOUT = 15L
            const val OK_HTTP_CONNECTION_TIMEOUT = 60L
            val DATE_FORMATTER: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
            val TIME_ZONE: DateTimeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
        }
    }
}