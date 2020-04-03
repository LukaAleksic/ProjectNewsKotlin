package com.example.readnews.everything

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readnews.R
import com.example.readnews.databinding.FragmentEverythingBinding
import com.google.android.material.snackbar.Snackbar

class EverythingFragment : Fragment() {


    private val viewModel: EverythingViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this)
            .get(EverythingViewModel::class.java)
    }

    /**
     * RecyclerView Adapter for converting a list of News to cards.
     */
    private var viewModelAdapter: EverythingAdapter? = null

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.journal.observe(viewLifecycleOwner, Observer { articles ->
            articles?.let {
                viewModelAdapter?.news = articles
            }
        })
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentEverythingBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_everything,
            container,
            false)
        // Set the lifecycleOwner so DataBinding can observe LiveData
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        viewModelAdapter =
            EverythingAdapter()

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }


        // Observer for the network error.
        viewModel.eventNetworkError.observe(viewLifecycleOwner, Observer{ isNetworkError ->
            if (isNetworkError) onNetworkError()
        })

        return binding.root
    }


    /**
     * Method for displaying a Toast error message for network errors.
     */
    private fun onNetworkError() {
        viewModel.isNetworkErrorShown.value?.let { value ->
            if (!value) {
                Snackbar.make(view!!, getString(R.string.network_error), Snackbar.LENGTH_LONG)
                    .show()
                viewModel.onNetworkErrorShown()
            }
        }
    }
}

