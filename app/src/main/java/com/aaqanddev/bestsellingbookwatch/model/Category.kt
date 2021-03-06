package com.aaqanddev.bestsellingbookwatch.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Category(
    val displayName: String,
    val encodedName: String,
    val oldestPublishedDate: String
): Parcelable