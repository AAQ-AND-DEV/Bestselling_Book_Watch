package com.aaqanddev.bestsellingbookwatch.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aaqanddev.bestsellingbookwatch.R
import com.aaqanddev.bestsellingbookwatch.databinding.FragmentAboutBinding
import com.aaqanddev.bestsellingbookwatch.databinding.FragmentBestsellersBinding

class AboutFragment : Fragment(R.layout.fragment_about) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAboutBinding.inflate(inflater)
        binding.aboutMytLogo.setOnClickListener {
           val intent = Intent()
            intent.apply{
                action = Intent.ACTION_VIEW
                data = Uri.parse("https://developer.nytimes.com")
            }
            startActivity(intent)

        }
        return binding.root
    }

}