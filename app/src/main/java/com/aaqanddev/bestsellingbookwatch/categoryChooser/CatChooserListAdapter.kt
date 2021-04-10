package com.aaqanddev.bestsellingbookwatch.categoryChooser

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aaqanddev.bestsellingbookwatch.databinding.CategoryItemVhBinding
import com.aaqanddev.bestsellingbookwatch.model.Category

class CatChooserListAdapter(
    private val context: Context, val onClickListener: CategoryChooserOnClickListener
): ListAdapter<Category, CatChooserListAdapter.CatChooserViewHolder>(CategoryDiffCallback()){

    init{
        //Timber.d("init block of Adapter called")
        setHasStableIds(true)
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
        val cat = getItem(position)
        holder.bind(cat)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(cat)
        }
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

    }

    class CategoryChooserOnClickListener(val listener: (category: Category)-> Unit){
        fun onClick(cat: Category) = listener(cat)
    }
}



