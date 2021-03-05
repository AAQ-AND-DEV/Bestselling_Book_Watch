package com.aaqanddev.bestsellingbookwatch.api

import com.aaqanddev.bestsellingbookwatch.model.Bestseller
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//@JsonClass(generateAdapter = true)
//data class NetworkBestseller(
//    val rank: Int, @Json(name="weeks_on_list") val wksPresent: Int,
//    @Json(name="primary_isbn10") val isbn10: String,
//    @Json(name="primary_isbn13") val isbn13: String,
//    val publisher: String, @Json(name="description") val desc: String,
//    val title: String, val author: String, @Json(name="book_image") val bk_img: String,
//    @Json(name="amazon_product_url") val amznUrl: String,
//    @Json(name="book_review_link") val reviewUrl: String
//)
//
//fun NetworkBestseller.asDomainModel(): Bestseller {
//    return Bestseller(
//        this.rank, this.wksPresent, this.isbn10, this.isbn13, this.publisher,
//        this.desc, this.title, this.author, this.bk_img, this.amznUrl, this.reviewUrl
//    )
//}
//
//@JsonClass(generateAdapter = true)
//data class NetworkBestsellerContainer(val bestsellers: List<NetworkBestseller>)
//
//fun NetworkBestsellerContainer.asDomainModel(): List<Bestseller>{
//    return bestsellers.map{
//        it.asDomainModel()
//    }
//}

