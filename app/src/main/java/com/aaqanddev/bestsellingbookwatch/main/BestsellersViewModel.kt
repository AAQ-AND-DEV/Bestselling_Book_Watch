package com.aaqanddev.bestsellingbookwatch.main

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.*
import com.aaqanddev.bestsellingbookwatch.data.AppResult
import com.aaqanddev.bestsellingbookwatch.data.BestsellerDataSource
import com.aaqanddev.bestsellingbookwatch.model.Bestseller
import com.aaqanddev.bestsellingbookwatch.model.Category
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class BestsellersViewModel(
    app: Application,
    private val repository: BestsellerDataSource
) : AndroidViewModel(app) {

    private val _bestsellersToDisplay = MutableLiveData<List<Bestseller>>()
    val bestsellersToDisplay: LiveData<List<Bestseller>>
        get() = _bestsellersToDisplay

    private val _activeList = MutableLiveData<String>()
    val activeList : LiveData<String>
        get() = _activeList

    private val _observedCategories = MutableLiveData<List<Category>>()
    val observedCategories : LiveData<List<Category>>
        get() = _observedCategories

    private val _allCategories = MutableLiveData<List<Category>>()
    val allCategories : LiveData<List<Category>>
        get() = _allCategories

    init{
        fetchActiveList()
    }

    fun fetchActiveList(){
        viewModelScope.launch {
            val result = repository.getBestsellers()
            //TODO incorporate showLoading pattern
            if (result != null){

            when (result){
                is AppResult.Success<*> ->
                    _bestsellersToDisplay.value = result.data as List<Bestseller>
                is AppResult.Error ->{
                    //TODO implement showSnackbar
                    _bestsellersToDisplay.value = emptyList()
                }
            }

        }
            }
    }
}

@Suppress("UNCHECKED_CAST")
class BestsellerViewModelFactory(
    @NonNull private val app: Application, private val bestsellerRepo: BestsellerDataSource
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BestsellersViewModel(app, bestsellerRepo) as T
    }
}