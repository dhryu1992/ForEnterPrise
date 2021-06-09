package com.example.databinding_kotlin

import android.databinding.ObservableField
import android.view.View
import android.widget.Toast

class ViewModel {
    val text = ObservableField<String>("")

    fun showText(view: View) {
        Toast.makeText(view.context, "${text.get()}", Toast.LENGTH_SHORT).show()
    }
}