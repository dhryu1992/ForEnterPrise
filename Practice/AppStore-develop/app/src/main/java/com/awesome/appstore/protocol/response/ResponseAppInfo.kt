package com.awesome.appstore.protocol.response

import com.awesome.appstore.database.AppInfo

data class ResponseAppInfo(
    val common: Common,
    val data: AppInfoList
){
    data class AppInfoList(
        val apps:List<AppInfo>?
    )
}

