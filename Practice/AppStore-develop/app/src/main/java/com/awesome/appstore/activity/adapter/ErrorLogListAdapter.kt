package com.awesome.appstore.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.awesome.appstore.R
import com.awesome.appstore.database.AppInfo
import com.awesome.appstore.database.ErrorLog
import com.awesome.appstore.database.Notice
import com.awesome.appstore.databinding.ItemErrLogBinding
import com.awesome.appstore.databinding.ItemNoticeBinding
import kotlinx.android.synthetic.main.item_err_log.view.*
import kotlinx.android.synthetic.main.item_notice.view.*
import kotlinx.android.synthetic.main.item_notice.view.const_expand


class ErrorLogListAdapter(var onClick: (ErrorLog) -> Unit) :
    RecyclerView.Adapter<ErrorLogListAdapter.ErrorLogHolder>(){

    var items = arrayListOf<ErrorLog?>()

    class ErrorLogHolder(val binding: ItemErrLogBinding) : RecyclerView.ViewHolder(binding.root){
        var isExpand = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ErrorLogListAdapter.ErrorLogHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_err_log, parent, false)
        val viewHolder = ErrorLogListAdapter.ErrorLogHolder(ItemErrLogBinding.bind(view))
        view.setOnClickListener {
            onClick(items[viewHolder.adapterPosition]!!)
            if (!viewHolder.isExpand) {
                view.const_expand.visibility = View.VISIBLE
                viewHolder.isExpand = true
            } else {
                view.const_expand.visibility = View.GONE
                viewHolder.isExpand = false
            }
        }
        view.check_box.setOnClickListener {
            if (items[viewHolder.adapterPosition]?.check == 1){
                items[viewHolder.adapterPosition]?.check = 0
            }else{
                items[viewHolder.adapterPosition]?.check = 1
            }
//            notifyItemChanged(viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ErrorLogListAdapter.ErrorLogHolder, position: Int) {
        holder.binding.item = items[position]
    }
}