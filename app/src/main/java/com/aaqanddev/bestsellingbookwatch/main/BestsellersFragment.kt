package com.aaqanddev.bestsellingbookwatch.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aaqanddev.bestsellingbookwatch.R
import com.aaqanddev.bestsellingbookwatch.api.BestsellerService
import com.aaqanddev.bestsellingbookwatch.api.NYTService
import com.aaqanddev.bestsellingbookwatch.databinding.FragmentBestsellersBinding
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit

class BestsellersFragment : Fragment() {
    //TODO create ViewModel
    //TODO instantiate viewModel here

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentBestsellersBinding.inflate(inflater)

        runBlocking {
            val result = this@BestsellersFragment.context?.applicationContext?.resources?.
            getString(
                R.string.nyt_key
            )?.let {
                NYTService.nytService
                    .getBestsellers(
                        "current", "hardcover-fiction",
                        it
                    )

            }
            binding.response.text = result

        }


        return binding.root
        //return super.onCreateView(inflater, container, savedInstanceState)
    }
}