package com.aaqanddev.bestsellingbookwatch.main

import android.app.Application
import androidx.lifecycle.*
import androidx.recyclerview.selection.Selection
import com.aaqanddev.bestsellingbookwatch.data.AppResult
import com.aaqanddev.bestsellingbookwatch.data.BestsellerDataSource
import com.aaqanddev.bestsellingbookwatch.model.Bestseller
import com.aaqanddev.bestsellingbookwatch.model.Category
import com.aaqanddev.bestsellingbookwatch.util.getUpdatedDateFromSharedPrefs
import com.aaqanddev.bestsellingbookwatch.util.standardizeOrder
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import timber.log.Timber

class BestsellersViewModel(
    private val app: Application,
    private val repository: BestsellerDataSource
) : AndroidViewModel(app) {

    private var _bestsellersToDisplay = MutableLiveData<List<Bestseller>>()
    val bestsellersToDisplay: LiveData<List<Bestseller>>
        get() = _bestsellersToDisplay

    private val _activeList = MutableLiveData<String>()
    val activeList: LiveData<String>
        get() = _activeList

    private val _observedCategories = MutableLiveData<List<Category>>()
    val observedCategories: LiveData<List<Category>>
        get() = _observedCategories

    private val _allCategories = MutableLiveData<List<Category>>()
    val allCategories: LiveData<List<Category>>
        get() = _allCategories

    private val _showToast = MutableLiveData<String?>()
    val showToast: LiveData<String?>
        get() = _showToast

    private val _showBestsellerToast = MutableLiveData<String?>()
    val showBestsellerToast : LiveData<String?>
        get() = _showBestsellerToast

    private val _showCatsLoading = MutableLiveData<Boolean>()
    val showCatsLoading: LiveData<Boolean>
        get() = _showCatsLoading

    private val _showBestsellersLoading = MutableLiveData<Boolean>()
    val showBestsellersLoading: LiveData<Boolean>
        get() = _showBestsellersLoading

    private val watchedCategories = Transformations.map(repository.watchedCategories) {
        Timber.d("watchedCats from repository.watchedCategories: $it")
        it
    }

    private val _watchedChooserCategories = MutableLiveData<MutableList<Category>>()
    val watchedChooserCategories: LiveData<MutableList<Category>>
        get() = _watchedChooserCategories

    init {
        fetchCategoriesList()
        refreshBestsellers()
        fetchActiveList()
        Timber.d("watchedCats in init of vm: ${watchedCategories.value}")
        watchedCategories.observeForever() {
            Timber.d("watchedCategories value change. logged here")
        }
        _watchedChooserCategories.value = mutableListOf()
    }

    fun addCatToChooserCats(cat: Category) {
        val watchedCatsList = _watchedChooserCategories.value
        Timber.d("cats in viewModel: $watchedCatsList")
        if (!watchedCatsList?.contains(cat)!!) {
            watchedCatsList.add(cat)

            _watchedChooserCategories.value = watchedCatsList!!
            Timber.d("cats in viewModel after add: ${_watchedChooserCategories.value}")
        }
    }

    fun removeCatFromChooserCats(cat: Category) {
        val watchedCatsList = _watchedChooserCategories.value
        Timber.d("cats in viewModel: $watchedCatsList")
        val itemRemoved = watchedCatsList?.remove(cat)
        Timber.d("cat removed: $itemRemoved")
        _watchedChooserCategories.value = watchedCatsList!!
        Timber.d("cats in viewModel after remove: ${_watchedChooserCategories.value}")
    }

    fun clearCatsFromChooserCats(cats: MutableList<Category>) {
        val catsIterator = cats.iterator()
        while (catsIterator.hasNext()) {
            val cat = catsIterator.next()
            cat.isWatched = !cat.isWatched
            runBlocking {
                updateCategory(cat)
            }
        }
        _watchedChooserCategories.value = mutableListOf()
    }

    fun updateActiveList(encodedName: String) {
        _activeList.value = encodedName
        Timber.d("active list updated to $encodedName")
    }

    fun updateCategory(cat: Category) {
        viewModelScope.launch {
            repository.updateCategory(cat)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun fetchCategoriesList() {
        viewModelScope.launch {
            _showCatsLoading.value = true
            val res = repository.getCategories()
            if (res != null) {
                when (res) {
                    is AppResult.Success<*> -> {
                        Timber.d("cats in repo.fetchCategories: ${res.data}")
                        _showCatsLoading.value = false
                        _allCategories.value = res.data as List<Category>
                    }
                    is AppResult.Error -> {
                        val msg = res.message
                        if (msg != null) {
                            _showToast.value = msg
                        }
                        _allCategories.value = emptyList()
                    }
                }
            }
        }
    }

    fun refreshBestsellers() {

        Timber.d("watchedCats in refreshBestsellers of vm: ${watchedCategories.value}")

        val updatedDatetimeString = getUpdatedDateFromSharedPrefs(app)
        var updatedDatetimePlus20hrs: LocalDateTime? = null
        if (updatedDatetimeString != null) {
            if (updatedDatetimeString.isNotEmpty()) {

                updatedDatetimePlus20hrs =
                    updatedDatetimeString.toLong()
                        .let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.MIN).plusHours(20) }
            }
        }
        Timber.d("is updatedDatetime: ${updatedDatetimePlus20hrs?.toEpochSecond(ZoneOffset.MIN)} < current time: ${System.currentTimeMillis()}")

        if (updatedDatetimeString.isNullOrBlank() || updatedDatetimePlus20hrs?.toEpochSecond(
                ZoneOffset.MIN
            )!! < System.currentTimeMillis()
        ) {
            viewModelScope.launch {
                Timber.d("repo.refreshBestsellers called in vm")
                repository.refreshBestsellers(watchedCategories.value)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun fetchActiveList() {

        viewModelScope.launch {
            _showBestsellersLoading.value = true
            Timber.d("about to launch repo.getBestsellers in fetchActiveList")
            val result = repository.getBestsellers(activeList.value)
            //TODO incorporate showLoading pattern
            if (result != null) {
                when (result) {
                    is AppResult.Success<*> -> {
                        Timber.d("cats in repo.fetchCategories: ${result.data}")
                        _showBestsellersLoading.value = false
                        val newList = result.data as List<Bestseller>
                        newList.standardizeOrder()
                        _bestsellersToDisplay.value = newList
                    }
                    is AppResult.Error -> {
                        val msg = result.message
                        if (msg != null) {
                            _showBestsellerToast.value = msg
                        }
                        _bestsellersToDisplay.value = emptyList()
                    }
                }
            }
        }
    }
}