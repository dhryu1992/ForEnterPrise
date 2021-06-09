package com.awesomebly.template.android.ui.base

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awesomebly.template.android.module.ApplicationModule
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : BaseViewModel
 * Date : 2021-05-03, 오후 12:08
 * History
seq   date          contents      programmer
01.   2021-05-03    init            차태준
02.
03.
 */
@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    //viewModel에서 안전하게 context에 접근하기 위하여 applicationContext를 hilt로 주입
    @Inject lateinit var appContext: Context

    init {
        Logger.d("OnCreate ${this::class.java.simpleName}")
    }

    // 화면의 로딩 상태 제어 변수
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Loading progress toggle
     * isLoading 변수의 값을 변경
     * @param switch
     * 변경할 상태값 [true -> on, false -> off]
     */
    fun loadingProgressToggle(switch: Boolean){
        _isLoading.value = switch
    }

}