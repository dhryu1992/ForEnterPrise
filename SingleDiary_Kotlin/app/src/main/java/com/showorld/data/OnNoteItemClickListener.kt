package com.showorld.data

import android.view.View

interface OnNoteItemClickListener {
    fun onItemClick(holder: NoteAdapter.ViewHolder?, view: View?, position: Int)

}
