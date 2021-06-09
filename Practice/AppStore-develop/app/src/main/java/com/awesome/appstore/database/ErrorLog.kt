package com.awesome.appstore.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class ErrorLog(
    @PrimaryKey
    val reportId: String,

    var appVersionCode: String = "",

    var androidVersion: String = "",

    var phoneModel: String = "",

    var userCrashDate: String = "",

    var stackTrace: String = "",

    var filePath: String = "",

    var check: Int? = 0
) : Parcelable