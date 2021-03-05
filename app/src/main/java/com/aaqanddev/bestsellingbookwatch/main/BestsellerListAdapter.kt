package com.aaqanddev.bestsellingbookwatch.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aaqanddev.bestsellingbookwatch.R
import com.aaqanddev.bestsellingbookwatch.databinding.BestsellerItemVhBinding
import com.aaqanddev.bestsellingbookwatch.model.Bestseller
import com.squareup.picasso.Picasso

class BestsellerListAdapter (val clickListener: BestsellerClickListener): ListAdapter<Bestseller, BestsellerListAdapter.BestsellerListViewHolder>(DiffCallback) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestsellerListViewHolder {
            return BestsellerListViewHolder.from(parent)
        }

        override fun onBindViewHolder(holder: BestsellerListViewHolder, position: Int) {
            holder.bind(clickListener, getItem(position))
        }

        companion object DiffCallback : DiffUtil.ItemCallback<Bestseller>(){
            override fun areItemsTheSame(oldItem: Bestseller, newItem: Bestseller): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Bestseller, newItem: Bestseller): Boolean {
                return oldItem == newItem
            }
        }

        class BestsellerListViewHolder(private val binding: BestsellerItemVhBinding) : RecyclerView.ViewHolder(binding.root){
            fun bind(listener: BestsellerClickListener, bestseller: Bestseller){
                binding.bestseller = bestseller
                binding.clickListener = listener
                Picasso.get().load(bestseller.bk_img)
                    .placeholder(R.drawable.ic_baseline_cloud_download_24)
                    .error(R.drawable.ic_baseline_error_24)
                    .into(binding.bookCoverImg)
                binding.executePendingBindings()
            }

            companion object {
                fun from(parent: ViewGroup): BestsellerListViewHolder{
                    val inflater = LayoutInflater.from(parent.context)
                    val binding = BestsellerItemVhBinding.inflate(inflater, parent, false)
                    return BestsellerListViewHolder(binding)
                }
            }
        }
    }

    class BestsellerClickListener(val clickListener: (Bestseller: Bestseller) -> Unit) {
        fun onClick(Bestseller: Bestseller) = clickListener(Bestseller)
    }

