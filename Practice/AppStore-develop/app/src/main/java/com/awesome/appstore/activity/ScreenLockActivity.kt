package com.awesome.appstore.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.andrognito.patternlockview.PatternLockView
import com.awesome.appstore.R
import com.awesome.appstore.activity.fragment.PatternLockFragment
import com.awesome.appstore.activity.fragment.PinLockFragment
import com.awesome.appstore.config.StringTAG
import com.awesome.appstore.config.StringTAG.Companion.ADD_LOCK
import com.awesome.appstore.config.StringTAG.Companion.EXTRA_KEY_LOCK_MODE
import com.awesome.appstore.config.StringTAG.Companion.EXTRA_KEY_LOCK_TYPE
import com.awesome.appstore.config.StringTAG.Companion.LOCK_PATTERN
import com.awesome.appstore.config.StringTAG.Companion.LOCK_PIN
import com.awesome.appstore.util.lock.Lock
import com.awesome.appstore.util.lock.LockManager
import com.google.android.material.snackbar.Snackbar
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_screen_lock.*
import kotlinx.android.synthetic.main.fragment_pattern_lock.*
import kotlinx.android.synthetic.main.fragment_pin_lock.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.prefs.Preferences

//private var lastUnlocked = 0L
class ScreenLockActivity : AppCompatActivity() {
    private lateinit var lockManager: LockManager
    private var mode: String? = null

