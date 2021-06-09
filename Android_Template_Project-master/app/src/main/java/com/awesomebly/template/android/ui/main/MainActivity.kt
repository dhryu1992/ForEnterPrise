package com.awesomebly.template.android.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.awesomebly.template.android.R
import com.awesomebly.template.android.databinding.MainActivityBinding
import com.awesomebly.template.android.ui.base.BaseActivity
import com.awesomebly.template.android.ui.main.fragment.FirstFragment
import com.awesomebly.template.android.ui.main.fragment.SecondFragment
import com.awesomebly.template.android.ui.main.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    lateinit var binding: MainActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
    }
}