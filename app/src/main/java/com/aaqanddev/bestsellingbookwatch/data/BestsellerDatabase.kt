package com.aaqanddev.bestsellingbookwatch.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aaqanddev.bestsellingbookwatch.model.Bestseller

@Database(entities=[Bestseller::class], version = 2, exportSchema = false)
@TypeConverters(RoomConverter::class)
abstract class BestsellerDatabase : RoomDatabase() {

    abstract fun bestsellersDao(): BestsellersDao
}