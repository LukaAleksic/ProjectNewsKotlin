package com.example.readnews.headlines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readnews.R
import com.example.readnews.databinding.FragmentTopHeadlinesBinding
import com.example.readnews.util.FRANCE_POSITION
import kotlinx.android.synthetic.main.fragment_top_headlines.*


class TopHeadlinesFragment : Fragment() {


    private val viewModel: TopHeadlinesViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this)
            .get(TopHeadlinesViewModel::class.java)
    }

    /**
     * RecyclerView Adapter for converting a list of News to cards.
     */
    private var topHeadlinesAdapter: TopHeadlinesAdapter? = null

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.journal.observe(viewLifecycleOwner, Observer { articles ->
            articles?.apply {
                topHeadlinesAdapter?.news = articles
            }
        })
        countrySpinner.setSelection(FRANCE_POSITION)
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentTopHeadlinesBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_top_headlines,
            container,
            false
        )
        // Set the lifecycleOwner so DataBinding can observe LiveData
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        topHeadlinesAdapter =
            TopHeadlinesAdapter(NewsClick {
                //TODO(navigation)
            })

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = topHeadlinesAdapter
        }

        // Observer for the network error.
        viewModel.eventNetworkError.observe(viewLifecycleOwner, Observer { isNetworkError ->
            if (isNetworkError) onNetworkError()
        })

        // Observer for the generic error.
        viewModel.eventGenericError.observe(viewLifecycleOwner, Observer { isGenericError ->
            if (isGenericError) onGenericError()
        })

        binding.root.findViewById<Button>(R.id.filterButton).setOnClickListener {
            val business: String
            val country: String
            binding.apply {
                business = businessSpinner.selectedItem.toString()
                country = countrySpinner.selectedItem.toString()
            }
            viewModel.filter(business, country)
        }

        return binding.root
    }

    /**
     * Method for displaying a Toast error message for network errors.
     */
    private fun onNetworkError() {
        viewModel.isNetworkErrorShown.value?.let { value ->
            if (!value) {
                Toast.makeText(activity, getString(R.string.network_error), Toast.LENGTH_LONG)
                    .show()
                viewModel.onNetworkErrorShown()
            }
        }
    }

    private fun onGenericError() {
        viewModel.isGenericErrorShown.value?.let { value ->
            if (!value) {
                Toast.makeText(activity, getString(R.string.generic_error), Toast.LENGTH_LONG)
                    .show()
                viewModel.onGenericErrorShown()
            }
        }
    }
}

