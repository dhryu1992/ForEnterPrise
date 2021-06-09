package com.awesomebly.template.android.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import com.awesomebly.template.android.R
import com.awesomebly.template.android.databinding.FirstFragmentBinding
import com.awesomebly.template.android.databinding.FragmentSecondBinding
import com.awesomebly.template.android.network.response.ResponsePosts
import com.awesomebly.template.android.ui.main.MainActivity
import com.awesomebly.template.android.ui.main.viewmodel.FirstFragmentViewModel
import com.awesomebly.template.android.ui.main.viewmodel.SecondFragmentViewModel
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecondFragment() : Fragment() {
    private val args: SecondFragmentArgs by navArgs()
    private val viewModel: SecondFragmentViewModel by viewModels()
    lateinit var binding: FragmentSecondBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Logger.d("argsSample : ${args.argsSample}")
        (activity as MainActivity).supportActionBar?.title = "Network Sample"
        binding = DataBindingUtil.inflate<FragmentSecondBinding>(inflater, R.layout.fragment_second, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@SecondFragment.viewModel
        }
        // 데이터 수신을 확인하면 프로그래스바를 숨긴다
        viewModel.post.observe(requireActivity()) {
            viewModel.loadingProgressToggle(false)
        }
        viewModel.run {
            getPost(args.argsSample)
            loadingProgressToggle(true)
        }
        return binding.root
    }
}