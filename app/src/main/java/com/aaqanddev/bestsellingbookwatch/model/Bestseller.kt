package com.aaqanddev.bestsellingbookwatch.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.aaqanddev.bestsellingbookwatch.api.NetworkBuyLink
import com.aaqanddev.bestsellingbookwatch.api.NetworkBuyLinks
import kotlinx.parcelize.Parcelize

//TODO mark as Room entity, or create EntityModel
//TODO autogenerate id -- either via room here, or do the conversion elsewhere b4 using in RecyclerView
//currently ID generated when NetworkBook fun asDomainModel() is called
@Entity(tableName = "Bestsellers")
@Parcelize
data class Bestseller(
    @PrimaryKey(autoGenerate = false)var id: String ="", var rank: Int? = 0, var wksPresent: Int? = 0, var isbn10: String? = "",
    var isbn13: String? ="", var publisher: String? = "", var desc: String? = "",
    var title: String? ="", var author: String? ="", var bk_img: String? ="",
    var amznUrl: String? ="", var reviewUrl: String? ="", @Embedded var buyLinks: NetworkBuyLinks? = NetworkBuyLinks(listOf(NetworkBuyLink("",""))), var categories: Set<String>? = emptySet()
) : Parcelable{
//    @Ignore
//    constructor() : this("", 0,0, "", "", "", "", "", "", "", "", "", listOf(NetworkBuyLink("","")), emptySet<String>())

//    @Ignore
//    constructor()
}