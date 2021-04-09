package com.aaqanddev.bestsellingbookwatch.util

import com.aaqanddev.bestsellingbookwatch.model.Bestseller

fun List<Bestseller>.standardizeOrder(){
    for ((i, book) in this.withIndex()){
        book.rank = i + 1
    }
}