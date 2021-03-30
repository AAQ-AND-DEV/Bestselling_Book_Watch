package com.aaqanddev.bestsellingbookwatch.categoryChooser

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aaqanddev.bestsellingbookwatch.databinding.CategoryItemVhBinding
import com.aaqanddev.bestsellingbookwatch.model.Category
import timber.log.Timber

//TODO pass viewModel here, instead of catList?
class CatChooserListAdapter(
    private val context: Context
): ListAdapter<Category, CatChooserListAdapter.CatChooserViewHolder>(CategoryDiffCallback()){

    init{
        //Timber.d("init block of Adapter called")
        setHasStableIds(true)
    }

    private var tracker: SelectionTracker<Category>? = null

    fun setTracker(tracker: SelectionTracker<Category>?) {
        this.tracker = tracker
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatChooserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CategoryItemVhBinding.inflate(inflater, parent, false)
        return CatChooserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CatChooserViewHolder, position: Int) {
        //Timber.d("current list size: ${currentList.size}")
        holder.bind(getItem(position))
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.encodedName == newItem.encodedName
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }

    inner class CatChooserViewHolder(private val binding: CategoryItemVhBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cat: Category){
            binding.category = cat
            binding.executePendingBindings()
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Category> =
            object: ItemDetailsLookup.ItemDetails<Category>(){
                override fun getPosition(): Int {
                    return adapterPosition
                }

                override fun getSelectionKey(): Category {
                    return getItem(adapterPosition)
                }
            }

    }
}

