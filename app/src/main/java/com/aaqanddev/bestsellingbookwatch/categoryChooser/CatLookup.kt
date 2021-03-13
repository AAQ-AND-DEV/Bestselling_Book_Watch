package com.aaqanddev.bestsellingbookwatch.categoryChooser

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.aaqanddev.bestsellingbookwatch.model.Category
import timber.log.Timber

class CatLookup(private val rv: RecyclerView) : ItemDetailsLookup<Category>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<Category>? {
        val view = rv.findChildViewUnder(e.x, e.y)
        //this is getting called two or three times for a single longclick,
        // and returns LinearLayout, which can be cast to a ViewHolder object
        Timber.d("view inside itemDetails lookup: $view")
        if (view!=null){
            Timber.d("view in Lookup cast: ${rv.getChildViewHolder(view) as CatChooserListAdapter.CatChooserViewHolder}")
            return (rv.getChildViewHolder(view) as CatChooserListAdapter.CatChooserViewHolder).getItemDetails()
        }
        return null
    }
}