    companion object {
        fun start(context: Context, addLock: Boolean, type: String?) {
            val intent = Intent(context, ScreenLockActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                if (addLock) {
                    putExtra(EXTRA_KEY_LOCK_MODE, ADD_LOCK)
                    putExtra(EXTRA_KEY_LOCK_TYPE, type)
                }
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_lock)
        lockManager = LockManager(this)
        mode = intent.getStringExtra(EXTRA_KEY_LOCK_MODE)
//        mode = "ADD_LOCK"
        val force = intent.getBooleanExtra("force", false)
        when (mode) {
            null -> {
                if (lockManager.isEmpty()) {
                    setResult(RESULT_OK)
                    finish()
                    return
                }

//                if (System.currentTimeMillis() - lastUnlocked < 5*60*1000 && !force) {
//                    lastUnlocked = System.currentTimeMillis()
//                    setResult(RESULT_OK)
//                    finish()
//                    return
//                }

                lock_pattern.apply {
                    isEnabled = lockManager.contains(Lock.Type.PATTERN)
                    if (!isEnabled) visibility = View.GONE
                    setOnClickListener {
                        supportFragmentManager.beginTransaction().replace(
                            R.id.lock_content, patternLockFragment
                        ).commit()
                    }
                }
                lock_pin.apply {
                    isEnabled = lockManager.contains(Lock.Type.PIN)
                    if (!isEnabled) visibility = View.GONE
                    setOnClickListener {
                        supportFragmentManager.beginTransaction().replace(
                            R.id.lock_content, pinLockFragment
                        ).commit()
                    }
                }

                when (lockManager.locks!!.first().type) {
                    Lock.Type.PIN -> {
                        supportFragmentManager.beginTransaction().add(
                            R.id.lock_content, pinLockFragment
                        ).commit()
                    }
                    Lock.Type.PATTERN -> {
                        supportFragmentManager.beginTransaction().add(
                            R.id.lock_content, patternLockFragment
                        ).commit()
                    }
                    else -> return
                }
            }
            ADD_LOCK -> {
                lock_pattern.isVisible = false
                lock_pin.isVisible = false

                when (intent.getStringExtra(EXTRA_KEY_LOCK_TYPE)!!) {
                    LOCK_PATTERN -> {
                        lock_pattern.isEnabled = true
                        supportFragmentManager.beginTransaction().add(
                            R.id.lock_content, patternLockFragment
                        ).commit()
                    }
                    LOCK_PIN -> {
                        lock_pin.isEnabled = true
                        supportFragmentManager.beginTransaction().add(
                            R.id.lock_content, pinLockFragment
                        ).commit()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        when (mode) {
            ADD_LOCK -> {
                finish()
            }
            else -> {
                moveTaskToBack(true)
            }
        }
    }


    private val patternLockFragment = PatternLockFragment().apply {
        var lastPass = ""
        patternDrawing = {
            when (mode) {
                null -> {
                    val result = lockManager.check(it)

                    if (result == true) {
//                        lastUnlocked = System.currentTimeMillis()
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        lock_pattern_view.setViewMode(PatternLockView.PatternViewMode.WRONG)
                        text_type1.visibility = View.GONE
                        text_type2.visibility = View.GONE
                        text_type3.visibility = View.VISIBLE
                    }
                }
                ADD_LOCK -> {
                    if (lastPass.isEmpty()) {
                        lastPass = it
                        lock_pattern_view.clearPattern()
                        text_type1.visibility = View.GONE
                        text_type2.visibility = View.VISIBLE
                        text_type3.visibility = View.GONE
//                        Snackbar.make(view!!, "동일한 패턴을 한번 더 입력해주세요", Snackbar.LENGTH_LONG).show()
                    } else {
                        if (lastPass == it) {
                            LockManager(context!!).add(Lock.generate(Lock.Type.PATTERN, it))
                            finish()
                        } else {
                            lock_pattern_view.setViewMode(PatternLockView.PatternViewMode.WRONG)
                            text_type1.visibility = View.GONE
                            text_type2.visibility = View.GONE
                            text_type3.visibility = View.VISIBLE
                            lastPass = ""
//                            Snackbar.make(view!!, "패턴이 일치하지 않습니다. 다시 입력해 주세요", Snackbar.LENGTH_LONG).show()

                        }
                    }
                }
            }
        }
    }
    private val pinLockFragment = PinLockFragment().apply {
        val pinFragment = this
        var lastPass = ""
        onPinEntered = {
            when (mode) {
                null -> {
                    val result = lockManager.check(it)
                    if (result == true) {
//                        lastUnlocked = System.currentTimeMillis()
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        pinFragment.binding.indicatorDots.startAnimation(
                            AnimationUtils.loadAnimation(
                                context,
                                R.anim.shake
                            ).apply {
                                setAnimationListener(object : Animation.AnimationListener {
                                    override fun onAnimationEnd(animation: Animation?) {
                                        pinFragment.binding.pinLockView.resetPinLockView()
                                        pinFragment.binding.pinLockView.isEnabled = true
                                    }

                                    override fun onAnimationStart(animation: Animation?) {
                                        pinFragment.binding.pinLockView.isEnabled = false
                                    }

                                    override fun onAnimationRepeat(animation: Animation?) {
                                    }
                                })
                            })
                        text_pin.visibility = View.GONE
                        text_pin_retry.visibility = View.GONE
                        text_pin_error.visibility = View.VISIBLE
                    }
                }
                ADD_LOCK -> {
                    if (lastPass.isEmpty()) {
                        lastPass = it
                        pinFragment.binding.pinLockView.resetPinLockView()
                        text_pin.visibility = View.GONE
                        text_pin_retry.visibility = View.VISIBLE
                        text_pin_error.visibility = View.GONE
//                        Snackbar.make(view!!, "동일한 PIN을 한번더 입력해 주세요", Snackbar.LENGTH_LONG).show()
                    } else {
                        if (lastPass == it) {
                            LockManager(context!!).add(Lock.generate(Lock.Type.PIN, it))
                            finish()
                        } else {
                            pinFragment.binding.indicatorDots.startAnimation(
                                AnimationUtils.loadAnimation(
                                    context,
                                    R.anim.shake
                                ).apply {
                                    setAnimationListener(object : Animation.AnimationListener {
                                        override fun onAnimationEnd(animation: Animation?) {
                                            pinFragment.binding.pinLockView.resetPinLockView()
                                            pinFragment.binding.pinLockView.isEnabled = true
                                        }

                                        override fun onAnimationStart(animation: Animation?) {
                                            pinFragment.binding.pinLockView.isEnabled = false
                                        }

                                        override fun onAnimationRepeat(animation: Animation?) {
                                            // Do Nothing
                                        }
                                    })
                                })
                            lastPass = ""
                            text_pin.visibility = View.GONE
                            text_pin_retry.visibility = View.GONE
                            text_pin_error.visibility = View.VISIBLE
//                            Snackbar.make(view!!, "PIN이 일치하지 않습니다. 다시 시도해주세요", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }


}