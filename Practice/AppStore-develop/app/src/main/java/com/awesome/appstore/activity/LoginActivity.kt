 package com.awesome.appstore.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ahnlab.v3mobileclient.interfaces.V3Ctl
import com.ahnlab.v3mobileclient.interfaces.V3CtlObserver
import com.ahnlab.v3mobileclient.interfaces.V3CtlReceiver
import com.awesome.appstore.R
import com.awesome.appstore.activity.viewmodel.LoginActivityViewModel
import com.awesome.appstore.config.StoreConfig.Companion.MDM_ENABLE
import com.awesome.appstore.config.StoreConfig.Companion.SSLVPN_ENABLE
import com.awesome.appstore.config.StoreConfig.Companion.SSLVPN_PATH
import com.awesome.appstore.config.StoreConfig.Companion.V3_ENABLE
import com.awesome.appstore.config.StoreConst.Companion.REQ_CODE_CHAR_KEYPAD
import com.awesome.appstore.config.StoreConst.Companion.REQ_CODE_ENC_CHAR_KEYPAD
import com.awesome.appstore.config.StoreConst.Companion.REQ_CODE_ENC_NUM_KEYPAD
import com.awesome.appstore.config.StoreConst.Companion.REQ_CODE_NUM_KEYPAD
import com.awesome.appstore.config.StoreConst.Companion.V3_POLICY3
import com.awesome.appstore.config.StringTAG
import com.awesome.appstore.config.StringTAG.Companion.SSLVPN
import com.awesome.appstore.databinding.ActivityLoginBinding
import com.awesome.appstore.module.viewmodels.ViewModelFactory
import com.awesome.appstore.security.keyboard.ExafeConfig
import com.awesome.appstore.security.keyboard.ExafeKeySecE2EManager
import com.awesome.appstore.security.mdm.MDMHelper
import com.awesome.appstore.security.sslvpn.SecuwaySSLHelper
import com.awesome.appstore.util.FileDirectoryInfo
import com.awesome.appstore.util.PackageUtil
import com.awesome.appstore.util.StorePreference
import com.awesome.appstore.util.Utils
import com.extrus.exafe.common.dialog.ExafeDialogHandler
import com.extrus.exafe.config.ExafeKeysecConfig
import com.extrus.exafe.enc.common.security.ExafeKeysecEncPKIInintech
import com.extrus.exafe.inintech.ExafeKeysecDec
import com.extrus.exafe.keysec.api.KeySecApiManager
import com.extrus.exafe.keysec.ui.CharKeypad
import com.extrus.exafe.keysec.ui.NumKeypad
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.orhanobut.logger.Logger
import com.scottyab.rootbeer.RootBeer
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.system.exitProcess

class LoginActivity : BaseActivity(), V3CtlObserver {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var storePreference: StorePreference

    @Inject
    lateinit var rootBeer: RootBeer

    @Inject
    lateinit var packageUtil: PackageUtil

    @Inject
    lateinit var fileDirectoryInfo: FileDirectoryInfo

    @Inject
    lateinit var mdmHelper: MDMHelper

    @Inject
    lateinit var secuwaySSLHelper: SecuwaySSLHelper

    lateinit var exafeKeySecE2EManager: ExafeKeySecE2EManager

    private lateinit var binding: ActivityLoginBinding

    private lateinit var viewModel: LoginActivityViewModel

    //V3
    private var v3Bind = false
    private var isV3ctlRegisterTrue = false
    lateinit var mV3CtlReceiver: V3CtlReceiver
    lateinit var v3Ctl: V3Ctl

