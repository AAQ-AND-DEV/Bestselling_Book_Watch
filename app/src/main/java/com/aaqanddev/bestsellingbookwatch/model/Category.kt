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
): Parcelable{
    override fun equals(other: Any?): Boolean {
        return (this.encodedName==(other as Category).encodedName
                && this.displayName==other.displayName
                && this.newestPublishedDate==other.newestPublishedDate
                && this.oldestPublishedDate==other.oldestPublishedDate)
    }

    override fun hashCode(): Int {
        var result = displayName.hashCode()
        result = 31 * result + encodedName.hashCode()
        result = 31 * result + oldestPublishedDate.hashCode()
        result = 31 * result + newestPublishedDate.hashCode()
        return result
    }
}