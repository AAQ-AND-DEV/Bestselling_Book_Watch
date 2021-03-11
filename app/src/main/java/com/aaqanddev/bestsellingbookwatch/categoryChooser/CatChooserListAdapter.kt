package com.aaqanddev.bestsellingbookwatch.categoryChooser

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.aaqanddev.bestsellingbookwatch.R
import com.aaqanddev.bestsellingbookwatch.databinding.BestsellerItemVhBinding
import com.aaqanddev.bestsellingbookwatch.databinding.CategoryItemVhBinding
import com.aaqanddev.bestsellingbookwatch.main.BestsellerListAdapter
import com.aaqanddev.bestsellingbookwatch.model.Category

class CatChooserListAdapter(private val categoriesList: List<Category>,
private val context: Context
): RecyclerView.Adapter<CatChooserViewHolder>(){
    init{
        setHasStableIds(true)
    }

    private var tracker: SelectionTracker<Long>? = null

    fun setTracker(tracker: SelectionTracker<Long>?) {
        this.tracker = tracker

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatChooserViewHolder {
        return CatChooserViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CatChooserViewHolder, position: Int) {
        holder.bind(categoriesList[position])
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }
}

class CatChooserViewHolder(private val binding: CategoryItemVhBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(cat: Category){
        binding.category = cat
        binding.executePendingBindings()
    }
    fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
        object: ItemDetailsLookup.ItemDetails<Long>(){
            override fun getPosition(): Int {
                return adapterPosition
            }

            override fun getSelectionKey(): Long {
                return itemId
            }
        }


    companion object {
        fun from(parent: ViewGroup): CatChooserViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = CategoryItemVhBinding.inflate(inflater, parent, false)
            return CatChooserViewHolder(binding)
        }
    }
}