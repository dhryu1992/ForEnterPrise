package com.awesome.appstore.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.awesome.appstore.R
import com.awesome.appstore.activity.FullSizeImageActivity
import com.awesome.appstore.databinding.ItemImageSliderBinding
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger

class ImageSliderAdapter(private val context: Context, private val imageList: ArrayList<String>,private val index: Int) : PagerAdapter() {

    private var layoutInflater: LayoutInflater? = null

    override fun getCount(): Int {
        return imageList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = layoutInflater!!.inflate(R.layout.item_image_slider, null)
        val imageBinding = ItemImageSliderBinding.bind(v)
        val imageView = imageBinding.imageSliderItem
        imageBinding.textIndex.text = "${position+1}/${imageList.size}"
        imageBinding.floatingExitBtn.setOnClickListener {
            (context as FullSizeImageActivity).finish()
        }
        imageBinding.imageSliderItem.setOnClickListener {
            if(imageBinding.textIndex.visibility == View.GONE){
                imageBinding.textIndex.visibility = View.VISIBLE
                imageBinding.floatingExitBtn.visibility = View.VISIBLE
//                imageBinding.linearExitBtn.visibility = View.VISIBLE
            }else{
                imageBinding.textIndex.visibility = View.GONE
                imageBinding.floatingExitBtn.visibility = View.GONE
//                imageBinding.linearExitBtn.visibility = View.GONE
            }
        }
        Glide.with(context)
            .load(imageList[position])
            .into(imageView)
        destroyItem(container, position, v)
        (container as ViewPager).addView(v, 0)
        return v
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }
}