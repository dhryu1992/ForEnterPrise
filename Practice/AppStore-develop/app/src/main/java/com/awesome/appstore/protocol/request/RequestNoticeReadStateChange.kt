package com.awesome.appstore.protocol.request

data class RequestNoticeReadStateChange(
    val data: RequestNoticeRead
) {
    data class RequestNoticeRead(
        val noticeId: Long
    )
}

