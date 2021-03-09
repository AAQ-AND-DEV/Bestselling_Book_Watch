package com.aaqanddev.bestsellingbookwatch.data

import com.aaqanddev.bestsellingbookwatch.api.NetworkBook
import com.aaqanddev.bestsellingbookwatch.model.Bestseller

interface BestsellerDataSource {

    suspend fun getBestsellers(): AppResult<List<Bestseller>>?
    //TODO may need to get bestseller by filter on author & title
    suspend fun getBestseller(id: String): AppResult<NetworkBook>
    suspend fun deleteAllBestsellers()
}