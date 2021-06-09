package com.awesome.appstore.config

class StoreConst {
    companion object {
        val REQUESTCODE_UPGRADE = 43
        val DATABASE_NAME = "store.db"
        val REQUEST_INSTALL_PERMISSION = 1000
        const val REQ_CODE_NUM_KEYPAD = 1 // 평문 보안키패드(숫자) 요청코드
        const val REQ_CODE_CHAR_KEYPAD = 2 // 평문 보안키패드(문자) 요청코드
        const val REQ_CODE_ENC_NUM_KEYPAD = 3 // 암호 보안키패드(숫자) 요청코드
        const val REQ_CODE_ENC_CHAR_KEYPAD = 4 // 암호 보안키패드(문자) 요청코드
        const val BTN_BACK_PRESSED = 9999 // 암호 보안키패드(문자) 요청코드
        const val V3_POLICY3 =
            "{ \"mdm_policy\": {   \"av_realtime\":\"on\",   \"av_realtime_level\":\"low\",   \"av_realtime_storage\":\"on\",   \"av_realtime_behavioral\":\"on\",   \"av_realtime_php\":\"on\",   \"av_realtime_pup\":\"on\",   \"av_manual_php\":\"on\",   \"av_manual_pup\":\"on\",   \"av_reserved\":\"on\",   \"av_reserved_term\":\"day\",   \"av_reserved_weekday\":\"0\",   \"av_reserved_time\":\"12:00\",   \"av_forced_removal\":\"off\",   \"av_display_scan_report\":\"on\",   \"av_update_reserved\":\"on\",   \"av_update_reserved_term\":\"day\",   \"av_update_reserved_weekday\":\"0\",   \"av_update_reserved_time\":\"15:00\",   \"removal_protection\":\"off\" }}"

    }
}