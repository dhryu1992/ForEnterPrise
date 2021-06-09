package com.example.recyclerview_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val dataArray: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addDataArray()

        rv_data_list.layoutManager = LinearLayoutManager(this)
        rv_data_list.adapter = DataAdapter(dataArray, this)
    }

    private fun addDataArray() {
        dataArray.add("오리")
        dataArray.add("호랑이")
        dataArray.add("여우")
        dataArray.add("늑대")
        dataArray.add("오소리")
        dataArray.add("원숭이")
        dataArray.add("물개")
        dataArray.add("펭귄")
        dataArray.add("하마")
        dataArray.add("미어캣")
        dataArray.add("타조")
    }
}