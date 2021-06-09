package com.awesome.appstore.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.awesome.appstore.R
import com.awesome.appstore.database.AppInfo
import com.awesome.appstore.databinding.ItemAppBinding
import com.awesome.appstore.databinding.ItemLauncherAppBinding
import com.awesome.appstore.databinding.ItemSearchAppBinding
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_detail.view.*
import kotlinx.android.synthetic.main.item_app.view.*


class AppSearchResultAdapter(private val touchListener: TouchListener) :
    RecyclerView.Adapter<AppSearchResultAdapter.SearchResultHolder>(){
    interface TouchListener {
        fun onClickApp(model: AppInfo?)
//        fun onClickInstall(model: AppInfo?, position: Int)
//        fun onClickDelete(model: AppInfo?)
    }

    var items = arrayListOf<AppInfo?>()

    class SearchResultHolder(val binding: ItemSearchAppBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_app, parent, false)
        val viewHolder = SearchResultHolder(ItemSearchAppBinding.bind(view))
        view.const_item_frame.setOnClickListener {
            touchListener.onClickApp(items[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: SearchResultHolder, position: Int) {
        holder.binding.item = items[position]
        Logger.d("$items \n position: $position")
        Glide.with(holder.itemView)
            .load(items[position]?.iconUrl)
            .into(holder.itemView.findViewById<View>(R.id.image_app_thum) as ImageView)
    }
}