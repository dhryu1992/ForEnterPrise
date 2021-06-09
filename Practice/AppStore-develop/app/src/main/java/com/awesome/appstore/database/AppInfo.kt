package com.awesome.appstore.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class AppInfo(
    @PrimaryKey
    val packageName: String,

    val appId: Long,

    var appName: String,

//   Android : A, iOS : I, Windows : W, Unix : U, 기타 : O
    var osType: String,

    var osVersion: String,

//    var deviceType: String = "SP",

//    var appType: String,

//    필수설치 : M, 선택설치 : O
    var releaseType: String = "O",

//    Alive : A, Dead : D, Sleep: S
    var appStatus: String = "A",

    var versionId: Long,

    var versionName: String = "",

    var versionCode: String = "",

//    알파버전 : A, 베타버전 : B, 배포버전 : R
    var versionType: String = "",

    var iconUrl: String = "",

    var downloadUrl: String = "",

//  업데이트가 필수인 앱인지 아닌 앱인지 구분 M, O
    var updateType: String = "O",
//  native
//  업데이트 존재시 Y, 아닐시 N
    var updateStatus: String = "N",

    var regDt: String = "",

    var checksum: String = "",

    var modDt: String = "",
//  native
    var installStatus: String = "N",
//  native
    var installDate: String = "",
//  native
    var updateDate: String = "",

    var descr: String = "",
//  native
    var progress: Int? = -1

) : Parcelable
