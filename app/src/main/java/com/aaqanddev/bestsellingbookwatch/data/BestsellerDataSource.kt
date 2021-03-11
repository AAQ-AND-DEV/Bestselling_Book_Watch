package com.aaqanddev.bestsellingbookwatch.data

import com.aaqanddev.bestsellingbookwatch.api.NetworkBook
import com.aaqanddev.bestsellingbookwatch.model.Bestseller
import com.aaqanddev.bestsellingbookwatch.model.Category

interface BestsellerDataSource {

    suspend fun getBestsellers(encodedListName: String?): AppResult<List<Bestseller>>?
    //TODO may need to get bestseller by filter on author & title
    suspend fun getBestseller(id: String): AppResult<NetworkBook>
    suspend fun deleteAllBestsellers()

    suspend fun getCategories(): AppResult<List<Category>>?
    suspend fun updateCategory(cat: Category)
}