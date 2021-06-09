package com.awesomebly.template.android.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.awesomebly.template.android.R
import com.awesomebly.template.android.database.entity.TpEntity
import com.awesomebly.template.android.databinding.ItemTpBinding

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : tpAdapter
 * Date : 2021-05-04, 오후 3:36
 * History
seq   date          contents      programmer
01.   2021-05-04                    차태준
02.
03.
 */
class TpAdapter(var touchListener: (TpEntity) -> Unit) : RecyclerView.Adapter<TpAdapter.TpHolder>() {

    class TpHolder(val binding: ItemTpBinding) : RecyclerView.ViewHolder(binding.root)

    var items = arrayListOf<TpEntity?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TpHolder {
        val holder = LayoutInflater.from(parent.context).inflate(R.layout.item_tp, parent, false).let {
            TpHolder(ItemTpBinding.bind(it))
        }
        // 아이템 클릭 이벤트
        holder.binding.root.setOnClickListener {
            items[holder.adapterPosition]?.let { touchListener(it) }
        }
        return holder
    }

    override fun onBindViewHolder(holder: TpHolder, position: Int) {
        holder.binding.item = items[position]
    }

    override fun getItemCount() = items.size
}