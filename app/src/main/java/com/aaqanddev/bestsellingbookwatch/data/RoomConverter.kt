package com.aaqanddev.bestsellingbookwatch.data

import androidx.room.TypeConverter
import com.aaqanddev.bestsellingbookwatch.api.NetworkBuyLink
import com.aaqanddev.bestsellingbookwatch.api.NetworkBuyLinks
import com.aaqanddev.bestsellingbookwatch.model.Category
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import timber.log.Timber

class RoomConverter {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val nblsAdapter: JsonAdapter<NetworkBuyLinks> = moshi.adapter(NetworkBuyLinks::class.java)
    private val nblAdapter: JsonAdapter<NetworkBuyLink> = moshi.adapter(NetworkBuyLink::class.java)

    @TypeConverter
    fun fromSet(set: Set<String>?): String? {
        val text = StringBuilder()
        if (set != null) {

            for (el in set) {
                text.append("$el \t")
            }
        }
        return if (text.isEmpty()) null else text.toString()
    }

    @TypeConverter
    fun toSet(setText: String): Set<String>? {
        val set = mutableSetOf<String>()
        val list = setText.split("\t")
        for (el in list) {
            set.add(el)
        }
        return if (set.isEmpty()) null else set
    }

    //TODO write Type converter for NetworkBuyLinks
    @TypeConverter
    fun fromNBLS(nbls: NetworkBuyLinks?): String? {
//        val sb = StringBuilder()
//        val linksList = nbl?.buyLinks
//        if (!linksList.isNullOrEmpty()){
//        sb.append("{")
//        for (el in linksList){
//
//        }
//        }
//        sb.append("}")
//        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
//        val adapter: JsonAdapter<NetworkBuyLinks> = moshi.adapter(NetworkBuyLinks::class.java)
        //val catJsonStringBuilder = StringBuilder()
        val buylinks = nbls
        var json: String? = null
        if (buylinks != null) {
            json = nblsAdapter.toJson(buylinks)
        }
        return json
    }

    @TypeConverter
    fun toNBLS(json: String?): NetworkBuyLinks?{
        var buyLinks: NetworkBuyLinks? = null
        if (json!=null){

        buyLinks = nblsAdapter.fromJson(json)
        }
        return buyLinks
    }

    @TypeConverter
    fun fromNBL(links:List<NetworkBuyLink>?): String{
        var json : StringBuilder? =null
        if (!links.isNullOrEmpty()){
            for (link in links){

                json = java.lang.StringBuilder()
                json.append("${nblAdapter.toJson(link)} \t")

            }

        }
        Timber.d("json string in fromNBL: ${json.toString()}")
        return json.toString()
    }

    @TypeConverter
    fun toNBL(json: String?): List<NetworkBuyLink>?{
        var list: List<NetworkBuyLink>? = null
        val links = json?.split("\t")
        if (!links.isNullOrEmpty()){
            list = mutableListOf()
            for (link in links){
                Timber.d("toNBL val of parsed links: $link")
                if (link.isNotBlank()){

                nblAdapter.fromJson(link)?.let { list.add(it) }
                }
            }
        }
        return list
    }
}
