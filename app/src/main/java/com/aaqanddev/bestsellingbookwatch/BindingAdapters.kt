package com.aaqanddev.bestsellingbookwatch

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.BindingAdapter


@BindingAdapter("reviewLink")
fun bindReviewLink(tv: TextView, link: String?) {

    if (link?.isNotEmpty() == true) {
        tv.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            try{
            startActivity(tv.context, intent, null)
            } catch (e: ActivityNotFoundException){
                Toast.makeText(tv.context, tv.context.getString(R.string.detail_no_browser_avail), Toast.LENGTH_LONG).show()
            }
        }
    } else{
        tv.text = tv.context.getString(R.string.detail_no_review)
    }
}