package com.aaqanddev.bestsellingbookwatch.main

import android.app.Application
import androidx.lifecycle.*
import com.aaqanddev.bestsellingbookwatch.data.AppResult
import com.aaqanddev.bestsellingbookwatch.data.BestsellerDataSource
import com.aaqanddev.bestsellingbookwatch.model.Bestseller
import com.aaqanddev.bestsellingbookwatch.model.Category
import com.aaqanddev.bestsellingbookwatch.util.getUpdatedDateFromSharedPrefs
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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

    private val watchedCategories = Transformations.map(repository.watchedCategories){
        it
    }

    init {
        fetchCategoriesList()

        fetchActiveList()
        Timber.d("watchedCats in init of vm: ${watchedCategories.value}")
        watchedCategories.observeForever(){
            Timber.d("watchedCategories value change. logged here")
        }
//        observedCategories.observeForever(){
//            Timber.d("observedCategories value change. logged here")
//        }
    }

    //TODO possibly modify this method
    fun updateAllCategories(cats: List<Category>) {
        _allCategories.value = cats
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

    suspend fun fetchSingleCategory(name: String): Category {
        return viewModelScope.async {
            (repository.getCategory(name) as AppResult.Success).data
        }.await()
    }

    fun fetchCategoriesList() {
        viewModelScope.launch {
            val res = repository.getCategories()
            if (res != null) {
                when (res) {
                    is AppResult.Success<*> ->
                        _allCategories.value = res.data as List<Category>
                    is AppResult.Error -> {
                        _allCategories.value = emptyList()
                    }
                }
            }
        }
    }

    fun refreshBestsellers(){

        Timber.d("watchedCats in refreshBestsellers of vm: ${watchedCategories.value}")

        val updatedDatetimeString = getUpdatedDateFromSharedPrefs(app)
        var updatedDatetimePlus20hrs : LocalDateTime? = null
        if (updatedDatetimeString != ""){

            updatedDatetimePlus20hrs =
                updatedDatetimeString?.toLong()?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.MIN).plusHours(20) }
        }
        if (updatedDatetimeString.isNullOrBlank() || updatedDatetimePlus20hrs?.toEpochSecond(ZoneOffset.MIN)!! < System.currentTimeMillis()){
            viewModelScope.launch {
                Timber.d("repo.refreshBestsellers called in vm")
                repository.refreshBestsellers(watchedCategories.value)
            }
        }
    }

    fun fetchActiveList() {
        viewModelScope.launch {
            Timber.d("about to launch repo.getBestsellers in fetchActiveList")
            val result = repository.getBestsellers(activeList.value)
            //TODO incorporate showLoading pattern
            if (result != null) {
                _bestsellersToDisplay.value = result!!
//                when (result) {
//                    is AppResult.Success<*> -> {
//                        Timber.d("success in fetchActiveList")
//                        _bestsellersToDisplay.value = result.data as List<Bestseller>
//                    }
//                    is AppResult.Error -> {
//                        //TODO implement showSnackbar
//                        Timber.d("error in fetchActiveList ${result.message}")
//                        _bestsellersToDisplay.value = emptyList()
//                    }
//                }

            }
        }
    }
}