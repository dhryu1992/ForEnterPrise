package com.awesome.appstore.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.jetbrains.annotations.NotNull

@Entity
@Parcelize
data class LauncherIndex(
    @NotNull
    val index: Int,

    @PrimaryKey
    var packageName: String
) : Parcelable