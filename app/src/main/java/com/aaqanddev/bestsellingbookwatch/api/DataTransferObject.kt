package com.aaqanddev.bestsellingbookwatch.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkDataTransferWrapper (
    @Json(name = "status")
    var status: String? = null,

    @Json(name = "num_results")
    var numResults: Int? = null,

    @Json(name = "last_modified")
    var lastModified: String? = null,

    @Json(name = "results")
    var results: NetworkResults? = null
)

@JsonClass(generateAdapter = true)
class NetworkResults (
    @Json(name = "list_name")
    var listName: String? = null,

    @Json(name = "list_name_encoded")
    var listNameEncoded: String? = null,

    @Json(name = "bestsellers_date")
    var bestsellersDate: String? = null,

    @Json(name = "published_date")
    var publishedDate: String? = null,

    @Json(name = "published_date_description")
    var publishedDateDescription: String? = null,

    @Json(name = "next_published_date")
    var nextPublishedDate: String? = null,

    @Json(name = "previous_published_date")
    var previousPublishedDate: String? = null,

    @Json(name = "display_name")
    var displayName: String? = null,

    @Json(name = "normal_list_ends_at")
    var normalListEndsAt: Int? = null,

    @Json(name = "updated")
    var updated: String? = null,

    @Json(name = "books")
    var books: List<NetworkBook>? = null,
)

@JsonClass(generateAdapter = true)
data class NetworkBook (
    @Json(name = "rank")
    var rank: Int? = null,

    @Json(name = "rank_last_week")
    var rankLastWeek: Int? = null,

    @Json(name = "weeks_on_list")
    var weeksOnList: Int? = null,

    @Json(name = "primary_isbn10")
    var primaryIsbn10: String? = null,

    @Json(name = "primary_isbn13")
    var primaryIsbn13: String? = null,

    @Json(name = "publisher")
    var publisher: String? = null,

    @Json(name = "description")
    var description: String? = null,

    @Json(name = "title")
    var title: String? = null,

    @Json(name = "author")
    var author: String? = null,

    @Json(name = "book_image")
    var bookImage: String? = null,

//    @Json(name = "book_image_width")
//    var bookImageWidth: Int? = null
//
//    @Json(name = "book_image_height")
//    var bookImageHeight: Int? = null

    @Json(name = "amazon_product_url")
    var amazonProductUrl: String? = null,

//    @Json(name = "age_group")
//    var ageGroup: String? = null

    @Json(name = "book_review_link")
    var bookReviewLink: String? = null,

//    @Json(name = "first_chapter_link")
//    var firstChapterLink: String? = null
//
//    @Json(name = "sunday_review_link")
//    var sundayReviewLink: String? = null
//
//    @Json(name = "article_chapter_link")
//    var articleChapterLink: String? = null

    @Json(name = "buy_links")
    var buyLinks: List<NetworkBuyLink>? = null,

    @Json(name = "book_uri")
    var bookUri: String? = null
)

@JsonClass(generateAdapter = true)
data class NetworkBuyLink(
    @Json(name = "name")
    var name: String? = null,

    @Json(name = "url")
    var url: String? = null
)