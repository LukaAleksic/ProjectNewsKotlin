package com.example.readnews.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.readnews.R
import com.example.readnews.databinding.FragmentDetailsBinding
import com.example.readnews.domain.Article

class DetailsFragment : Fragment() {

    private val viewModel: DetailsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
        }
        ViewModelProvider(this)
            .get(DetailsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentDetailsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_details,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        val args = DetailsFragmentArgs.fromBundle(arguments!!)

        var article: Article?
        viewModel.journal.observe(viewLifecycleOwner, Observer { articles ->
            articles?.apply {
                article = this.find { it.url == args.articleUrl }
                if (article != null)
                    binding.news = article
            }
        })

        binding.root.findViewById<TextView>(R.id.url).setOnClickListener {
            searchSuccess()
        }

        return binding.root
    }

    private fun getSearchIntent(): Intent {
        val args = DetailsFragmentArgs.fromBundle(arguments!!)
        return Intent(Intent.ACTION_VIEW, Uri.parse(args.articleUrl))
    }

    private fun searchSuccess() {
        startActivity(getSearchIntent())
    }


}