package com.awesome.appstore.activity.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.awesome.appstore.R
import com.awesome.appstore.activity.DetailActivity
import com.awesome.appstore.activity.adapter.AppSearchResultAdapter
import com.awesome.appstore.config.StringTAG
import com.awesome.appstore.database.AppInfo
import com.awesome.appstore.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    lateinit var binding : FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.recyclerSearchResult.layoutManager = LinearLayoutManager(requireContext()).apply { orientation = LinearLayoutManager.VERTICAL }
        binding.recyclerSearchResult.adapter = AppSearchResultAdapter(object :AppSearchResultAdapter.TouchListener{
            override fun onClickApp(model: AppInfo?) {
                Intent(requireContext(),DetailActivity::class.java)
                    .apply { putExtra(StringTAG.EXTRA_KEY_APP_INFO, model) }
                    .let {
                    startActivity(it)
                }
            }
        })
        return binding.root
    }

    fun setSearchData(appList: List<AppInfo?>?){
        (binding.recyclerSearchResult.adapter as AppSearchResultAdapter).items = appList as ArrayList<AppInfo?>
        (binding.recyclerSearchResult.adapter as AppSearchResultAdapter).notifyDataSetChanged()
    }
}