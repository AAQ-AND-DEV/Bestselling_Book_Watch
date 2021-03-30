package com.aaqanddev.bestsellingbookwatch.data.category

import androidx.lifecycle.LiveData
import androidx.room.*
import com.aaqanddev.bestsellingbookwatch.model.Bestseller
import com.aaqanddev.bestsellingbookwatch.model.Category

@Dao
interface CategoryDao {

    @Query("SELECT * FROM Categories")
    fun getCategories(): LiveData<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(categories: List<Category>)

    @Update
    fun updateCategory(category: Category)

    @Query("SELECT * FROM Categories WHERE encodedName = (:name)")
    fun getCategory(name: String): Category

    @Query("SELECT * FROM Categories WHERE isWatched = 1")
    fun getWatchedCategories(): LiveData<List<Category>>
}