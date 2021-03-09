package com.aaqanddev.bestsellingbookwatch.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aaqanddev.bestsellingbookwatch.model.Bestseller

@Dao
interface BestsellersDao {

    @Query("SELECT * FROM Bestsellers")
    fun observeBestsellers(): LiveData<List<Bestseller>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(bestsellers: List<Bestseller>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(bestseller: Bestseller)

}