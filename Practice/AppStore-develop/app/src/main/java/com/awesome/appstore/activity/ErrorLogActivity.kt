package com.awesome.appstore.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.awesome.appstore.R
import com.awesome.appstore.activity.adapter.ErrorLogListAdapter
import com.awesome.appstore.activity.viewmodel.ErrorLogActivityViewModel
import com.awesome.appstore.database.ErrorLog
import com.awesome.appstore.databinding.ActivityErrorLogBinding
import com.awesome.appstore.module.viewmodels.ViewModelFactory
import com.orhanobut.logger.Logger
import javax.inject.Inject

class ErrorLogActivity : BaseActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var binding: ActivityErrorLogBinding
    private lateinit var viewModel: ErrorLogActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_error_log)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ErrorLogActivityViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recyclerErrorList.adapter = ErrorLogListAdapter({})
        binding.recyclerErrorList.layoutManager = LinearLayoutManager(this).apply { orientation = LinearLayoutManager.VERTICAL }

        binding.linearErrBack.setOnClickListener {
            onBackPressed()
        }

        binding.textAllCheck.setOnClickListener {
            binding.checkAll.callOnClick()
        }

        viewModel.errList.observe(this, {
            (binding.recyclerErrorList.adapter as ErrorLogListAdapter).items = it as ArrayList<ErrorLog?>
            (binding.recyclerErrorList.adapter as ErrorLogListAdapter).notifyDataSetChanged()
        })
        viewModel.onClickDelete.observe(this, {
            Logger.d(getCheckedLog())
            viewModel.deleteErrorLog(getCheckedLog())
        })
        viewModel.onClickSelectAll.observe(this, {
            if (getCheckedLog().size == (binding.recyclerErrorList.adapter as ErrorLogListAdapter).items.size) {
                (binding.recyclerErrorList.adapter as ErrorLogListAdapter).items.forEach { it?.check = 0 }
            } else {
                (binding.recyclerErrorList.adapter as ErrorLogListAdapter).items.forEach { it?.check = 1 }
            }
            (binding.recyclerErrorList.adapter as ErrorLogListAdapter).notifyDataSetChanged()
        })
        viewModel.onClickSubmit.observe(this,{
            Logger.d(getCheckedLog())
            viewModel.submitErrorLog(getCheckedLog())
        })
    }

    private fun getCheckedLog(): List<ErrorLog?> {
        return (binding.recyclerErrorList.adapter as ErrorLogListAdapter).items.filter { it?.check == 1 }
    }
}