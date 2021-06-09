package com.awesome.appstore.activity

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesome.appstore.R
import com.awesome.appstore.activity.adapter.NoticeListAdapter
import com.awesome.appstore.activity.adapter.ReviewAdapter
import com.awesome.appstore.activity.viewmodel.DetailActivityViewModel
import com.awesome.appstore.activity.viewmodel.NoticeActivityViewModel
import com.awesome.appstore.database.Notice
import com.awesome.appstore.databinding.ActivityNoticeBinding
import com.awesome.appstore.module.viewmodels.ViewModelFactory
import com.orhanobut.logger.Logger
import kotlinx.coroutines.delay
import javax.inject.Inject

class NoticeActivity : BaseActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var viewModel: NoticeActivityViewModel
    lateinit var binding: ActivityNoticeBinding

//    @SuppressLint("UseCompatLoadingForDrawables")
//    val imageGetter = Html.ImageGetter { source ->
//        val resourceId: Int = resources.getIdentifier(source, "drawable", packageName)
//        val drawable: Drawable = resources.getDrawable(resourceId, null)
//        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
//        drawable
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notice)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(NoticeActivityViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.onClickExit.observe(this, {
            if (it.getContentIfNotHandled()!!) {
                finish()
            }
        })

        binding.recyclerNotice.adapter = NoticeListAdapter { notice, position ->
            viewModel.setNoticeReadState(notice)
            binding.recyclerNotice.smoothScrollToPosition(position + 1)
        }

        viewModel.noticeList.observe(this, {
            if ((binding.recyclerNotice.adapter as NoticeListAdapter).items.isNullOrEmpty()) {
                (binding.recyclerNotice.adapter as NoticeListAdapter).items = it as ArrayList<Notice?>
                (binding.recyclerNotice.adapter as NoticeListAdapter).notifyDataSetChanged()
            } else {
                val size = (binding.recyclerNotice.adapter as NoticeListAdapter).items.size
                (binding.recyclerNotice.adapter as NoticeListAdapter).items.plusAssign(it as ArrayList<Notice?>)
                (binding.recyclerNotice.adapter as NoticeListAdapter).notifyItemRangeInserted(size, it.size )
                binding.progressNotice.visibility = View.GONE
            }
        })

        binding.recyclerNotice.layoutManager = LinearLayoutManager(this).also { it.orientation = RecyclerView.VERTICAL }
        binding.recyclerNotice.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastViewPosition = (binding.recyclerNotice.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                Logger.d("lastViewPosition : $lastViewPosition")
                if ((binding.recyclerNotice.adapter as NoticeListAdapter).itemCount == lastViewPosition + 1) {
                    Logger.d("itemCount : ${(binding.recyclerNotice.adapter as NoticeListAdapter).itemCount}")
                    if (viewModel.pagingInfo.value?.page!! < viewModel.pagingInfo.value?.totalPage!!){
                        binding.progressNotice.visibility = View.VISIBLE
                        viewModel.getNoticeList(viewModel.pagingInfo.value?.page!! + 1)
                    }
//                    binding.recyclerNotice.removeOnScrollListener(this)
                }
            }
        })
        binding.recyclerNotice.visibility = View.VISIBLE


        viewModel.tokenExpireDialog.observe(this, {
            tokenExpireDialog()
        })
    }
}