package com.aaqanddev.bestsellingbookwatch.categoryChooser

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.aaqanddev.bestsellingbookwatch.model.Category

class CatLookup(private val rv: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = rv.findChildViewUnder(e.x, e.y)
        if (view!=null){
            return (rv.getChildViewHolder(view) as CatChooserViewHolder).getItemDetails()
        }
        return null
    }
}