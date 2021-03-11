package com.aaqanddev.bestsellingbookwatch.data.remote

import android.content.Context
import com.aaqanddev.bestsellingbookwatch.R
import com.aaqanddev.bestsellingbookwatch.api.NYTService
import com.aaqanddev.bestsellingbookwatch.api.NetworkBook
import com.aaqanddev.bestsellingbookwatch.api.asDomainModel
import com.aaqanddev.bestsellingbookwatch.data.BestsellerDataSource
import com.aaqanddev.bestsellingbookwatch.data.BestsellersDao
import com.aaqanddev.bestsellingbookwatch.data.AppResult
import com.aaqanddev.bestsellingbookwatch.data.category.CategoryDao
import com.aaqanddev.bestsellingbookwatch.model.Bestseller
import com.aaqanddev.bestsellingbookwatch.model.Category
import com.aaqanddev.bestsellingbookwatch.util.isNetworkAvailable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception

class BestsellersRepository(
    private val api: NYTService,
    private val context: Context,
    private val dao: BestsellersDao,
    private val catDao: CategoryDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BestsellerDataSource {

    override suspend fun getBestsellers(encodedListName: String?): AppResult<List<Bestseller>>? {
        var domainBooksResult: AppResult<List<Bestseller>>? = null
        val domainBooks: List<Bestseller>?


        Timber.d("networkAvailability: ${isNetworkAvailable(context)}")
        if (isNetworkAvailable(context)) {

            try {

                Timber.d("starting bestsellers fetch")
                val result = context.applicationContext?.resources?.getString(
                    R.string.nyt_key
                )?.let {
                    api.nytService
                        .getBestsellers(
                            "current", encodedListName!!,
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

                    withContext(dispatcher) {
                        //TODO should probably retrieve each book, to add the category to the category Set, if applicable
                        //Timber.d("domainBooks before attempt to addAll: $domainBooks")
                        domainBooks?.let {
                            dao.addAll(it)
                            domainBooksResult = AppResult.Success(it)
                        }

                    }
                } else {
                    Timber.e("result is null")
                    domainBooksResult = AppResult.Error("result is null")
                }

            } catch (e: Exception) {
                domainBooksResult = AppResult.Error(e.localizedMessage)
            }
        } else {
            withContext(dispatcher) {
                val data = dao.getBestsellers()


                if (data.isNotEmpty()) {
                    Timber.d("data from db")
                    domainBooksResult = AppResult.Success(data)

                } else {
                    Timber.e("no data found in db")
                }
            }
        }

        //Timber.d("before return: current value of ${(domainBooksResult as AppResult.Success).data}")
        return domainBooksResult
    }

    override suspend fun getBestseller(id: String): AppResult<NetworkBook> {
        TODO("not implemented")
    }

    override suspend fun deleteAllBestsellers() {
        TODO("not implemented")
    }

    override suspend fun getCategories(): AppResult<List<Category>>? {

        var categoriesResult: AppResult<List<Category>>? = null
        if (isNetworkAvailable(context)) {

            //TODO add cache and cache timeout (in sharedPrefs?)
            try {

                Timber.d("starting categories network fetch")
                val result = context.applicationContext?.resources?.getString(
                    R.string.nyt_key
                )?.let {
                    NYTService.nytService
                        .getCategories(it)
                }
                //Timber.d(result?.results.toString())

                val networkCategories = result?.results

                val categories = networkCategories?.asDomainModel()
                //TODO can I access SharedPrefs here to attain list of watchedCats
                //update this list accordingly?
                if (categories != null) {

                    withContext(dispatcher) {
                        try {
                            //TODO here all categories will be added anew,
                            // but I want it to persist the isWatched var

                            catDao.addAll(categories as List<Category>)
                            categoriesResult = AppResult.Success(categories)

                        } catch (e: TypeCastException) {
                            Timber.e("probably cast exception: ${e.localizedMessage}")
                        }
                    }


                } else {
                    Timber.e("result is null")
                    categoriesResult = AppResult.Error("result is null")
                }

            } catch (e: Exception) {
                categoriesResult = AppResult.Error(e.localizedMessage)
            }
        } else {
            withContext(dispatcher) {
                val categories = catDao.getCategories()


                val localCatVal = categories.value
                if (localCatVal != null) {
                    if (localCatVal.isNotEmpty()) {
                        Timber.d("data from db")
                        categoriesResult = AppResult.Success(localCatVal)

                    }
                } else {
                    Timber.e("no data found in db")
                }
            }
        }
        return categoriesResult
    }

    override suspend fun updateCategory(cat: Category) {
        withContext(dispatcher) {
            catDao.updateCategory(cat)
        }
    }
}

