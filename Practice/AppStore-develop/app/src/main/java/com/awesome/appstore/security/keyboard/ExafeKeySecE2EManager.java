package com.awesome.appstore.security.keyboard;

import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.awesome.appstore.R;
import com.extrus.exafe.common.CommonAPIManager;
import com.extrus.exafe.common.dialog.ExafeDialogHandler;
import com.extrus.exafe.config.ExafeKeysecConfig;
import com.extrus.exafe.enc.util.SecurityUtil;

import net.extrus.exafe.appdefence.module.config.ExafeE2EConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;

import dagger.Module;

public class ExafeKeySecE2EManager {

    public static Context mContext = null;

    static {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public ExafeKeySecE2EManager(Context context) {
        // Context 설정
        mContext = context;
        CommonAPIManager.setActivityContext(mContext);
    }

    public void exafeInitKeySecE2E() {

        //////////////////////////////////////////////////////////////////
        // Common Init Settings
        //////////////////////////////////////////////////////////////////

        // 라이선스 세팅
        /*CommonAPIManager.setLicenseCode(ExafeConfig.licenseCode);*/

        // 디버그 모드 적용
        // 릴리즈 버전으로 출시 될 때 false로 지정해 주셔야 합니다.
        CommonAPIManager.setDebuggingMode(true);

        // Dialog 세팅 함수
        // @param themeid : theme id
        // @param dialogid : dialog id
        // @param titleid : title id
        // @param negativeButtonid : negativeButton id
        // @param positiveButtonid : positiveButton id
        // @param messageid : message id
        // @param contentid : content id
        CommonAPIManager.setDialogOption(R.style.ExafeDialog, R.layout.exafe_dialog, R.id.title, R.id.negativeButton,
                R.id.positiveButton, R.id.message, R.id.content);

        // AlertMessage 세팅
        // @param alertMessageView : 팝업 유무
        // true : 팝업띄움
        // @param dialogId : 팝업 타입
        // ExafeDialogHandler.CUSTOM_DIALOG, ExafeDialogHandler.DEFAULT_DIALOG
        // Default : CUSTOM_DIALOG
        CommonAPIManager.setAlertMessageView(true, ExafeDialogHandler.CUSTOM_DIALOG);

        //////////////////////////////////////////////////////////////////
        // KeySec Init Settings
        //////////////////////////////////////////////////////////////////

        // Blank 및 Blank내 이미지 지정
        // Defult는 true입니다.
        // MainActivity.mSampleStart 는 세플에서 제공하는 옵션을 적용하기 위해 임시로 넣은 코드입니다. 개발 진행
        // 시 삭제하시면 됩니다.
        //TODO: 부분 확인 1
        /*if (MainActivity.mSampleStart == false) {
            CommonAPIManager.setCBlank_Image_OnOff(true);
            CommonAPIManager.setCBlank_Custom_Image_OnOff(true);
        }*/

        // 라이트 모드 암호화 여부 세팅 함수
        CommonAPIManager.setLiteModeEncEnable(ExafeConfig.mKeysec_PKI_Inintech_Enable);

        // 색상 지정
        CommonAPIManager.setE_Keypad_UI_Type(ExafeKeysecConfig.KeypadUIType.GREY);
        ExafeKeysecConfig.initUIType(CommonAPIManager.getE_Keypad_UI_Type());

        // 테블릿폰의 경우 가로모드 보안키패드에 일괄 font size Up 지정
        // @param LANDSCAPE_FONT_SIZEUP = 12f
        // (주의) 테블릿에 한정되며, 문자 보안키패드 내부에만 반영됩니다.
        CommonAPIManager.setLandscapeFontSizeUp(12f);

        // 영문/한글 투글자 font size 지정
        // @param PORTRAIT_TOP_TXT_SIZE = 24f : 세로모드 영문 글자 크기
        // @param PORTRAIT_BOT_TXT_SIZE = 12f : 세로모드 한글 글자 크기
        // @param LANDSCAPE_TOP_TXT_SIZE = 20f : 가로모드 영문 글자 크기
        // @param LANDSCAPE_BOT_TXT_SIZE = 14f : 가로모드 한글 글자 크기
        CommonAPIManager.setTwoTxtFontSize(24f, 12f, 20f, 14f);

        // 숫자/기호/space/완료 font size 지정
        // PORTRAIT_COMMON_TXT_SIZE = 28f : 세로모드 숫자 글자 크기
        // LANDSCAPE_COMMON_TXT_SIZE = 16f : 가로모드 숫자 글자 크기
        // PORTRAIT_EXP_TXT_SIZE = 16f : 세로모드 기호/완료 글자 크기
        // LANDSCAPE_EXP_TXT_SIZE = 16f : 가로모드 기호/완료 글자 크기
        // PORTRAIT_SPACE_TXT_SIZE = 20f : 세로모드 space 글자 크기
        // LANDSCAPE_SPACE_TXT_SIZE = 20f : 가로모드 space 글자 크기
        CommonAPIManager.setOneTxtFontSize(28f, 16f, 20f, 16f, 38f, 30f);

        // 돋보기 font size 지정
        // PORTRAIT_POPUP_TXT_SIZE = 48f : 세로모드 돋보기 글자 크기
        // LANDSCAPE_POPUP_TXT_SIZE = 48f : 가로모드 돋보기 글자 크기
        CommonAPIManager.setOneTxtPopFontSize(48f, 48f);

        // Custom, NumKeypad 숫자 font size 지정
        // NUM_KEYPAD_TXT_SIZE = 28f
        CommonAPIManager.setNumKeypadTxtFontSize(28f);

        // Num keypad 라운딩 크기 조절 옵션
        // 조절 값 : 0 ~ 15
        CommonAPIManager.setCommonNumKeypadRadiusOption(10f);

        // Char keypad 라운딩 크기 조절 옵션
        // 조절 값 : 0 ~ 10
        CommonAPIManager.setCommonCharKeypadRadiusOption(5f);

        // Edit Control 라운딩 크기 조절 옵션
        // 조절 값 : 0 ~ 15
        CommonAPIManager.setCommonETCRadiusOption(10f);

        // 버튼 크기 조절 옵션
        // 조절 값 : 크기는 기본 70에 더해져서 크게 변경됩니다.
        CommonAPIManager.setCommonETCButtonSizeOption(87);
        
        // 스크린캡쳐 방지 세팅
        // API는 HONEYCOMB 이상에서만 동작합니다.
        // 폰마다 상이하게 작용하는 부분에 대해서는 보장할 수 없으며, 고객사 측에서 필요한 경우에만 적용하여 사용하시기 바랍니다.
        // (주의 사항) 해당 API를 사용할 경우 다음 두개의 API를 반듯이 사용해 주셔야 합니다.
        // enableScreenCapture() 기능 활성화를 위한 API를 선언해 주셔야 합니다.
        // Activity가 종료될 때 SecurityUtil.setDisAllowScreenCapture() 함수를 사용하여
        // 원복처리해 주셔야 합니다.
        // @param activity 액티비티
        SecurityUtil.enableScreenCapture(false);
        SecurityUtil.setAllowScreenCapture((Activity) mContext);

        //////////////////////////////////////////////////////////////////
        // E2E Init Settings
        //////////////////////////////////////////////////////////////////

        // TOTP Interval 설정
        // E2E Server와 동일한 값으로 세팅을 해야 합니다. 아닐 경우 TOTP 검증 오류 발생합니다.
        CommonAPIManager.setTOTPInterval(60);
    }

    /**
     * E2E 전용 암복호화 테스트 - String 암호화된메세지 = E2EApiManager.e2eEncrypt(암호화대상메세지);
     * String 복호화된메세지 = E2EApiManager.e2eDecrypt(암호화된메세지);
     */
    public static void startE2EEncDec(byte[] message) {

        StringBuffer sbLog = new StringBuffer();

        // 테스트 메세지 생성
//        LogManager.debug("Enc Taget Msg : [" + new String(message) + "]");
        sbLog.append("Enc Taget Msg : [").append(new String(message)).append("]\n\n");

        // E2E 암호화 API
        byte[] encryptedMessage = null;
        try {
            encryptedMessage = net.extrus.exafe.appdefence.module.common.CommonAPIManager.e2eEncrypt(mContext, message);
        } catch (Exception e) {
//            LogManager.printStackTrace(e);
            Log.e("상훈",e.getMessage());
            // CommonUtil.exafeAlert(mContext, String.valueOf(e.getErrorCode()),
            // e.getMessage());
            return;
        }

        // 클라이언트 - 서버 통신(테스트)
        String serverUrl = generateServerUrl();
        // LogManager.debug("E2E 테스트 URL : [" + serverUrl + "]");
        sbLog.append("E2E Test URL : [").append(serverUrl).append("]\n\n");

        String serverReturnedMessage = "";
        try {
            // serverReturnedMessage = ProtocolManager.sendServer(serverDemoUrl,
            // encryptedMessage, SERVER_CHARSET, SERVER_CONN_TIMEOUT);

            Map<String, String> params = new HashMap<String, String>();

            params.put("e2eFormatedMessage", new String(encryptedMessage));
            serverReturnedMessage = net.extrus.exafe.appdefence.module.common.CommonAPIManager.requestServer(mContext, serverUrl, params,
                    ExafeE2EConfig.SERVER_CONN_TIMEOUT);

            params.remove("e2eFormatedMessage");

            if (serverReturnedMessage.isEmpty()) {
                return;
            }
        } catch (Exception e) {
//            LogManager.printStackTrace(e);
            return;
        }

        net.extrus.exafe.appdefence.module.common.CommonAPIManager.checkTOTPServerReturnedMessage(serverReturnedMessage);
        // LogManager.debug("E2E 암호화 메세지(서버리턴) : [" + serverReturnedMessage +
        // "]");
        sbLog.append("E2E Enc Msg(Server) : [").append(serverReturnedMessage).append("]\n\n");

        if (serverReturnedMessage.contains("Error report")) {
            net.extrus.exafe.appdefence.module.common.dialog.ExafeDialogHandler.exafeAlert(mContext, "E2E Server Error", sbLog.toString());
            return;
        }

        // E2E 복호화
        byte[] decryptedMessage = null;
        try {
            decryptedMessage = net.extrus.exafe.appdefence.module.common.CommonAPIManager.e2eDecrypt(mContext, serverReturnedMessage.getBytes());
        } catch (Exception e) {
//            LogManager.printStackTrace(e);
            return;
        }
        // LogManager.debug("E2E 복호화 메세지(서버리턴) : [" + new
        // String(decryptedMessage) + "]");
        sbLog.append("E2E Dec Msg(Server) : [").append(new String(decryptedMessage)).append("]\n\n");

        net.extrus.exafe.appdefence.module.common.dialog.ExafeDialogHandler.exafeAlert(mContext, "Result", sbLog.toString());
    }
    
    private static String generateServerUrl() {
        return ExafeConfig.SERVER_PROTOCOL + "://" + ExafeConfig.SERVER_IP + ":" + ExafeConfig.SERVER_PORT
                + ExafeConfig.SERVER_CONTEXT_PATH + ExafeConfig.SERVER_DEMO_URI;
    }

    public static void stopKeySec() {
        // 스크린캡쳐 방지 세팅 해제
        // Webview일 경우 windowsManager에서 죽는 현상이 발생될 수 있다. 이때는 Main Thread가 아니여서
        // 발생되는 현상으로 Main에서 관련 함수를 처리하면 된다.
        SecurityUtil.setDisAllowScreenCapture((Activity) mContext);
        // Alert Handler Dismiss 처리 API
        ExafeDialogHandler.exafeAlertHandlerDismiss();
    }
}
