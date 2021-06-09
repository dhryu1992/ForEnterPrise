package com.awesome.appstore.activity.adapter

import android.annotation.SuppressLint
import android.content.Context.VIBRATOR_SERVICE
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorSpace
import android.graphics.drawable.Drawable
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.awesome.appstore.R
import com.awesome.appstore.activity.fragment.TabExecuteFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.item_launcher_app.view.*

class LauncherItemMoveCallback(
    private val listener: ItemMoveListener,
    private val fragment: TabExecuteFragment
) : ItemTouchHelper.Callback() {
    private var deletedApp = false
    private lateinit var curHolder: RecyclerView.ViewHolder
    private var curPos: Int = 0
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        curPos = target.adapterPosition
        Logger.d("onMove")
        return if (!deletedApp) {
            listener.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        } else true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
//        super.onSelectedChanged(viewHolder, actionState);
        deletedApp = false
        fragment.binding.fabDeleteApp.visibility = View.VISIBLE
        Logger.d(actionState)
        if (ItemTouchHelper.ACTION_STATE_DRAG == actionState) {
            curHolder = viewHolder!!
            curPos = curHolder.adapterPosition
            Logger.d("Selected")
            listener.onItemDrag(curPos, viewHolder)
            val effect = VibrationEffect.createOneShot(100, 100)
            (fragment.requireActivity().getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(effect)
            //추후 아이콘 크기 증가, 진동 - 디자인 기획상 아이콘 크기 증가는 안 함
        }
        if (ItemTouchHelper.ACTION_STATE_IDLE == actionState) {
            //아이콘 크기 복원
            Logger.d("Dropped")
            listener.onItemDropped()
            if (isViewOverlap(curHolder.itemView, fragment.binding.fabDeleteApp)) {
                deletedApp = true
                listener.confirmDelete(curPos)
            }

        }
    }

    private fun isViewOverlap(firstView: View, secondView: View): Boolean {
        val firstPosition = IntArray(2)
        val secondPosition = IntArray(2)
        firstView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        firstView.getLocationOnScreen(firstPosition)
        secondView.getLocationOnScreen(secondPosition)
//        Logger.d(firstPosition)
//        Logger.d(secondPosition)
        val r = firstView.measuredHeight + firstPosition[1]
        val l = secondPosition[1]
        return r >= l && r != 0 && l != 0
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
//        fragment.binding.fabDeleteApp.setTextColor(Color.BLACK)
//        fragment.binding.fabDeleteApp.setIconTintResource(R.color.black)
        fragment.binding.fabDeleteApp.visibility = View.INVISIBLE
    }

    @SuppressLint("ResourceAsColor")
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
//        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//        Logger.d("onChildDraw")
        viewHolder.itemView.translationY = dY
        viewHolder.itemView.translationX = dX
        Logger.d("actionState : $actionState currentActive : $isCurrentlyActive")
        if (isCurrentlyActive){
            viewHolder.itemView.findViewById<ConstraintLayout>(R.id.const_app_icon_stroke)?.apply {
                isPressed = true
            }
        }else{
            viewHolder.itemView.findViewById<ConstraintLayout>(R.id.const_app_icon_stroke)?.apply {
                isPressed = false
            }
        }
        if (isViewOverlap(curHolder.itemView, fragment.binding.fabDeleteApp)) {
            Logger.d("overlap!!!!!!!!!!!!")
//            fragment.binding.fabDeleteApp.setTextColor(Color.RED)
//            fragment.binding.fabDeleteApp.setIconTintResource(R.color.red)
            fragment.binding.imageDeleteIcon.setImageResource(R.drawable.ic_delete_on)
        } else {
            fragment.binding.imageDeleteIcon.setImageResource(R.drawable.btn_delete_off)
//            fragment.binding.fabDeleteApp.setTextColor(Color.BLACK)
//            fragment.binding.fabDeleteApp.setIconTintResource(R.color.black)
        }
    }


}