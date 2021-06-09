package com.awesome.appstore.util


data class FileDirectoryInfo(
    val rootDirectory: String,
    val externalDownLoads:String,
    val internalDownLoads: String,
    val caches: String,
    val pictures: String,
)