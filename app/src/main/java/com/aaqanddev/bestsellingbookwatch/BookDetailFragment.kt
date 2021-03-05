package com.aaqanddev.bestsellingbookwatch

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.aaqanddev.bestsellingbookwatch.databinding.FragmentBookDetailBinding
import com.squareup.picasso.Picasso

class BookDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        //TODO get id from BookDetailFragmentArgs (Q: will I have access to the id at this point?)
        val book = BookDetailFragmentArgs.fromBundle(requireArguments()).book
        //TODO pass id to network call? or viewModel?
        val binding = FragmentBookDetailBinding.inflate(inflater)
        binding.bestseller = book
        val imgView = binding.detailCoverImg
        Picasso.get().load(book.bk_img)
            .placeholder(R.drawable.ic_baseline_cloud_download_24)
            .error(R.drawable.ic_baseline_error_24)
            .into(imgView)

        val categoriesLinLo = binding.categoriesLinlo
        val categories = book.categories
        for (cat in categories){
        val textView = TextView(requireContext())
            textView.text = cat
            categoriesLinLo.addView(textView)
            //TODO polish/post add onClick to send to listView of that category
        }

        val buylinkLinLo = binding.buylinksLinlo
        val buyLinks = book.buyLinks
        if (buyLinks != null) {
            for (link in buyLinks){
                val textView = TextView(requireContext())
                textView.text = resources.getString(R.string.buyLink,link.name)
                textView.isClickable = true
                textView.isFocusable = true
                textView.setOnClickListener {
                    view ->
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse(link.url))
                    startActivity(intent)
                }
                buylinkLinLo.addView(textView)
            }
        }

        return binding.root

    }
}