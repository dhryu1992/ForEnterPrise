package com.awesome.appstore.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.awesome.appstore.R
import com.awesome.appstore.databinding.ItemAppImgBinding
import com.awesome.appstore.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class AppImgAdapter(val onClick: (url: ArrayList<String>, index: Int) -> Unit) :
    RecyclerView.Adapter<AppImgAdapter.AppImgHolder>() {

    var items = arrayListOf<String>()

    class AppImgHolder(val binding: ItemAppImgBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppImgHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app_img, parent, false)
        val viewHolder = AppImgHolder(ItemAppImgBinding.bind(view))
        view.setOnClickListener {
            onClick(items, viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: AppImgHolder, position: Int) {
//        holder.binding.item = items[position]
        Glide.with(holder.itemView)
            .load(items[position])
            .apply(RequestOptions.bitmapTransform(RoundedCorners(Utils().dpToPx(10))))
            .into(holder.itemView.findViewById<View>(R.id.image_app_use) as ImageView)
    }
}