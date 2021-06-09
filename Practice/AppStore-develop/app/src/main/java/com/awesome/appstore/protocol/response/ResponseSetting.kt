package com.awesome.appstore.protocol.response

import android.content.res.Resources
import android.provider.ContactsContract
import com.awesome.appstore.database.AppInfo
import com.awesome.appstore.protocol.request.Push


data class ResponseSetting(
    val common: Common,
    val data: Settings
)

data class Settings(
    val push: Push,
    val lockScreen: String,
    val crash: String,
    val profile: Profile,
    val theme: Theme,
    val unreadCount: Int,
    val store: Map<String, Any>
)

data class Profile(
    val account: String,
    val name: String,
    val departmentName: String,
    val departmentCode: String,
    val profileImg: String,
    val email: String
)

data class Theme(
    val title: String
)


