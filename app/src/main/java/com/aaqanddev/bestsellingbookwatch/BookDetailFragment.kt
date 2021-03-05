package com.aaqanddev.bestsellingbookwatch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class BookDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //TODO get id from BookDetailFragmentArgs (Q: will I have access to the id at this point?)
        val id = BookDetailFragmentArgs.fromBundle(requireArguments()).bookId
        //TODO pass id to network call? or viewModel?

        return super.onCreateView(inflater, container, savedInstanceState)

    }
}