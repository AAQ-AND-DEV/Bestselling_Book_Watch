package com.aaqanddev.bestsellingbookwatch.data

import com.aaqanddev.bestsellingbookwatch.api.NetworkBook
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

//class BestsellersLocalRepository(private val bestsellersDao: BestsellersDao,
//private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO)
//    :BestsellerDataSource{
//
//    override suspend fun getBestsellers(): Result<List<NetworkBook>> {
//        return try {
//            Result.Success(bestsellersDao.getBestsellers())
//        } catch(ex: Exception){
//            Result.Error(ex.localizedMessage)
//        }
//    }
//
//    //TODO may need to get book by title and author
//    override suspend fun getBestseller(id: String): Result<NetworkBook> {
//        try{
//            val bestseller = bestsellersDao.getBestseller(id)
//            if (bestseller!= null){
//                return Result.Success(bestseller)
//            } else{
//                return Result.Error("bestseller not found!")
//            }
//        } catch (e: Exception){
//            return Result.Error(e.localizedMessage)
//        }
//    }
//
//    override suspend fun deleteAllBestsellers() {
//        withContext(ioDispatcher){
//            bestsellersDao.deleteAllBestsellers()
//        }
//    }
//}