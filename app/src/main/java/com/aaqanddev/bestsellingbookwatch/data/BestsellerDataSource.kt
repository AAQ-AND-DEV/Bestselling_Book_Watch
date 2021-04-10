package com.aaqanddev.bestsellingbookwatch.data

import androidx.lifecycle.LiveData
import com.aaqanddev.bestsellingbookwatch.api.NetworkBook
import com.aaqanddev.bestsellingbookwatch.model.Bestseller
import com.aaqanddev.bestsellingbookwatch.model.Category

interface BestsellerDataSource {

    val watchedCategories: LiveData<List<Category>>
    suspend fun getBestsellers(encodedListName: String?): AppResult<List<Bestseller>>?
    //TODO may need to get bestseller by filter on author & title
    suspend fun getBestseller(id: String): AppResult<NetworkBook>
    suspend fun deleteAllBestsellers()
    //suspend fun getBestsellersByCat(cat: String): LiveData<List<Bestseller>>
    suspend fun refreshBestsellers(categories: List<Category>?)

    suspend fun getCategories(): AppResult<List<Category>>?
    suspend fun updateCategory(cat: Category)
    //suspend fun getCategory(name: String): AppResult<Category>?
    //suspend fun getWatchedCategories(): LiveData<List<Category>>?
}