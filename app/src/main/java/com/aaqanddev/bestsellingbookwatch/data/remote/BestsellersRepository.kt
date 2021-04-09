package com.aaqanddev.bestsellingbookwatch.data.remote

import android.content.Context
import androidx.lifecycle.LiveData
import com.aaqanddev.bestsellingbookwatch.R
import com.aaqanddev.bestsellingbookwatch.api.BestsellerService
import com.aaqanddev.bestsellingbookwatch.api.NetworkBook
import com.aaqanddev.bestsellingbookwatch.api.asDomainModel
import com.aaqanddev.bestsellingbookwatch.data.AppResult
import com.aaqanddev.bestsellingbookwatch.data.BestsellerDataSource
import com.aaqanddev.bestsellingbookwatch.data.BestsellersDao
import com.aaqanddev.bestsellingbookwatch.data.category.CategoryDao
import com.aaqanddev.bestsellingbookwatch.model.Bestseller
import com.aaqanddev.bestsellingbookwatch.model.Category
import com.aaqanddev.bestsellingbookwatch.util.addDateUpdatedToSharedPrefs
import com.aaqanddev.bestsellingbookwatch.util.getWatchedCatsFromSharedPrefs
import com.aaqanddev.bestsellingbookwatch.util.isNetworkAvailable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import timber.log.Timber

