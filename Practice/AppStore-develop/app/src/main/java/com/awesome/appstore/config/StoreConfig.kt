package com.awesome.appstore.config

class StoreConfig {

    companion object {
        val BASEURL = "http://121.138.60.100:14100/"
        val MOCK_API = "https://api.mocki.io/"
        val FILE_PROVIDER = "com.awesome.appstore.fileprovider"
        var ERROR_LOG_FAIL = "save fail" + System.currentTimeMillis()
        val FETCH_DOWNLOAD_LIMT = 3
        val SSLVPN_PATH="/sdcard/SecuwayPKI3/Certs/"
        val SSLVPN_SERVER_ADDRESS = "https://1.226.142.20"
        val V3_ENABLE = false
        val SSLVPN_ENABLE = false
        val MDM_ENABLE = false


    }
}