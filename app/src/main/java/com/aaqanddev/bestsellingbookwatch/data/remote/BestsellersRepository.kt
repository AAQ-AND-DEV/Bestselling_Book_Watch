package com.aaqanddev.bestsellingbookwatch.data.remote

import android.content.Context
import com.aaqanddev.bestsellingbookwatch.R
import com.aaqanddev.bestsellingbookwatch.api.NYTService
import com.aaqanddev.bestsellingbookwatch.api.NetworkBook
import com.aaqanddev.bestsellingbookwatch.api.asDomainModel
import com.aaqanddev.bestsellingbookwatch.data.BestsellerDataSource
import com.aaqanddev.bestsellingbookwatch.data.BestsellersDao
import com.aaqanddev.bestsellingbookwatch.data.AppResult
import com.aaqanddev.bestsellingbookwatch.model.Bestseller
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class BestsellersRepository(private val api: NYTService,
                                  private val context: Context,
                            private val dao: BestsellersDao,
                                    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
                                  ) : BestsellerDataSource {

    override suspend fun getBestsellers(): AppResult<List<Bestseller>>? {
        var domainBooksResult: AppResult<List<Bestseller>>? = null
        var domainBooks: List<Bestseller>?
        withContext(dispatcher) {


            Timber.d("starting bestsellers fetch")
            val result = context.applicationContext?.resources?.getString(
                R.string.nyt_key
            )?.let {
                api.nytService
                    .getBestsellers(
                        "current", "hardcover-fiction",
                        it
                    )

            }
            //val text = StringBuilder()
            if (result != null) {
                val category = result.results?.listName
                Timber.d("category is $category")
                val books = result.results?.books
                //TODO before next one actually here is where the categories are being added
                //
                domainBooks = category?.let { books?.asDomainModel(it) }
//                if (books != null) {
//                    for (book in books) {
//                        text.append("${book.title} \n")
//                    }
//
//                }
                //TODO should probably retrieve each book, to add the category to the category Set, if applicable
                domainBooks?.let {
                    dao.addAll(it)
                    domainBooksResult = AppResult.Success(it)
                }


            } else {
                Timber.e("result is null")
                domainBooksResult = AppResult.Error("result is null")
            }
        }
        return domainBooksResult
    }

    override suspend fun getBestseller(id: String): AppResult<NetworkBook> {
        TODO("not implemented")
    }

    override suspend fun deleteAllBestsellers() {
        TODO("not implemented")
    }
}