class BestsellersRepository(
    private val api: BestsellerService,
    private val context: Context,
    private val dao: BestsellersDao,
    private val catDao: CategoryDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BestsellerDataSource {

    override val watchedCategories =
        catDao.getWatchedCategories()


    //api Terms say i can't maintain data longer than 24 hrs
    //so refreshing all watched bestseller categories
    override suspend fun refreshBestsellers(categories: List<Category>?) {

        withContext(dispatcher) {
            Timber.d("refresh videos called")
            //watched categories are passed in
            val cats = categories
            //until a remote data store is set up, cannot fetch all categories, b/c of api terms
            //getCategoriesAsync()
            Timber.d("cats in refreshBestsellers: ${cats}")
            //iterate over watchedCategories, fetch list, and add to Db
            if (cats != null) {
                //val locCats = cats.value
                //if (locCats != null) {
                for (cat in cats) {
                    //TODO currently always fetching result...ensure refreshBestsellers is only called when stale
                    val result = context.applicationContext?.resources?.getString(
                        R.string.nyt_key
                    )?.let { api.getBestsellers("current", cat.encodedName, it) }

                    if (result != null) {
                        val category = result.results?.displayName

                        Timber.d("category in refreshBestsllers is $category")
                        val books = result.results?.books
                        //TODO before next one actually here is where the categories are being added
                        //
                        val domainBooks =
                            category?.let { books?.asDomainModel(it) }?.toMutableList()

                        Timber.d("domainBooks in refreshBest: $domainBooks")
                        withContext(dispatcher) {
                            //TODO should probably retrieve each book, to add the category to the category Set, if applicable
                            //Timber.d("domainBooks before attempt to addAll: $domainBooks")
                            if (domainBooks != null) {
                                for ((i, book) in domainBooks.withIndex()) {
                                    if (book.isbn10 != null) {
                                        val currentBookFromDb = getBestsellerByISBN(book.isbn10!!)

                                        Timber.d("currentBookFromDb in refreshBest: $currentBookFromDb")
                                        if (currentBookFromDb != null) {
                                            val currCats = currentBookFromDb.categories
                                            if (currCats != null) {
                                                if (category.isNotEmpty()) {
                                                    Timber.d("cat:|${category.trim()}|")

                                                    if (!currCats.contains(category.trim())){

                                                val catAdded = currCats.add(category.trim())
                                                Timber.d("catAdded within iteration over domainBooks in refreshBestsellers: $catAdded")
                                                    }
                                                }
                                            } else {
                                                Timber.d("currentCats is null")
                                                if (category.isNotEmpty()) {

                                                    val newSetOfCats = mutableSetOf(category.trim())
                                                    currentBookFromDb.categories = newSetOfCats
                                                }
                                            }
                                            Timber.d("currentBookFromDb not null, added to domainBooks")
                                            domainBooks[i] = currentBookFromDb
                                        } else {
                                            //TODO currentBook is null, so adding new set with category
                                            Timber.d("currentBookFromDb null, do I need to add the mutableSet to the domainBook?")
                                            domainBooks[i].categories = mutableSetOf(category.trim())
                                        }
                                    } else {
                                        Timber.d("isbn10 is null")
                                    }
                                }
                                domainBooks.let {
                                    Timber.d("domain books not null, adding to db")
                                    dao.addAll(it)
                                    //domainBooksResult = AppResult.Success(it)
                                    //TODO this should only happen if the books have been added.
                                    addDateUpdatedToSharedPrefs(context, System.currentTimeMillis())
                                }
                            }
                            //TODO else domainBooks is null, repeat fetch via network?


                        }


                    }
                    //TODO else api result is null

                }

            }
        }
    }

    suspend fun processFetchedList(category: String, domainBooks: MutableList<Bestseller>): List<Bestseller> {

        for ((i, book) in domainBooks.withIndex()) {
            if (book.isbn10 != null) {
                val currentBookFromDb = getBestsellerByISBN(book.isbn10!!)

                Timber.d("currentBookFromDb in refreshBest: $currentBookFromDb")
                if (currentBookFromDb != null) {
                    val currCats = currentBookFromDb.categories
                    if (currCats != null) {
                        if (category.isNotEmpty()) {
                            Timber.d("cat:|${category.trim()}|")

                            if (!currCats.contains(category.trim())){

                                val catAdded = currCats.add(category.trim())
                                Timber.d("catAdded within iteration over domainBooks in refreshBestsellers: $catAdded")
                            }
                        }
                    } else {
                        Timber.d("currentCats is null")
                        if (category.isNotEmpty()) {

                            val newSetOfCats = mutableSetOf(category.trim())
                            currentBookFromDb.categories = newSetOfCats
                        }
                    }
                    Timber.d("currentBookFromDb not null, added to domainBooks")
                    domainBooks[i] = currentBookFromDb
                } else {
                    //TODO currentBook is null, so adding new set with category
                    Timber.d("currentBookFromDb null, do I need to add the mutableSet to the domainBook?")
                    domainBooks[i].categories = mutableSetOf(category.trim())
                }
            } else {
                Timber.d("isbn10 is null")
            }
        }
        return domainBooks
    }

    suspend fun getCategoriesAsync(): LiveData<List<Category>> {
        return withContext(dispatcher) {

            catDao.getCategories()

        }
    }

    suspend fun getBestsellerByISBN(isbn10: String): Bestseller? {
        return withContext(dispatcher) {
            async {
                dao.getBestsellerByISBN(isbn10)
            }.await()
        }
    }

    override suspend fun getBestsellers(encodedListName: String?): List<Bestseller>? {

        //var domainBooksResult: AppResult<List<Bestseller>>? = null
        val domainBooks: List<Bestseller>?

        //db the default retrieval strategy?
//
        return withContext(dispatcher) {

            //val data = dao.getBestsellers()
            Timber.d(encodedListName)
            //val category = encodedListName?.let { catDao.getCategory(it) }
            val displayName = encodedListName?.let { getDisplayNameFromCategory(it) }
            // category?.value?.displayName
            Timber.d(displayName)
            if (displayName != null) {
                //val modDisplayName = displayName.replace("&", "and")
                //Timber.d(modDisplayName)
                val bookList = getBestsellersFromCategory(displayName)
                bookList as List<Bestseller>
                if (bookList.isNotEmpty()){

                    return@withContext bookList
                }

                else{
                    Timber.d(" db empty, fetching list -- networkAvailability: ${isNetworkAvailable(context)}")
        if (isNetworkAvailable(context)) {

            try {

                Timber.d("starting bestsellers fetch")
                val result = context.applicationContext?.resources?.getString(
                    R.string.nyt_key
                )?.let {
                    api.getBestsellers(
                        "current", encodedListName!!,
                        it
                    )
                }
                //val text = StringBuilder()
                if (result != null) {
                    val category = result.results?.displayName

                    Timber.d("category in hot fetch is $category")
                    val books = result.results?.books

                    domainBooks = category?.let { books?.asDomainModel(it) }

                    if (domainBooks!=null){


                        return@withContext withContext(dispatcher) {
                            //TODO should probably retrieve each book, to add the category to the category Set, if applicable
                            //Timber.d("domainBooks before attempt to addAll: $domainBooks")
                            val processedBooks : List<Bestseller>? = if (category != null) {
                                processFetchedList(category, domainBooks.toMutableList())
                            } else{
                                null
                            }


                            processedBooks?.let {
                                dao.addAll(processedBooks)
                                return@let processedBooks
                            }

                        }
                    }
                } else {
                    Timber.e("result from api fetch is null")
                    //TODO trigger Error property Toast

                    return@withContext null
                }

            } catch (e: Exception) {
                //TODO trigger error propety toast
                Timber.e(e.localizedMessage)
                return@withContext null
            }
            }
        }
            } else {
                Timber.d("displayName is null")
                return@withContext null
            }

            return@withContext null

        }
    }

    private suspend fun getBestsellersFromCategory(encodedName: String): List<Bestseller> {
        return withContext(dispatcher) {
            async {
                dao.getBestsellersByCat(encodedName)
            }
        }.await()
    }

    override suspend fun getBestseller(id: String): AppResult<NetworkBook> {
        TODO("not implemented")
    }

    override suspend fun deleteAllBestsellers() {
        TODO("not implemented")
    }

//    override suspend fun getBestsellersByCat(cat: String): LiveData<List<Bestseller>> {
//        return dao.getBestsellersByCat(cat)
//    }

    override suspend fun getCategories(): AppResult<List<Category>>? {

        //TODO default to getting categories from db
        var categoriesResult: AppResult<List<Category>>? = null
        val currWatchedCats = getWatchedCatsFromSharedPrefs(context)
        val currWatchedCatsEncodedNames = currWatchedCats.map {
            it.encodedName
        }
        if (isNetworkAvailable(context)) {

            //TODO add cache and cache timeout (in sharedPrefs?)
            try {

                Timber.d("starting categories network fetch")
                val result = context.applicationContext?.resources?.getString(
                    R.string.nyt_key
                )?.let {
                    api.getCategories(it)
                }
                //Timber.d(result?.results.toString())

                val networkCategories = result?.results

                val categories = networkCategories?.asDomainModel()
                //access SharedPrefs list of watchedCats and modify categories
                //to update isWatched value before adding to Db
                if (categories != null) {
                    categories.forEach {
                        if (currWatchedCatsEncodedNames.contains(it?.encodedName)) {
                            it?.isWatched = true
                        }
                    }
                    Timber.d("after checking against SharedPrefs watchlist: $categories")

                    withContext(dispatcher) {
                        try {
                            catDao.addAll(categories as List<Category>)
                            categoriesResult = AppResult.Success(categories)

                        } catch (e: Exception) {
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
            //TODO need to put this first...
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

    private suspend fun getDisplayNameFromCategory(encodedName: String): String {
        return withContext(dispatcher) {
            async {
                catDao.getCategory(encodedName)
            }
        }.await().displayName
    }
}

