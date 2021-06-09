package com.awesomebly.template.android.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.awesomebly.template.android.R
import com.awesomebly.template.android.database.entity.TpEntity
import com.awesomebly.template.android.databinding.FirstFragmentBinding
import com.awesomebly.template.android.ui.main.MainActivity
import com.awesomebly.template.android.ui.main.adapter.TpAdapter
import com.awesomebly.template.android.ui.main.viewmodel.FirstFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirstFragment : Fragment() {
    private val viewModel: FirstFragmentViewModel by viewModels()
    lateinit var binding: FirstFragmentBinding
    lateinit var tpAdapter: TpAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).supportActionBar?.title = "Database Sample"
        binding = DataBindingUtil.inflate<FirstFragmentBinding>(inflater, R.layout.first_fragment, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@FirstFragment.viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerTp.apply {
            adapter = TpAdapter {
                // 아이템 선택시 해당 아이템의 아이디 값을 파라미터로 하여 SecondFragment 이동
                findNavController().navigate(FirstFragmentDirections.actionFirstFragmentToSecondFragment(it.id.toInt()))
            }
            layoutManager = LinearLayoutManager(requireContext()).apply { orientation = LinearLayoutManager.VERTICAL }
            visibility = View.VISIBLE
        }.also { tpAdapter = it.adapter as TpAdapter }

        // tpList 관측 하여 RecyclerView 갱신
        viewModel.tpList.observe(requireActivity()) {
            tpAdapter.run {
                items = it as ArrayList<TpEntity?>
                notifyDataSetChanged()
            }
        }

        viewModel.selectAllTp()

        //SAVE 버튼 클릭
        viewModel.btnDbSave.observe(requireActivity()) {
            if (binding.editTpName.text?.isNotEmpty() == true)
                viewModel.insertTp(binding.editTpName.text.toString())
            else Toast.makeText(requireContext(), getString(R.string.hint_tp_name), Toast.LENGTH_SHORT).show()
        }
    }
}