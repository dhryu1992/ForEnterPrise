package com.awesome.appstore.security.keyboard;


import com.extrus.exafe.config.ExafeKeysecConfig;

public class ExafeConfig {

    /*########################################################################################################*/
    //# 보안키패드 설정
    //# (주의 사항)
    //# 해당 설정은 보안키패드의 종류를 세팅해 주는 부분으로 고객사에서 구매 시 협의된 타입으로 설정 해 주셔야 합니다.
    //#  KEYSEC_ENTERPRISE_MODE : encryption된 암호 결과 값
    //#   KEYSEC_STANDARD_MODE, KEYSEC_LITE_MODE 타입으로도 설정이 가능합니다.
    //#  KEYSEC_STANDARD_MODE : Hash 결과 값
    //#   KEYSEC_LITE_MODE 타입으로도 설정이 가능합니다.
    //#  KEYSEC_LITE_MODE : 평문 결과 값
    /*########################################################################################################*/
    public static final String licenseCode = "N2U2MzJlMzNjNzkzYTY5YWY2N2UxYjJiZmQ1MmRkNzc5YzgzN2MxM0soMykrRSgxKStEKDEpdW5saW1pdGVk";
    public static int mKeysecMode = ExafeKeysecConfig.KEYSEC_ENTERPRISE_MODE;
    // 이니텍 PKI지원 여부 설정
    public static boolean mKeysec_PKI_Inintech_Enable = false;
    /*########################################################################################################*/
    //# E2E 설정
    //# (주의 사항)
    //# 해당 설정은 보안키패드와 연동 시 설정되는 사항으로 다음과 같이 사용 시 세팅해 주셔야 합니다.
    //#  보안키패드 없이 E2E만 따로 구동을 원하실 경우
    //#  보안키패드 + E2E와 연동하여 구동을 원하는 경우
    /*########################################################################################################*/

    public static final String SERVER_PROTOCOL = "http"; // 서버 프로토콜(http / https)
    public static final String SERVER_IP = "1.226.142.60";
    public static final int SERVER_PORT = 5003; // 서버 Port

   //    public static final String SERVER_CONTEXT_PATH = "/e2e"; // 서버 컨텍스트 경로
    public static final String SERVER_CONTEXT_PATH = "/keysec-e2e"; // 서버 컨텍스트 경로
    public static final String SERVER_DEMO_URI = "/e2e"; // 서버 암복호화 데모 URI

    /*########################################################################################################*/
    //# 앱위변조 설정
    /*########################################################################################################*/

    // 테스트
    //public static final String SERVICE_URL = "http://203.228.172.58/e2e/e2e";   // 예시 "http://192.168.0.128:8080/ExafeE2EServer_Gradle_build/e2e";

    // 리얼
    public static final String SERVICE_URL = "http://1.226.142.60:4003/apd-e2e/e2e"; // 서버 프로토콜(http / https : 포트)
    public static final String EVENT_TYPE = "1"; // Type : 1:`기기고유정보 해시, 2:ID/PWD

    // 다음 사항에 대해서는 고객사에서 따로 설정을 하지 않으셔도 됩니다.
    public static final int VERSION_CODE = 2; // 1: 검증 xml 변경 전
    // 2: 검증 xml 변경 후
    public static final String DEFENCE_FILE_TYPE = "APK"; // 검증 모듈 타입
    public static boolean mAppDefenceTokenServerEnable = false; // 앱위변조 검증 토큰 서버 포함 여부
    public static final String TOKEN_SERVICE_URL = "http://192.168.0.179:8090/Exafe_WAS_Token_Verify/extrus/apd_token_verify.jsp"; // 서버 프로토콜(http / https : 포트)

}
