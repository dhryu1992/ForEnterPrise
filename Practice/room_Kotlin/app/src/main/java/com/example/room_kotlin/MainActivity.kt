package com.example.room_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.getAll().observe(this, Observer {
            todos -> result_Text.text = todos.toString()
        })
        viewModel.getAll().observe(this, Observer {  todos ->
            result_Text.text = todos.toString()

        })

        addButton.setOnClickListener {
            viewModel.insert(Todo(todo_editText.text.toString()))
            //result_Text.text = db.todoDao().getAll().toString()
        }

    /*    deleteButton.setOnClickListener {
            db.todoDao().delete(todo = )
        }*/
    }
}