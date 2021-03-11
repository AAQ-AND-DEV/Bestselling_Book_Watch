package com.aaqanddev.bestsellingbookwatch.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
@Entity(tableName = "Categories")
data class Category(
    val displayName: String,
    @PrimaryKey(autoGenerate = false)
    val encodedName: String,
    val oldestPublishedDate: String,
    val newestPublishedDate: String,
    var isWatched: Boolean = false
): Parcelable