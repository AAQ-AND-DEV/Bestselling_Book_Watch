package com.aaqanddev.bestsellingbookwatch.model

import android.os.Parcelable
import com.aaqanddev.bestsellingbookwatch.api.NetworkBuyLink
import kotlinx.parcelize.Parcelize

//TODO mark as Room entity, or create EntityModel
//TODO autogenerate id -- either via room here, or do the conversion elsewhere b4 using in RecyclerView
//currently ID generated when NetworkBook fun asDomainModel() is called
@Parcelize
data class Bestseller(
    val id: String, val rank: Int?, val wksPresent: Int?, val isbn10: String?,
    val isbn13: String?, val publisher: String?, val desc: String?,
    val title: String?, val author: String?, val bk_img: String?,
    val amznUrl: String?, val reviewUrl: String?, val buyLinks: List<NetworkBuyLink>?, val categories: Set<String>
) : Parcelable {
}