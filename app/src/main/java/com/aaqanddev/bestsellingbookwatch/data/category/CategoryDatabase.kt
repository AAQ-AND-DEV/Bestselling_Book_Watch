package com.aaqanddev.bestsellingbookwatch.data.category

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aaqanddev.bestsellingbookwatch.model.Category

@Database(entities = [Category::class], version = 1, exportSchema = false)
abstract class CategoryDatabase
    : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

}