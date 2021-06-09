package com.awesome.appstore.database

import java.net.SocketTimeoutException

class DatabaseHandler {

    fun <T : Any> handleSuccess(data:T): DatabaseResult<T> {
        return DatabaseResult.success(data)
    }

    fun <T : Any> handleException(e: Exception): DatabaseResult<T> {
        return DatabaseResult.error(e.toString(), null)
    }

}
