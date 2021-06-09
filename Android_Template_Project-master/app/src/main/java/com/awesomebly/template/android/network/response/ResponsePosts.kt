package com.awesomebly.template.android.network.response

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : ResponsePosts
 * Date : 2021-05-03, 오후 5:14
 * History
seq   date          contents      programmer
01.   2021-05-03                   차태준
02.
03.
 */

data class ResponsePosts(
    val id: Long,
    val userId: Long,
    val title: String,
    val body: String
)