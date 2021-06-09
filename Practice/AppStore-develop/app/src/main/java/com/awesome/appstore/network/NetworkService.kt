package com.awesome.appstore.network

import com.awesome.appstore.protocol.request.*
import com.awesome.appstore.protocol.response.*
import retrofit2.http.*

interface NetworkService {

    @GET("/v1/cde61ee2")
    suspend fun getAppListMock(): ResponseAppInfo?

    @GET("/v1/apps")
    suspend fun getAppList(@Query("page") page:Int, @Query("pageSize")pageSize: Int): ResponseAppInfo?

    @GET("/v1/3a5164fa")
    suspend fun getAppDetailContentsMock(): ResponseAppDetail?

    @GET(" /v1/apps/{appId}")
    suspend fun getAppDetailContents(@Path("appId") appId: Long): ResponseAppDetail?

    @GET("/v1/2b90c795")
    suspend fun getNoticeListMock(): ResponseNotice?

    @GET("/v1/notice")
    suspend fun getNoticeList(@Query("sort") sort:String, @Query("page") page:Int, @Query("pageSize") pageSize:Int): ResponseNotice?

    @POST("/v1/auth/login")
    suspend fun getUserAuth(@Body data: RequestUserAuth): ResponseUserAuth

    @POST("/v1/apps/feedback")
    suspend fun appStatusChange(@Body data: RequestAppStatusChange): Common

    @GET("/v1/88811de9")
    suspend fun getReviewListMock(): ResponseReview?

    @GET("/v1/reviews")
    suspend fun getReviewList(@Query("page") page:Int, @Query("pageSize") pageSize:Int, @Query("appId") appId:Long): ResponseReview?

    @POST("/v1/reviews")
    suspend fun writeReview(@Body data: RequestWriteReview): Common

    @POST("/v1/notice/confirmed")
    suspend fun updateNoticeReadState(@Body data: RequestNoticeReadStateChange):Common

    @DELETE("/v1/reviews/{reviewId}")
    suspend fun deleteReview(@Path("reviewId") reviewId: Int): Common

    @PUT("/v1/reviews")
    suspend fun editReview(@Body data: RequestEditReview): Common

    @POST("/v1/settings")
    suspend fun updateSetting(@Body data:RequestSetting): Common

    @GET("/v1/settings")
    suspend fun getSettings():ResponseSetting

}