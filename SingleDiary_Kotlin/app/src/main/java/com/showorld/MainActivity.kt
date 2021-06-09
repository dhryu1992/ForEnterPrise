package com.showorld

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.showorld.data.Fragment1
import com.showorld.data.MyApplication
import com.showorld.data.OnRequestListener
import com.showorld.data.OnTabItemSelectedListener

class MainActivity : AppCompatActivity(), OnTabItemSelectedListener, OnRequestListener, MyApplication.OnResponseListener {
    private val TAG = "MainActivity"

    var fragment1: Fragment1? = null
    var fragment2: Fragment2? = null
    var fragment3: Fragment3? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onTabSelected(position: Int) {
        TODO("Not yet implemented")
    }

    override fun showFragment2(item: Note) {
        TODO("Not yet implemented")
    }

    override fun onRequest(command: String) {
        TODO("Not yet implemented")
    }

    override fun processResponse(requestCode: Int, responseCode: Int, response: String?) {
        TODO("Not yet implemented")
    }
}