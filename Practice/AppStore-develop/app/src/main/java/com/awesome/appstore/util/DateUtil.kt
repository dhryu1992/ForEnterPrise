package com.awesome.appstore.util

import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.PeriodType
import org.joda.time.format.DateTimeFormat
import java.sql.Timestamp
import java.time.*

class DateUtil {
    var fmt = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss")

    fun getStringToDateTime(loginTime: String?): DateTime? {
        return DateTime.parse(loginTime, fmt)
    }

    fun getCurrentTime(): DateTime? {
        return DateTime()
    }

    fun getCurrentDateToString(): String? {
        val dateTime = DateTime()
        return dateTime.toString(fmt)
    }

    fun getDiffDates(loginTime: DateTime?, currentDateTime: DateTime?): Int {
        val period = Period(loginTime, currentDateTime, PeriodType.minutes())
        return period.minutes
    }

    fun getTimestampToDate(timestamp: Long): LocalDateTime? {
        return Instant.ofEpochSecond(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }
}