    private var issslVPNKeyInput = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d("onCreate LoginActivity")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(LoginActivityViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
//        startService(Intent(this, StoreService::class.java))
        
        if (Utils().isRootedDevice(this)){
            Toast.makeText(applicationContext, "루팅된 디바이스 입니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        if (V3_ENABLE) {
            v3Ctl = V3Ctl(this@LoginActivity, this@LoginActivity).also { isV3ctlRegisterTrue = true }
            mV3CtlReceiver = V3CtlReceiver(this@LoginActivity).apply { registerV3CtlObserver(this@LoginActivity) }
        }

        if (MDM_ENABLE) mdmHelper.checkMdmStatus(applicationContext, this@LoginActivity)

        if (SSLVPN_ENABLE) secuwaySSLHelper.bindService(applicationContext)

        exafeKeySecE2EManager = ExafeKeySecE2EManager(this)
        exafeKeySecE2EManager.exafeInitKeySecE2E()

        storePreference.saveIdSavePreference("")
        storePreference.saveTokenPreference("")

        viewModel.showUserIdToast.observe(this,
            { booleanEvent ->
                Logger.d("onChanged")
                if (booleanEvent.getContentIfNotHandled()!!) {
                    Toast.makeText(
                        applicationContext,
                        R.string.login_id_require,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        viewModel.showPasswordToast.observe(this,
            { booleanEvent ->
                Logger.d("onChanged")
                if (booleanEvent.getContentIfNotHandled()!!) {
                    Toast.makeText(
                        applicationContext,
                        R.string.login_password_require,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        viewModel.showSSLVPNPassword.observe(this,
            { booleanEvent ->
                Logger.d("onChanged")
                if (booleanEvent.getContentIfNotHandled()!!) {
                    Toast.makeText(
                        applicationContext,
                        R.string.login_sslvpn_passowrd_require,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        viewModel.showRequestMessage.observe(this,
            { booleanEvent ->
                Logger.d("onChanged")
                if (booleanEvent.getContentIfNotHandled()!!) {
                    binding.progressLogin.visibility = View.GONE
                    Toast.makeText(
                        applicationContext,
                        viewModel.requestErrorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        viewModel.startTabActivity.observe(this, {
//            startActivity(Intent(this@LoginActivity, TabActivity::class.java))
            //todo 서버에 로그인 확인 하는것보다 먼지 실행 하게 변경해야 함
            if (!mdmHelper.isRunning) mdmHelper.startMDM()
            if (!secuwaySSLHelper.isVPNService() && SSLVPN_ENABLE) {
                if (checkCertFile(SSLVPN_PATH, storePreference.loadIdSavePreference()!!)) {
                    viewModel.startVPN()
                } else {
                    Toast.makeText(this, "아이디에 해당하는 인증서가 존재하지 않습니다. 인증서를  다시 발급해주세요.", Toast.LENGTH_SHORT).show()
                    startActivity(packageManager.getLaunchIntentForPackage(SSLVPN))
                    finish()
                }
            } else {
                startActivity(Intent(this@LoginActivity, TabActivity::class.java))
                finish()
            }
        })
        viewModel.showLoadingProgress.observe(this, {
            binding.progressLogin.visibility = View.VISIBLE
        })
        viewModel.showAppInstallDialog.observe(this, {
            when (it) {
                StringTAG.V3_MOBILE -> {
                    MaterialAlertDialogBuilder(this)
                        .setMessage("백신이 설치되지 않았습니다. 설치를 진행합니다.")
                        .setCancelable(false)
                        .setPositiveButton("확인", DialogInterface.OnClickListener { _, _ ->
                            val inputStream = assets.open(StringTAG.V3_APK)
                            val outPath = fileDirectoryInfo.internalDownLoads + "/$it.apk"
                            val outputStream = FileOutputStream(outPath)
                            val buffer = ByteArray(2048)
                            while (true) {
                                val data = inputStream.read(buffer)
                                if (data == -1) break
                                outputStream.write(buffer, 0, data)
                            }
                            inputStream.close()
                            outputStream.close()

                            packageUtil.startInstallPackage(this, "/$it.apk", 1000, true)
                            finishAffinity()
                        })
                        .show()
                }
                StringTAG.MDM -> {
                    MaterialAlertDialogBuilder(this)
                        .setMessage("mdm이 설치되지 않았습니다. 설치를 진행합니다.")
                        .setCancelable(false)
                        .setPositiveButton("확인", DialogInterface.OnClickListener { _, _ ->
                            val manufacturer = Build.MANUFACTURER
                            var apkName = when {
                                manufacturer.equals("samsung", ignoreCase = true) -> {
                                    StringTAG.MDM_APK_S
                                }
                                manufacturer.equals("LGE", ignoreCase = true) -> {
                                    StringTAG.MDM_APK_L
                                }
                                else -> {
                                    StringTAG.MDM_APK_G
                                }
                            }
                            val inputStream = assets.open(apkName)
                            val outPath = fileDirectoryInfo.internalDownLoads + "/$it.apk"
                            val outputStream = FileOutputStream(outPath)
                            val buffer = ByteArray(2048)
                            while (true) {
                                val data = inputStream.read(buffer)
                                if (data == -1) break
                                outputStream.write(buffer, 0, data)
                            }
                            inputStream.close()
                            outputStream.close()

                            packageUtil.startInstallPackage(this, "/$it.apk", 1000, true)
                            finishAffinity()
                        })
                        .show()
                }
                StringTAG.SSLVPN -> {
                    MaterialAlertDialogBuilder(this)
                        .setMessage("모바일 인증센터가 설치되지 않았습니다. 설치를 진행합니다.")
                        .setCancelable(false)
                        .setPositiveButton("확인", DialogInterface.OnClickListener { _, _ ->
                            val inputStream = assets.open(StringTAG.SSL_APK)
                            val outPath = fileDirectoryInfo.internalDownLoads + "/$it.apk"
                            val outputStream = FileOutputStream(outPath)
                            val buffer = ByteArray(2048)
                            while (true) {
                                val data = inputStream.read(buffer)
                                if (data == -1) break
                                outputStream.write(buffer, 0, data)
                            }
                            inputStream.close()
                            outputStream.close()

                            packageUtil.startInstallPackage(this, "/$it.apk", 1000, true)
                            finishAffinity()
                        })
                        .show()
                }
                StringTAG.SSLVPN_SERVICE -> {
                    MaterialAlertDialogBuilder(this)
                        .setMessage("SecuwaySSL이 설치되지 않았습니다. 설치를 진행합니다.")
                        .setCancelable(false)
                        .setPositiveButton("확인", DialogInterface.OnClickListener { _, _ ->
                            val inputStream = assets.open(StringTAG.SSL_SERVICE_APK)
                            val outPath = fileDirectoryInfo.internalDownLoads + "/$it.apk"
                            val outputStream = FileOutputStream(outPath)
                            val buffer = ByteArray(2048)
                            while (true) {
                                val data = inputStream.read(buffer)
                                if (data == -1) break
                                outputStream.write(buffer, 0, data)
                            }
                            inputStream.close()
                            outputStream.close()

                            packageUtil.startInstallPackage(this, "/$it.apk", 1000, true)
                            finishAffinity()
                        })
                        .show()
                }
            }
        })

//        binding.textPw.setOnClickListener {
//            issslVPNKeyInput = false
//            val mKeysecMode = ExafeKeysecConfig.KEYSEC_ENTERPRISE_MODE
//            startCharKeypad(5, 16, mKeysecMode)
//        }

        binding.textPw.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                issslVPNKeyInput = false
                val mKeysecMode = ExafeKeysecConfig.KEYSEC_ENTERPRISE_MODE
                startCharKeypad(5, 16, mKeysecMode)
            }
        }

        binding.textSslvpn.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                issslVPNKeyInput = true
                val mKeysecMode = ExafeKeysecConfig.KEYSEC_LITE_MODE
                startCharKeypad(5, 16, mKeysecMode)
            }
        }

//        binding.textSslvpn.setOnClickListener {
//            issslVPNKeyInput = true
//            val mKeysecMode = ExafeKeysecConfig.KEYSEC_LITE_MODE
//            startCharKeypad(5, 16, mKeysecMode)
//        }

        rootChecker()
        getPushToken()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE_NUM_KEYPAD -> {
                // 평문 보안키패드(숫자)

                if (resultCode == RESULT_OK) {
                    val keypadEncryptedMessage = data!!.getStringExtra(NumKeypad.KEYPAD_ENC_MESSAGE)
                    Logger.d("NUM KEYPAD - MESSAGE : [$keypadEncryptedMessage]")
                    ExafeDialogHandler.exafeAlert(
                        this, "Result",
                        "NUM KEYPAD - MESSAGE : [$keypadEncryptedMessage]"
                    )
                    // Initech PKI 복호화
                    if (ExafeConfig.mKeysec_PKI_Inintech_Enable === true) {
                        // 복호화 함수
                        val keypadDecryptedMessage = ExafeKeysecDec.getPassWord(
                            this, keypadEncryptedMessage!!.toByteArray(),
                            ExafeKeysecEncPKIInintech.getSeedOptionPassword(),
                            ExafeKeysecEncPKIInintech.getSeedOptionSalt()
                        )
                        Logger.d("NUM KEYPAD - Initech PKI Dec MESSAGE : [" + String(keypadDecryptedMessage) + "]")
                        ExafeDialogHandler.exafeAlert(
                            this, "Result",
                            "NUM KEYPAD - Initech PKI Dec MESSAGE : [" + String(keypadDecryptedMessage) + "]"
                        )
                        // Option reset함수
                        // ExafeKeysecEncPKIInintech.setSeedOptionReset();
                    }
                }
            }
            REQ_CODE_CHAR_KEYPAD -> {
                // 평문 보안키패드(문자)

                if (resultCode == RESULT_OK) {
                    val keypadEncryptedMessage = data!!.getStringExtra(CharKeypad.KEYPAD_ENC_MESSAGE)
                    Logger.d("CHAR KEYPAD - MESSAGE : [$keypadEncryptedMessage]")
                    //     ExafeDialogHandler.exafeAlert(this, "Result","CHAR KEYPAD - MESSAGE : [" + keypadEncryptedMessage + "]");

                    // Initech PKI 복호화
                    if (ExafeConfig.mKeysec_PKI_Inintech_Enable === true) {
                        // 복호화 함수
                        val keypadDecryptedMessage = ExafeKeysecDec.getPassWord(
                            this, keypadEncryptedMessage!!.toByteArray(),
                            ExafeKeysecEncPKIInintech.getSeedOptionPassword(),
                            ExafeKeysecEncPKIInintech.getSeedOptionSalt()
                        )
                        Logger.d("CHAR KEYPAD - Initech PKI Dec MESSAGE : [" + String(keypadDecryptedMessage) + "]")
                        ExafeDialogHandler.exafeAlert(
                            this, "Result",
                            "CHAR KEYPAD - Initech PKI Dec MESSAGE : [" + String(keypadDecryptedMessage) + "]"
                        )
                        // Option reset함수
                        // ExafeKeysecEncPKIInintech.setSeedOptionReset();
                    } else {
                        if (issslVPNKeyInput) {
                            binding.textSslvpn.setText(keypadEncryptedMessage)
                            Logger.d("sslVPN password :$keypadEncryptedMessage")
                        }
                    }
                }
            }
            REQ_CODE_ENC_NUM_KEYPAD -> {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        val keypadEncryptedMessage = data.getStringExtra(NumKeypad.KEYPAD_ENC_MESSAGE)
                        Logger.d("NUM KEYPAD - ENC MESSAGE : [$keypadEncryptedMessage]")
                    }
                }
            }
            REQ_CODE_ENC_CHAR_KEYPAD -> {
                if (resultCode == RESULT_OK) {
                    try {
                        if (data != null) {
                            val keypadEncryptedMessage = data.getStringExtra(CharKeypad.KEYPAD_ENC_MESSAGE)
                            Logger.d("REQ_CODE_ENC_CHAR_KEYPAD - ENC MESSAGE : [$keypadEncryptedMessage]")
                            if (!issslVPNKeyInput) {
                                binding.textPw.text = keypadEncryptedMessage as Editable
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        Logger.d("REQ_CODE_ENC_CHAR_KEYPAD  error catch" + e.message)
                        binding.textPw.text = "" as Editable
                    }
                }
            }
        }
    }


    private fun rootChecker() {
        if (rootBeer.isRooted) {
            Toast.makeText(
                applicationContext,
                R.string.login_rooting_device,
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }

    }

    override fun onResume() {
        viewModel.checkInstallV3()
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        closeV3()
        mdmHelper.checkMdmStatus(applicationContext, this@LoginActivity)
        mdmHelper.stopMDM()
        unbindService(mdmHelper.mdmServiceConn)
        unbindService(secuwaySSLHelper.mConnection)
        exitProcess(0)
    }

    private fun getPushToken() {
        Firebase.messaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Logger.d("Fetching FCM registration token failed $task.exception")
                return@OnCompleteListener
            }
            val token = task.result
            Logger.d("token : $token")
//            storePreference.saveTokenPreference(token)
        })
    }

    //    V3
    private fun checkResponse(response: String): Int {
        return if (response.isNotEmpty()) {
            val res = response.split("\\|".toRegex()).toTypedArray()[0]
            res.toInt()
        } else {
            -1
        }
    }

    private fun checkResponse2(response: String): Int {
        return if (response.isNotEmpty()) {
            val res = response.split("\\|".toRegex()).toTypedArray()[1]
            res.toInt()
        } else -1
    }

    override fun onFinishedTask(s: String?, i: Int) {
        if (V3Ctl.ERROR_SUCCESS == i) {
            Toast.makeText(this, "V3 검사가 끝났습니다. [$s]", Toast.LENGTH_SHORT).show()
        } else {
            if (V3Ctl.ERROR_DUPLICATION_COMMAND == i) {
                Toast.makeText(this, "V3 검사가 끝났습니다. [D_$s]", Toast.LENGTH_SHORT).show()
            } else {
//                val CLOSE_LISTENER = View.OnClickListener {}
                Logger.d("V3 오류 : $i")
                Toast.makeText(this, "V3 검사가 실패했습니다 다시 실행해 주세요", Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
        }
    }

    override fun onClientBinded() {
        v3Bind = true
//        val file = File("${filesDir.absolutePath}/v3meprofile.dat")
//        if (!file.exists()) {
        val inputStream = assets.open("v3meprofile.dat")
        val outPath = "${filesDir.absolutePath}/v3meprofile.dat"
        val outputStream = FileOutputStream(outPath)
        val buffer = ByteArray(2048)
        while (true) {
            val data = inputStream.read(buffer)
            if (data == -1) break
            outputStream.write(buffer, 0, data)
        }
        inputStream.close()
        outputStream.close()
        Logger.d(File("${filesDir.absolutePath}/v3meprofile.dat").exists())
        Logger.d(File("${filesDir.absolutePath}/v3meprofile.dat").readText())
//        }

        var ret: String = v3Ctl.RmCtlV3(V3Ctl.RMCTLV3_CMD_W_PROFILE, outPath)
        Logger.d("V3 onClientBinded :$ret")
        if (V3Ctl.ERRNO_SUCCESS == checkResponse(ret)) {
            ret = v3Ctl.RmCtlV3(V3Ctl.RMCTLV3_CMD_PROFILE)
            if (V3Ctl.ERRNO_SUCCESS == checkResponse(ret)) onCommandReceived()
            else Logger.d("프로파일 적용실패 :$ret")
        } else {
            Logger.d("프로파일 등록 실패 :$ret")
        }
        Logger.d("V3 onClientBinded :$ret")
    }

    override fun onClientUnbinded() {
        v3Bind = false
    }

    private fun onCommandReceived() {
        var response: String? = ""
        if (v3Bind) {
            var policy_write_ret: String? = ""
            policy_write_ret = v3Ctl.RmCtlV3(V3Ctl.RMCTLV3_CMD_W_POLICY, V3_POLICY3)
            if (V3Ctl.ERROR_SUCCESS == checkResponse(policy_write_ret)) {
                policy_write_ret = v3Ctl.RmCtlV3(V3Ctl.RMCTLV3_CMD_POLICY)
                response = v3Ctl.RmCtlV3(V3Ctl.RMCTLV3_CMD_AV_FSCAN)
                Logger.d(response)
            } else {
                Logger.d(policy_write_ret)
                Toast.makeText(this, "V3 실시간 검사에 실패하였습니다. (정책적용 및 일반검사)", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        Logger.d(v3Bind)
        if (V3Ctl.ERROR_SUCCESS != checkResponse(response!!)) {
            if (V3Ctl.ERRNO_PERMISSION_NOT_GRANTED == checkResponse2(response)) {
                v3Ctl.RmCtlV3(V3Ctl.RMCTLV3_CMD_EXECUTE_V3ME)
                finish()
            }
        }
    }

    fun closeV3() {
        Logger.d("closeV3")
        try {
            if (isV3ctlRegisterTrue) {
                mV3CtlReceiver.removeV3CtlObserver(this)
                v3Ctl.close()
            }
        } catch (e: Exception) {
            Logger.d("LoginActivity closeV3 Remove Exception:", e.toString())
        }
    }

    fun checkCertFile(certPath: String, userId: String): Boolean {
        val filePath = certPath + "sh_" + userId.trim { it <= ' ' } + ".crt"
        val file = File(filePath)
        Logger.d(file.toString())
        return file.exists()
    }

    //////////////////////////// Keyboard ///////////////////////////
    private fun startNumKeypad(min: Int, max: Int) {
        val mTitle = getString(R.string.str_btn_qwerty_keypad_title)
        val mSubTitle = getString(R.string.str_btn_rrn)
        var requestCode = 0
        var encEable = false
        Logger.d("startNumKeypad() start")
        ExafeKeysecConfig.setHintMsg(getString(R.string.str_btn_hint_msg, mSubTitle))
        if (ExafeConfig.mKeysecMode === ExafeKeysecConfig.KEYSEC_LITE_MODE) {
            requestCode = REQ_CODE_NUM_KEYPAD
            encEable = false
        } else {
            requestCode = REQ_CODE_ENC_NUM_KEYPAD
            encEable = true
        }
        try {
            KeySecApiManager.startNumKeypad(
                this, mTitle, mSubTitle, min, max, requestCode, ExafeConfig.mKeysecMode,
                encEable
            )
        } catch (e: java.lang.Exception) {
            Logger.d(e)
        }
    }

    /**
     * 보안 키패드
     */
    private fun startCharKeypad(min: Int, max: Int, mKeysecMode: Int) {
        Logger.d("startCharKeypad() start")
        val mTitle = getString(R.string.str_btn_qwerty_keypad_title)
        val mSubTitle = getString(R.string.str_btn_rrn)
        var requestCode = 0
        var encEable = false
        ExafeKeysecConfig.setHintMsg(getString(R.string.str_btn_hint_msg, mTitle))
        if (ExafeKeysecConfig.KEYSEC_LITE_MODE == mKeysecMode) {
            requestCode = REQ_CODE_CHAR_KEYPAD
            encEable = false
        } else {
            requestCode = REQ_CODE_ENC_CHAR_KEYPAD
            encEable = true
        }
        try {
            KeySecApiManager.startCharKeypad(
                this, mTitle, mSubTitle, min, max, requestCode, ExafeConfig.mKeysecMode,
                encEable
            )
        } catch (e: Exception) {
            Logger.d(e)
        }
    }
}