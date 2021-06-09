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
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.item_app.view.*


class AppListAdapter(private val touchListener: TouchListener) :
    RecyclerView.Adapter<AppListAdapter.AppIconHolder>(), ItemMoveListener {
    interface TouchListener {
        fun onClickApp(model: AppInfo?)
        fun onClickInstall(model: AppInfo?, position: Int)
//        fun onClickDelete(model: AppInfo?)
    }

    var items = arrayListOf<AppInfo?>()
        set(value) {
            field = value.filter { it?.appStatus == "A" } as ArrayList<AppInfo?>
        }

    class AppIconHolder(val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppIconHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        val viewHolder = AppIconHolder(ItemAppBinding.bind(view))
        view.const_item_frame.setOnClickListener {
            touchListener.onClickApp(items[viewHolder.adapterPosition])
        }
        view.btn_app_install.setOnClickListener{
            touchListener.onClickInstall(items[viewHolder.adapterPosition],viewHolder.adapterPosition)
        }
        view.btn_app_update.setOnClickListener{
            touchListener.onClickInstall(items[viewHolder.adapterPosition],viewHolder.adapterPosition)
        }
//        view.btn_app_delete.setOnClickListener{
//            touchListener.onClickDelete(items[viewHolder.adapterPosition])
//        }
        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: AppIconHolder, position: Int) {
        holder.binding.item = items[position]
        Glide.with(holder.itemView)
            .load(items[position]?.iconUrl)
            .into(holder.itemView.findViewById<View>(R.id.image_app_thum) as ImageView)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        val appInfo: AppInfo? = items[fromPosition]
        items.removeAt(fromPosition)
        items.add(toPosition, appInfo)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(adapterPosition: Int) {
    }

    override fun confirmDelete(curPos: Int) {
    }

    override fun onItemDrag(adapterPosition: Int, holder: RecyclerView.ViewHolder) {
    }

    override fun onItemDropped() {
    }
}