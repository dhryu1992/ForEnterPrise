package com.awesome.appstore.protocol.request

data class RequestAppStatusChange(
    val data: StatusChange
) {
    data class StatusChange(
        val userId: String,
        val appId: Long,
        val versionId: Long,
        val feedbackType: String
    )
}

