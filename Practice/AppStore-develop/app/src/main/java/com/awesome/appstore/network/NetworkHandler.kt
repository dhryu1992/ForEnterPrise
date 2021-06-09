package com.awesome.appstore.network

import com.awesome.appstore.config.StringTAG.Companion.ERROR_CODE_401
import com.awesome.appstore.config.StringTAG.Companion.ERROR_CODE_404
import com.orhanobut.logger.Logger
import retrofit2.HttpException
import java.net.SocketTimeoutException

enum class ErrorCodes(val code: Int) {
    SocketTimeOut(-1)
}

class NetworkHandler {

    fun <T : Any> handleSuccess(data: T): NetworkResult<T> {
        return NetworkResult.success(data)
    }

    fun <T : Any> handleException(e: Exception): NetworkResult<T> {
        return when (e) {
            is HttpException -> NetworkResult.error(getErrorMessage(e.code(), e.message()), null)
            is SocketTimeoutException -> NetworkResult.error(
                getErrorMessage(ErrorCodes.SocketTimeOut.code, "Time out"),
                null
            )
            else -> NetworkResult.error(getErrorMessage(Int.MAX_VALUE, e.message), null)
        }
    }

    private fun getErrorMessage(code: Int, message: String?): String {
        return when (code) {
            ErrorCodes.SocketTimeOut.code -> "Timeout"
            401 -> "$code Unauthorised"
            404 -> "$code Not found"
            else -> "$code $message"

            //TODO 세부적으로 정의 필요
        }
    }
}
