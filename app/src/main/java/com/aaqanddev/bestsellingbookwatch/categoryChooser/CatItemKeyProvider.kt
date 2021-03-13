package com.aaqanddev.bestsellingbookwatch.categoryChooser

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView
import com.aaqanddev.bestsellingbookwatch.model.Category
import timber.log.Timber
import java.lang.IllegalStateException

class CatItemKeyProvider(private val adapter: CatChooserListAdapter?) : ItemKeyProvider<Category>(
    SCOPE_CACHED) {
    override fun getKey(position: Int): Category {
        //Timber.d("in key provider getKey list: ${adapter.currentList} ")
        Timber.d("in getKey, pos: $position")
        Timber.d("returning from getKey: ${adapter?.currentList?.get(position)}" )
        return adapter?.currentList?.get(position) ?: throw IllegalStateException("Recyclerview Adapter is not set!")
    }

    override fun getPosition(key: Category): Int {
        Timber.d("in key provider list getPosition : ${adapter?.currentList?.indexOfFirst{it == key}} and key: $key")
        return adapter?.currentList?.indexOfFirst { it == key }?: RecyclerView.NO_POSITION
    }
}