package com.awesome.appstore.activity.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.andrognito.pinlockview.PinLockListener
import com.awesome.appstore.R
import com.awesome.appstore.databinding.FragmentPinLockBinding
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_pin_lock.*


class PinLockFragment : Fragment(), PinLockListener {
    var onPinEntered: ((String) -> Unit)? = null
    lateinit var binding : FragmentPinLockBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pin_lock, container, false)
        binding.pinLockView.attachIndicatorDots(binding.indicatorDots)
        binding.pinLockView.setPinLockListener(this@PinLockFragment)
        return binding.root
    }

    override fun onComplete(pin: String?) {
        Logger.d("PinComplete")
        onPinEntered?.invoke(pin!!)
    }

    override fun onEmpty() {
    }

    override fun onPinChange(pinLength: Int, intermediatePin: String?) {
    }

}