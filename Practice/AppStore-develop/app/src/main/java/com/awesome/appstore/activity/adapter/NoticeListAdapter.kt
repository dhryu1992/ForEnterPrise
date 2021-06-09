package com.awesome.appstore.activity.adapter

import android.graphics.drawable.Drawable
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.awesome.appstore.R
import com.awesome.appstore.database.Notice
import com.awesome.appstore.databinding.ItemNoticeBinding
import kotlinx.android.synthetic.main.item_notice.view.*


class NoticeListAdapter(var onClick: (Notice, Int) -> Unit) :
    RecyclerView.Adapter<NoticeListAdapter.NoticeHolder>() {

    var items = arrayListOf<Notice?>()

    class NoticeHolder(val binding: ItemNoticeBinding) : RecyclerView.ViewHolder(binding.root) {
        var isExpand = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notice, parent, false)
        val viewHolder = NoticeHolder(ItemNoticeBinding.bind(view))
        view.setOnClickListener {
            onClick(items[viewHolder.adapterPosition]!!, viewHolder.adapterPosition)
            view.image_notice_new.visibility = View.GONE

            if (!viewHolder.isExpand) {
                view.img_down.visibility = View.GONE
                view.img_up.visibility = View.VISIBLE
                view.const_expand.visibility = View.VISIBLE
                viewHolder.isExpand = true
            } else {
                view.img_down.visibility = View.VISIBLE
                view.img_up.visibility = View.GONE
                view.const_expand.visibility = View.GONE
                viewHolder.isExpand = false
            }
        }
        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: NoticeHolder, position: Int) {
        holder.binding.item = items[position]
        holder.binding.constExpand.visibility = View.GONE
        holder.binding.imgUp.visibility = View.GONE
        holder.binding.webTest.loadData(items[position]?.content!!, "text/html", "UTF-8")
    }
}