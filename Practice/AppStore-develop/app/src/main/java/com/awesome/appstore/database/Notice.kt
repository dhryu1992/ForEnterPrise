package com.awesome.appstore.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Notice(
    @PrimaryKey(autoGenerate = true)
    val noticeId: Long,

    var subject: String = "",

    var content: String = "",

    var regDt: String = "",

    var isRead: String = "N"
) : Parcelable