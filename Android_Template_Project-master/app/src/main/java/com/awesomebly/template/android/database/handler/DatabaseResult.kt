package com.awesomebly.template.android.database.handler

import com.orhanobut.logger.Logger

data class DatabaseResult<out T>(val databaseStatus : DatabaseStatus, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): DatabaseResult<T> {
            return DatabaseResult(DatabaseStatus.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): DatabaseResult<T> {
            Logger.e(msg)
            return DatabaseResult(DatabaseStatus.ERROR, data, msg)
        }

        fun <T> loading(data: T?): DatabaseResult<T> {
            return DatabaseResult(DatabaseStatus.LOADING, data, null)
        }
    }
}