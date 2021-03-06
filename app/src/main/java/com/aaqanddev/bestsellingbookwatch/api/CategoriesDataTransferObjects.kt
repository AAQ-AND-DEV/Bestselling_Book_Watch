package com.aaqanddev.bestsellingbookwatch.api

import com.aaqanddev.bestsellingbookwatch.model.Category
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CategoriesDataTransferWrapper(
    @Json(name = "status")
    var status: String? = null,

    @Json(name = "copyright")
    var copyright: String? = null,

    @Json(name = "num_results")
    var numResults: Int? = null,

    @Json(name = "results")
    var results: List<NetworkCategory>? = null
)


//TODO polish/post can traverse past lists with oldestPublishedDate, and nextPublishedDate of NetworkResults class of Bestsellers
@JsonClass(generateAdapter = true)
data class NetworkCategory(
    @Json(name = "list_name")
    var listName: String? = null,

    @Json(name = "display_name")
    var displayName: String? = null,

    @Json(name = "list_name_encoded")
    var listNameEncoded: String? = null,

    @Json(name = "oldest_published_date")
    var oldestPublishedDate: String? = null,

    @Json(name = "newest_published_date")
    var newestPublishedDate: String? = null,

    @Json(name = "updated")
    var updated: String? = null
)


fun NetworkCategory.asDomainModel(): Category? {
    var category :Category? = null
    if (this.displayName != null && this.listNameEncoded != null && this.oldestPublishedDate != null) {

        category = Category(
            this.displayName!!, this.listNameEncoded!!, this.oldestPublishedDate!!
        )
    }
    return category
}

fun List<NetworkCategory>.asDomainModel(): List<Category?>{
    return this.map{
        it ->
        it.asDomainModel()
    }
}