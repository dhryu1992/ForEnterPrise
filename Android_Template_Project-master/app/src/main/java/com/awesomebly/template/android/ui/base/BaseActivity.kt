package com.awesomebly.template.android.ui.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : BaseActivity
 * Date : 2021-05-03, 오후 12:03
 * History
seq   date          contents      programmer
01.   2021-05-03    init            차태준
02.
03.
 */
@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d("OnCreate ${this::class.java.simpleName}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d("OnDestroy ${this::class.java.simpleName}")
    }


}

