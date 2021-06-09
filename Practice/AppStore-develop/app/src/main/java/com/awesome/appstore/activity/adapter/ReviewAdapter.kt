package com.awesome.appstore.activity.adapter

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.awesome.appstore.R
import com.awesome.appstore.databinding.ItemAppImgBinding
import com.awesome.appstore.databinding.ItemReviewBinding
import com.awesome.appstore.protocol.response.Review
import com.awesome.appstore.util.StorePreference
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_review.view.*
import javax.inject.Inject


class ReviewAdapter(val sawonNum : String, val onClickEdit: (review: Review) -> Unit, val onClickDelete: (review: Review) -> Unit) :
    RecyclerView.Adapter<ReviewAdapter.ReviewHolder>() {

    var items = arrayListOf<Review?>()

    class ReviewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapter.ReviewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        val viewHolder = ReviewAdapter.ReviewHolder(ItemReviewBinding.bind(view))

        try{
            if(items[viewHolder.adapterPosition]?.reviewer?.sawonNum == sawonNum){
                view.const_my_review_btn.visibility = View.VISIBLE
            }
        }catch (e: Exception){}

        view.text_review_delete.setOnClickListener {
            onClickDelete(items[viewHolder.adapterPosition]!!)
        }
        view.text_review_edit.setOnClickListener {
            onClickEdit(items[viewHolder.adapterPosition]!!)
        }
        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ReviewAdapter.ReviewHolder, position: Int) {
        holder.binding.item = items[position]
    }
}