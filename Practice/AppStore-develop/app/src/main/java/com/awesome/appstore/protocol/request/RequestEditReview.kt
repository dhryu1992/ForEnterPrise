package com.awesome.appstore.protocol.request

data class RequestEditReview(
    var data: Review?
) {
    data class Review(
        val reviewId: Long,
        val subject: String,
        val content: String,
        val starPoint: Double
    )
}

