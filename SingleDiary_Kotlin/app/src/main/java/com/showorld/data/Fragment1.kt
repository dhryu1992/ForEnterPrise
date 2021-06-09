package com.showorld.data

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.showorld.R
import lib.kingja.switchbutton.SwitchMultiButton
import java.text.SimpleDateFormat

const val TAG = "Fragment1"

class Fragment1 : Fragment() {

    var recyclerView: RecyclerView? = null
    var adapter: NoteAdapter? = null

    var context1: Context? = null

    var listener: OnTabItemSelectedListener? = null

    var todayDateFormat: SimpleDateFormat? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        this.context1 = context

        if (context is OnTabItemSelectedListener) {
            listener = context
        }
    }



    override fun onDetach() {

        super.onDetach()
    if (context1 != null) {
        context1 = null
        listener = null
    }
}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment1, container, false) as ViewGroup

        initUI(rootView)

        return rootView

    }

    private fun initUI(rootView: ViewGroup) {

        val todayWriteButton = rootView.findViewById<Button>(R.id.todayWriteButton)
        todayWriteButton.setOnClickListener {
            if (listener != null) {
                listener!!.onTabSelected(1)
            }
        }

        val switchButton: SwitchMultiButton = rootView.findViewById(R.id.switchButton)
        switchButton.setOnSwitchListener { position, tabText ->
            //Toast.makeText(getContext(), tabText, Toast.LENGTH_SHORT).show();
            adapter.switchLayout(position)
            adapter!!.notifyDataSetChanged()
        }

    }
}