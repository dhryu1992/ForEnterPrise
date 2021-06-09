package com.awesome.appstore.activity.adapter

import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import androidx.databinding.BindingAdapter
import com.awesome.appstore.R
import com.orhanobut.logger.Logger

object LoginBindingAdapter {

    @JvmStatic
    @BindingAdapter("isLinearFadIn")
    fun isLinearFadIn(view: View, isLinearFadIn: Boolean) {
        Logger.d("isLinearFadIn")
        val fadIn = AnimationUtils.loadAnimation(view.context, R.anim.fade_in_slow)
        fadIn.duration = 1000
        fadIn.fillAfter = true
        view.startAnimation(fadIn)
        Handler().postDelayed({ view.visibility = View.VISIBLE }, 1000)
    }

    @JvmStatic
    @BindingAdapter("isLinearFadOut")
    fun isLinearFadOut(view: View, isLinearFadOut: Boolean) {
        Logger.d("isLinearFadIn")
        val fadeOut = AnimationUtils.loadAnimation(view.context, R.anim.fade_out_slow)
        fadeOut.duration = 1000
        fadeOut.fillAfter = true
        view.startAnimation(fadeOut)

    }
}