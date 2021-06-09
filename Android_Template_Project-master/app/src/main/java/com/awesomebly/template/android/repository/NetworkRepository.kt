package com.awesomebly.template.android.repository

import android.content.Context
import android.provider.SyncStateContract.Helpers.insert
import com.awesomebly.template.android.database.dao.TpDao
import com.awesomebly.template.android.database.entity.TpEntity
import com.awesomebly.template.android.database.handler.DatabaseHandler
import com.awesomebly.template.android.database.handler.DatabaseResult
import com.awesomebly.template.android.network.api.PostsApi
import com.awesomebly.template.android.network.handler.NetworkHandler
import com.awesomebly.template.android.network.handler.NetworkResult
import com.awesomebly.template.android.network.response.ResponsePosts
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : DatabaseRepository
 * Date : 2021-05-03, 오후 3:03
 * History
seq   date          contents      programmer
01.   2021-05-03                    차태준
02.
03.
 */
class NetworkRepository @Inject constructor(
    private val appContext: Context,
    private val networkHandler: NetworkHandler,
    private val postsApi: PostsApi
) {

    suspend fun getPostById(id: Long): NetworkResult<ResponsePosts> {
        return try {
            networkHandler.handleSuccess(postsApi.getPostById(id))
        } catch (exception: Exception) {
            networkHandler.handleException(exception)
        }
    }

    suspend fun getPostByUserId(userId: Long): NetworkResult<List<ResponsePosts>> {
        return try {
            networkHandler.handleSuccess(postsApi.getPostByUserId(userId))
        } catch (exception: Exception) {
            networkHandler.handleException(exception)
        }
    }
}