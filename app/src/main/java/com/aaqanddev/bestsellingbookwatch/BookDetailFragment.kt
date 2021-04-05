package com.aaqanddev.bestsellingbookwatch

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat.SHOW_DIVIDER_MIDDLE
import androidx.cardview.widget.CardView
import androidx.core.view.marginTop
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
        if (categories!=null){

        for (cat in categories){
        val textView = TextView(requireContext())
            textView.text = cat
            val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            textView.layoutParams = layoutParams
            val textViewMarginParams = textView.layoutParams as ViewGroup.MarginLayoutParams
            textViewMarginParams.setMargins(
                resources.getDimension(R.dimen.margin_tiny).toInt(),
                resources.getDimension(R.dimen.margin_tiny).toInt(),
                resources.getDimension(R.dimen.margin_tiny).toInt(),
                0,
            )
            textView.setTextColor(resources.getColor(R.color.color_accent_dark))
            categoriesLinLo.addView(textView)
            //TODO polish/post add onClick to send to listView of that category
        }

        }
        val buylinkLinLo = binding.buylinksLinlo
        buylinkLinLo.dividerDrawable = resources.getDrawable(R.drawable.vertical_divider)
        buylinkLinLo.showDividers = SHOW_DIVIDER_MIDDLE
        val netBuyLinks = book.buyLinks
        val buyLinks = netBuyLinks?.buyLinks
        //var idxBuyLinks = 0
        if (buyLinks != null) {
            for (link in buyLinks){
                //TODO make a layout for this that looks better
                val cardView = CardView(requireContext())
                val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                cardView.layoutParams = layoutParams
                val cardViewMarginParams = cardView.getLayoutParams() as ViewGroup.MarginLayoutParams
                cardViewMarginParams.setMargins(
                    resources.getDimension(R.dimen.margin_tiny).toInt(),
                    resources.getDimension(R.dimen.margin_medium).toInt(),
                    resources.getDimension(R.dimen.margin_tiny).toInt(),
                    resources.getDimension(R.dimen.margin_medium).toInt(),
                )
                cardView.radius = 40f
                cardView.requestLayout()
                //cardView.maxCardElevation = resources.getDimension(R.dimen.margin_big)
                cardView.minimumHeight = resources.getDimension(R.dimen.margin_big).toInt()
                cardView.setContentPadding(
                    resources.getDimension(R.dimen.margin_medium).toInt(),
                    resources.getDimension(R.dimen.margin_medium).toInt(),
                    resources.getDimension(R.dimen.margin_medium).toInt(),
                    resources.getDimension(R.dimen.margin_medium).toInt(),
                    )
                    val textView = TextView(requireContext())
                textView.text = resources.getString(R.string.buyLink,link.name)

                textView.isClickable = true
                textView.isFocusable = true
                textView.setOnClickListener {
                    view ->
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse(link.url))
                    try{

                    startActivity(intent)
                    } catch (e: ActivityNotFoundException){
                        Toast.makeText(textView.context, getString(R.string.detail_no_buylink_browser_avail), Toast.LENGTH_LONG).show()
                    }
                }
                cardView.addView(textView)
                buylinkLinLo.addView(cardView)
//                if (idxBuyLinks<buyLinks.size-1){
//
//                }
//                idxBuyLinks++
            }
        }

        return binding.root

    }
}