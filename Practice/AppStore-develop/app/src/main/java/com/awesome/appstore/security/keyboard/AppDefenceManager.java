package com.awesome.appstore.security.keyboard;

import android.content.Context;
import android.os.Message;
import android.os.StrictMode;
import android.webkit.WebView;


import com.awesome.appstore.R;
import com.extrus.exafe.common.dialog.ExafeDialogHandler;
import com.orhanobut.logger.Logger;

import net.extrus.exafe.appdefence.module.appdefence.DefenceApiImpl;
import net.extrus.exafe.appdefence.module.appdefence.WeakHandler;
import net.extrus.exafe.appdefence.module.appdefence.XmlParser;
import net.extrus.exafe.appdefence.module.common.CommonAPIManager;
import net.extrus.exafe.appdefence.module.enc.util.SecurityUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;

/**
 * Created by cheetos on 2017-01-19.
 */

public class AppDefenceManager {

    private Context mContext = null;
    private WeakHandler mHandler = null;

    static {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public AppDefenceManager(Context context, WeakHandler handler) {
        // Context 설정
        mContext = context;
        CommonAPIManager.setActivityContext(mContext);

        // Handler 설정
        mHandler = handler;
    }

    public void initAppDefennce() {
//        LogManager.debug();
        Logger.d("initAppDefennce Start!!!");

        // 라이선스 세팅
        /*CommonAPIManager.setLicenseCode(ExafeConfig.licenseCode);*/

        // 디버그 모드 적용
        CommonAPIManager.setDebuggingMode(true);

        // Server 세팅
    //    if (MainActivity.appDefenceURIEditEnable == false) {
            CommonAPIManager.setAppDefenceIP(ExafeConfig.SERVICE_URL);

      //  }
        // Version 세팅
        CommonAPIManager.setAppDefenceVersion(ExafeConfig.VERSION_CODE);

        // 앱위변조 로그인 타입 세팅 함수
        CommonAPIManager.setEventType(ExafeConfig.EVENT_TYPE);

        // 앱위변조 검증 타입 세팅 함수
        CommonAPIManager.setDefenceFileType(ExafeConfig.DEFENCE_FILE_TYPE);

        // ProcessKill 세팅
        // @param processKillAlert : true: 동작 / false : 비동작
        // @param delay : 강제 종료 time
        CommonAPIManager.setAppDefenceProcessKill(false, 5000);

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
        // exafeDialogHandler.CUSTOM_DIALOG, exafeDialogHandler.DEFAULT_DIALOG
        // Default : CUSTOM_DIALOG
        CommonAPIManager.setAlertMessageView(true, ExafeDialogHandler.CUSTOM_DIALOG);
    }

//    public void appDefenceCallApi() {
//        if (mHandler != null) {
//            DefenceApiImpl DefenceApi = new DefenceApiImpl();
//            try {
//                String result = "";
//                // 앱위변조 검증
//                // ExafeConfig.EVENT_TYPE : 1:`기기고유정보 해시, 2:ID/PWD
//                // ExafeConfig.EVENT_TYPE 1인 경우 mLogin, mPassword값을 null로 처리해
//                // 주시면
//                // 됩니다.
//                if (DefenceApi != null && DefenceApi.verify(null, null) == true) {
//                    // 검증 성공 후 고객사 실행 API를 넣어주시면 됩니다.
//                    mHandler.sendEmptyMessage(Launcher3.APP_DEFENCE_CHECK_SUCCESS);
//
//                    // 서버 토큰
//                    String serverToken = DefenceApi.getServerToken();
//                    LogManager.debug(String.format("Server Token: %s", serverToken));
//
//                    // 클라이언트 시그니쳐
//                    String clientSignature = DefenceApi.getClientSignature();
//                    LogManager.debug(String.format("Client Signature: %s", clientSignature));
//
//                }
//                else {
//                    result = CommonAPIManager.getAppDefenceResult();
//                    LogManager.debug("AppDefence Result : " + result);
//                    Message msg = Message.obtain();
//                    msg.what = Launcher3.APP_DEFENCE_CHECK_FAIL;
//                    msg.obj = result;
//                    mHandler.sendMessage(msg);
//                }
//            } catch (Exception e) {
//                mHandler.sendEmptyMessage(Launcher3.APP_DEFENCE_CHECK_ERROR);
//                LogManager.printStackTrace(e);
//            }
//        }
//    }

//    public void simpleAppDefenceCallApi() {
//        if (mHandler != null) {
//            DefenceApiImpl DefenceApi = new DefenceApiImpl();
//            try {
//                String result = "";
//                // 앱위변조 검증
//                // ExafeConfig.EVENT_TYPE : 1:`기기고유정보 해시, 2:ID/PWD
//                // ExafeConfig.EVENT_TYPE 1인 경우 mLogin, mPassword값을 null로 처리해
//                // 주시면
//                // 됩니다.
//                if (DefenceApi != null && DefenceApi.SimpleVerify(null, null) == true) {
//                    // 검증 성공 후 고객사 실행 API를 넣어주시면 됩니다.
//                    mHandler.sendEmptyMessage(Launcher3.APP_DEFENCE_CHECK_SUCCESS);
//
//                    // 서버 토큰
//                    String serverToken = DefenceApi.getServerToken();
//                    LogManager.debug(String.format("Server Token: %s", serverToken));
//
//                    // 클라이언트 시그니쳐
//                    String clientSignature = DefenceApi.getClientSignature();
//                    LogManager.debug(String.format("Client Signature: %s", clientSignature));
//
//                }
//                else {
//                    result = CommonAPIManager.getAppDefenceResult();
//                    LogManager.debug("AppDefence Result : " + result);
//                    Message msg = Message.obtain();
//                    msg.what = Launcher3.APP_DEFENCE_CHECK_FAIL;
//                    msg.obj = result;
//                    mHandler.sendMessage(msg);
//                }
//            } catch (Exception e) {
//                mHandler.sendEmptyMessage(Launcher3.APP_DEFENCE_CHECK_ERROR);
//                LogManager.printStackTrace(e);
//            }
//        }
//    }

    // 토큰 검증 프로세스
    // (주의 사항)
    // 해당 기능은 AppDefecne 3.0기능입니다.
    // 기능 포함여부를 확인하셔야 정상 동작 합니다.
    // Webview로 URL을 호출 시 Header 정보에 앱위변조 토큰 검증 API값을 넣어 보내주셔야 정상적인 검증이 실행됩니다.
    public void checkTokenVerify(int webViewID, String tokenUrl) {
        if (ExafeConfig.mAppDefenceTokenServerEnable == true) {
            WebView host = (WebView) SecurityUtil.getActivity().findViewById(webViewID);
            Map<String, String> extraHeaders = new HashMap<String, String>();
            String mTokenMsg = new String(XmlParser.getAppDefenceToken()).replace("\n", "");
            extraHeaders.put("Exafe-Token", mTokenMsg);
            host.loadUrl(tokenUrl, extraHeaders);
        }
    }
}
