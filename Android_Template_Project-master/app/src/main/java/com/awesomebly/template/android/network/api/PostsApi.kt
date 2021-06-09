package com.awesomebly.template.android.network.api

import com.awesomebly.template.android.network.response.ResponsePosts
import retrofit2.http.*

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : PostsApi
 * Date : 2021-05-03, 오후 5:07
 * History
seq   date          contents      programmer
01.   2021-05-03                    차태준
02.
03.
 */
interface PostsApi {
    @GET("/posts/{num}")
    suspend fun getPostById(@Path("num") id: Long): ResponsePosts

    @GET("/posts")
    suspend fun getPostByUserId(@Query("userId") userId:Long): List<ResponsePosts>
}