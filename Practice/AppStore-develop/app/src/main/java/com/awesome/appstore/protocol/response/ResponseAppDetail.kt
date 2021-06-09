package com.awesome.appstore.protocol.response

data class ResponseAppDetail(
    val common: Common,
    val data: AppDetail
) {
    data class AppDetail(
        val app: App,
        val version: Version,
        val files: List<DownloadFile>

    )
    data class App(
        val appId: Long,
        val appName: String,
        val releaseType: String,
        val descr: String,
        var starPoint: Double,
        var reviewCount: Int,
        var regDt: String,
        var modDt: String
    )
    data class Version(
        val versionId: Long,
        val versionName: String,
        val updateType: String,
        var downloadCount: Int,
        val descr: String,
        var regDt: String,
        var modDt: String
    )
    data class DownloadFile(
        val fileId: Long,
        val url: String,
        val type: String
    )
}

