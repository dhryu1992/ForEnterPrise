package com.awesome.appstore.protocol.response

import com.awesome.appstore.database.AppInfo
import com.awesome.appstore.database.Notice

data class ResponseNotice(
    val common: Common,
    val data: NoticeList
){
    data class NoticeList(
        val notice:List<Notice?>,
        val unreadCount:Int,
        val paging: Paging
    )
}

