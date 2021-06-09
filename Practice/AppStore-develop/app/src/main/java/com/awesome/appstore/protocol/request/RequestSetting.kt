package com.awesome.appstore.protocol.request

data class RequestSetting(
    val data: Push
)
data class Push(
    val isUse: String = "Y",
    val type: String? = null,
    val key: String? = null
)