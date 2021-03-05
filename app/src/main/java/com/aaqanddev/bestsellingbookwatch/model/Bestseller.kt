package com.aaqanddev.bestsellingbookwatch.model

//TODO mark as Room entity, or create EntityModel
//TODO autogenerate id -- either via room here, or do the conversion elsewhere b4 using in RecyclerView
data class Bestseller(
    val id: String, val rank: Int?, val wksPresent: Int?, val isbn10: String?,
    val isbn13: String?, val publisher: String?, val desc: String?,
    val title: String?, val author: String?, val bk_img: String?,
    val amznUrl: String?, val reviewUrl: String?, val categories: Set<String>
) {
}