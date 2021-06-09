package com.awesomebly.template.android.network.handler

import com.orhanobut.logger.Logger

data class NetworkResult<out T>(val networkStatus: NetworkStatus, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): NetworkResult<T> {
            return NetworkResult(NetworkStatus.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): NetworkResult<T> {
            Logger.e(msg)
            return NetworkResult(NetworkStatus.ERROR, data, msg)
        }
        fun <T> loading(data: T?): NetworkResult<T> {
            return NetworkResult(NetworkStatus.LOADING, data, null)
        }
    }
}