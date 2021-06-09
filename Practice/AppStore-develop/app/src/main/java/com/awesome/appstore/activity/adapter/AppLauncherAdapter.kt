package com.awesome.appstore.activity.adapter

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.*
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.awesome.appstore.AppStoreApplication
import com.awesome.appstore.R
import com.awesome.appstore.database.AppInfo
import com.awesome.appstore.database.PushCount
import com.awesome.appstore.databinding.ItemLauncherAppBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.orhanobut.logger.Logger
import javax.inject.Inject


class AppLauncherAdapter(private val appListener: LauncherListener) :
    RecyclerView.Adapter<AppLauncherAdapter.AppIconHolder>(), ItemMoveListener {

    interface LauncherListener {
        fun onClickApp(model: AppInfo?)
        fun onClickDelete(appInfo: AppInfo?)
        fun onAppMove(appInfo: ArrayList<AppInfo?>)
    }

    var items = arrayListOf<AppInfo?>()

    var badgeCountList = arrayListOf<PushCount?>()

    lateinit var dragHolder: RecyclerView.ViewHolder
    var dragPosition: Int = 0

    class AppIconHolder(val binding: ItemLauncherAppBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppIconHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_launcher_app, parent, false)
        val viewHolder = AppIconHolder(ItemLauncherAppBinding.bind(view))
        view.setOnClickListener {
            appListener.onClickApp(items[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: AppIconHolder, position: Int) {
        holder.binding.item = items[position]
        val badgeCount = badgeCountList.filter { it?.packageName == items[position]?.packageName }
        if (badgeCount.isNotEmpty()) {
            holder.binding.badge = badgeCount.first()
        }
        Glide.with(holder.itemView)
            .load(items[position]?.iconUrl)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(dpToPx(5))))
//            .override(dpToPx(62), dpToPx(62))
            .into(holder.itemView.findViewById<View>(R.id.image_app_icon) as ImageView)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        val appInfo: AppInfo? = items[fromPosition]
        items.removeAt(fromPosition)
        items.add(toPosition, appInfo)
        appListener.onAppMove(items)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(adapterPosition: Int) {
        Logger.d("onItemDismiss")
    }

    override fun confirmDelete(curPos: Int) {
        Logger.d(curPos)
        appListener.onClickDelete(items[curPos])
    }

    override fun onItemDrag(adapterPosition: Int, holder: RecyclerView.ViewHolder) {
        Logger.d("onItemDrag")
        dragHolder = holder
        dragPosition = adapterPosition
        holder.itemView.findViewById<View>(R.id.text_app_title).visibility = View.GONE
//        Glide.with(holder.itemView)
//            .load(items[adapterPosition]?.iconUrl)
//            .apply(RequestOptions.bitmapTransform(RoundedCorners(5)))
//            .override(dpToPx(70), dpToPx(70))
//            .into(holder.itemView.findViewById<View>(R.id.image_app_icon) as ImageView)
    }

    override fun onItemDropped() {
        Logger.d("onItemDropped")
        dragHolder.itemView.findViewById<View>(R.id.text_app_title).visibility = View.VISIBLE

//        Glide.with(dragHolder.itemView)
//            .load(items[dragPosition]?.iconUrl)
//            .override(dpToPx(63), dpToPx(63))
//            .apply(RequestOptions.bitmapTransform(RoundedCorners(dpToPx(5))))
//            .into(dragHolder.itemView.findViewById<View>(R.id.image_app_icon) as ImageView)
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}