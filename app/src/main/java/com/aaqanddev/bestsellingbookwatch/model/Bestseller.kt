package com.aaqanddev.bestsellingbookwatch.model

class Bestseller(val rank: Int, val wksPresent: Int, val isbn10: String,
                  val isbn13: String, val publisher: String,  val desc: String,
                 val title: String, val author: String,  val bk_img: String,
                 val amznUrl: String, val reviewUrl: String) {
}