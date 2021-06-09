package com.awesome.appstore.protocol.response

data class ResponseUserAuth(
    val common: Common,
    val data: UserAuth
){
    data class UserAuth(
        val expiredTime: Long,
        val token: String
    )
}
