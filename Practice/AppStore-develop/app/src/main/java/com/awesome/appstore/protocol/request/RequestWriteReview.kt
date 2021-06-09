package com.awesome.appstore.protocol.request

data class RequestWriteReview(
    var data: Review?
) {
    data class Review(
        val appId: Long,
        val subject: String,
        val content: String,
        val starPoint: Float
    )
}

