package com.awesome.appstore.activity

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.awesome.appstore.R
import com.awesome.appstore.activity.viewmodel.MainActivityViewModel
import com.awesome.appstore.databinding.ActivityMainBinding
import com.awesome.appstore.module.viewmodels.ViewModelFactory
import com.orhanobut.logger.Logger
import javax.inject.Inject

class MainActivity : BaseActivity() {
//
//    @Inject
//    lateinit var viewModelFactory: ViewModelFactory
//
//    private lateinit var binding: ActivityMainBinding
//
//    private lateinit var viewModel: MainActivityViewModel
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Logger.d("MainActivity-onCreate")
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
//        binding.viewModel = viewModel
//        binding.lifecycleOwner = this
//
//        viewModel.startLoginActivity.observe(this, {
//            if (it.getContentIfNotHandled()!!) {
//                val loginIntent = Intent(applicationContext, LoginActivity::class.java)
//                startActivity(loginIntent)
//            }
//        })
//        viewModel.startTabActivity.observe(this, {
////            throw Exception()
//            if (it.getContentIfNotHandled()!!) {
//                val tabIntent = Intent(applicationContext, TabActivity::class.java)
//                startActivity(tabIntent)
//            }
//        })
////        LockManager(this!!).add(Lock.generate(Lock.Type.PATTERN, it))
//    }
//    override fun onRestart() {
//        viewModel.start()
//        super.onRestart()
//    }

}