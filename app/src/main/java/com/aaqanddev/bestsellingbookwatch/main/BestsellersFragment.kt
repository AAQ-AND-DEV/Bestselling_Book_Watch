package com.aaqanddev.bestsellingbookwatch.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aaqanddev.bestsellingbookwatch.databinding.FragmentBestsellersBinding

class BestsellersFragment : Fragment(){
    //TODO create ViewModel
    //TODO instantiate viewModel here

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentBestsellersBinding.inflate(inflater)

        return binding.root
        //return super.onCreateView(inflater, container, savedInstanceState)
    }
}