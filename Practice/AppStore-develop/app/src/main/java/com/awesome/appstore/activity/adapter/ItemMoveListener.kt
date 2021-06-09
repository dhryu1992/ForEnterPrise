package com.awesome.appstore.activity.adapter

import androidx.recyclerview.widget.RecyclerView

interface ItemMoveListener {

    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean

    fun onItemDismiss(adapterPosition: Int)

    fun confirmDelete(curPos: Int)

    fun onItemDrag(adapterPosition: Int, holder: RecyclerView.ViewHolder)

    fun onItemDropped()
}