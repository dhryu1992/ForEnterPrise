package com.awesome.appstore.activity

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.awesome.appstore.R
import com.awesome.appstore.activity.viewmodel.SettingActivityViewModel
import com.awesome.appstore.config.StringTAG
import com.awesome.appstore.databinding.ActivitySettingBinding
import com.awesome.appstore.module.viewmodels.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class SettingActivity : BaseActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding : ActivitySettingBinding

    private lateinit var viewModel: SettingActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SettingActivityViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.onClickExit.observe(this,{
            this.finish()
        })
        viewModel.setPattern.observe(this, {
            if (it.getContentIfNotHandled()!!) {
                ScreenLockActivity.start(this, true, StringTAG.LOCK_PATTERN)
            }
        })
        viewModel.setPin.observe(this, {
            if (it.getContentIfNotHandled()!!) {
                ScreenLockActivity.start(this, true, StringTAG.LOCK_PIN)
            }
        })
        viewModel.pushAlarm.observe(this,{
        })
        viewModel.alertLock.observe(this,{
            Snackbar.make(binding.root, "패턴 또는 PIN 등록이 필요 합니다.", Snackbar.LENGTH_SHORT).show()
        })
        viewModel.tokenExpireDialog.observe(this,{
            tokenExpireDialog()
        })
        viewModel.lockScreenDisableMsg.observe(this,{
            Toast.makeText(this, "Lock Screen 사용이 불가능 합니다. 관리자에게 문의하십시오.",Toast.LENGTH_SHORT).show()
        })
    }
}