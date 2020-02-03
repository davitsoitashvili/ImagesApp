package com.example.designapp

import android.graphics.Bitmap

interface ItemClickListener {
    fun getItemPosition(position: Int)
    fun deleteItem(position: Int)

}