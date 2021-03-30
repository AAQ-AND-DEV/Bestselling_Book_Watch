package com.aaqanddev.bestsellingbookwatch.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aaqanddev.bestsellingbookwatch.model.Bestseller

@Dao
interface BestsellersDao {

    //TODO must getBestsellers by category
    //need storage strategy (list of categories may not work, unless
    // can figure out how to store the category rankings)
    //TODO or, just report the list unsorted
    @Query("SELECT * FROM Bestsellers")
    fun getBestsellers(): LiveData<List<Bestseller>>

    @Query("SELECT * FROM Bestsellers WHERE categories LIKE '%' || :cat  || '%'")
    suspend fun getBestsellersByCat(cat: String): List<Bestseller>

    @Query("SELECT * FROM Bestsellers WHERE isbn10 = :isbn LIMIT 1")
    fun getBestsellerByISBN(isbn: String): Bestseller

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(bestsellers: List<Bestseller>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(bestseller: Bestseller)

}