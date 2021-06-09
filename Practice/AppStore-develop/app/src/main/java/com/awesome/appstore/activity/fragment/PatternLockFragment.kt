package com.awesome.appstore.activity.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.awesome.appstore.R
import kotlinx.android.synthetic.main.fragment_pattern_lock.*
import kotlinx.android.synthetic.main.fragment_pattern_lock.view.*


class PatternLockFragment : Fragment(), PatternLockViewListener {
    var patternDrawing: ((String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pattern_lock, container, false).apply {
            lock_pattern_view.addPatternLockListener(this@PatternLockFragment)
        }
    }
    override fun onStarted() {
    }

    override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {
    }

    override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
        val password = PatternLockUtils.patternToMD5(lock_pattern_view, pattern)
        patternDrawing?.invoke(password)
    }
    override fun onCleared() {
    }
}