package com.awesome.appstore.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.awesome.appstore.R
import com.awesome.appstore.activity.adapter.ImageSliderAdapter
import com.awesome.appstore.config.StringTAG
import com.awesome.appstore.databinding.ActivityFullSizeImageBinding
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_full_size_image.*

class FullSizeImageActivity : AppCompatActivity() {
    lateinit var binding: ActivityFullSizeImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_size_image)

        val index = intent.getIntExtra(StringTAG.EXTRA_IMAGE_INDEX, 0)
        val imageList = intent.getStringArrayListExtra(StringTAG.EXTRA_IMAGE_URL) as ArrayList<String>

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        binding.viewPagerImageSlider.adapter = ImageSliderAdapter(this, imageList, index)
        binding.viewPagerImageSlider.currentItem = index
    }
}