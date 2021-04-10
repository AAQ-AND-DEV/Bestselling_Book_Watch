package com.aaqanddev.bestsellingbookwatch.util

import android.content.Context
import android.widget.Toast
import com.aaqanddev.bestsellingbookwatch.model.Bestseller

fun Context.toast(msg: CharSequence){
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun List<Bestseller>.standardizeOrder(){
    for ((i, book) in this.withIndex()){
        book.rank = i + 1
    }
}