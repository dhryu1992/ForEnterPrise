package com.awesome.appstore.protocol.response

import com.awesome.appstore.database.AppInfo
import com.awesome.appstore.database.Notice

data class ResponseReview(
    val common: Common,
    val data: ReviewList
)

data class ReviewList(
    val reviews: List<Review?>,
    val paging: Paging
)

data class Review(
    val reviewId: Long,
    val appId: Long,
    val subject: String,
    val content: String,
    val starPoint: Float,
    val upperReviewId: Long,
    val reviewer: Reviewer,
    val regDt: String,
    val modDt: String
)

data class Reviewer(
    val userId: Long,
    val sawonNum: String,
    val name: String,
    val email: String
)