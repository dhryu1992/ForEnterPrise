package com.example.databinding_kotlin

import android.database.DatabaseUtils
import android.databinding.DataBindingUtil.setContentView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.databinding_kotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val DataBinding = DatabaseUtils.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.vm = ViewModel()
    }
}