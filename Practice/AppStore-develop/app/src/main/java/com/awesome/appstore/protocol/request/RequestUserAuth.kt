package com.awesome.appstore.protocol.request

data class RequestUserAuth(
    var data: UserAuth?

) {
    data class UserAuth(
        val account: String,
        val password: String
    )
}

