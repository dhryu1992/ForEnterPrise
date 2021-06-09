package com.awesome.appstore.activity.adapter

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.awesome.appstore.R
import com.awesome.appstore.activity.fragment.TabAllFragment
import com.awesome.appstore.activity.fragment.TabEssentialFragment
import com.awesome.appstore.activity.fragment.TabExecuteFragment
import com.awesome.appstore.activity.fragment.TabUpdateFragment

class SectionsPagerAdapter(private val context: Context, private val fm: FragmentManager) :
    FragmentPagerAdapter(fm) {
    @StringRes
    private val TAB_TITLES = intArrayOf(
        R.string.tab_title_1,
        R.string.tab_title_2,
        R.string.tab_title_3,
        R.string.tab_title_4
    )
    override fun getCount(): Int {
        return TAB_TITLES.size
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> TabExecuteFragment.newInstance(position + 1)
            1 -> TabAllFragment.newInstance(position + 1)
            2 -> TabEssentialFragment.newInstance(position + 1)
            3 -> TabUpdateFragment.newInstance(position + 1)
            else -> TabExecuteFragment.newInstance(1)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.getString(TAB_TITLES[position])
    }

}