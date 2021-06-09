package com.awesome.appstore.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class PushCount(
    @PrimaryKey
    val packageName: String,
    var count: Int = 0,
) : Parcelable