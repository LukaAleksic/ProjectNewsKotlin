package com.example.readnews.headlines

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readnews.R
import com.example.readnews.databinding.FragmentTopHeadlinesBinding
import com.example.readnews.domain.Article
import kotlinx.android.synthetic.main.fragment_top_headlines.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TopHeadlinesFragment : Fragment() {


    private val viewModel: TopHeadlinesViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this)
            .get(TopHeadlinesViewModel::class.java)
    }

    /**
     * RecyclerView Adapter for converting a list of News to cards.
     */
    private var viewModelAdapter: TopHeadlinesAdapter? = null

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.journal.observe(viewLifecycleOwner, Observer<List<Article>> { articles ->
            articles?.apply {
                viewModelAdapter?.news = articles
            }
        })
        countrySpinner.setSelection(7)
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
        val binding: FragmentTopHeadlinesBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_top_headlines,
            container,
            false)
        // Set the lifecycleOwner so DataBinding can observe LiveData
        binding.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = viewModel

        viewModelAdapter =
            TopHeadlinesAdapter(NewsClick {
                //TODO(navigation)
            })

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }


        // Observer for the network error.
        viewModel.eventNetworkError.observe(this, Observer{ isNetworkError ->
            if (isNetworkError) onNetworkError()
        })

        binding.root.findViewById<Button>(R.id.filterButton).setOnClickListener {
            val viewModelJob = SupervisorJob()
            val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

            viewModelScope.launch(Dispatchers.Main){
                val business :String
                val country : String
                binding.apply {
                    business = businessSpinner.selectedItem.toString()
                    country = countrySpinner.selectedItem.toString()
                }
                if(country == ""){
                    Toast.makeText(activity, getString(R.string.error_empty_country), Toast.LENGTH_LONG)
                        .show()
                }
                else {
                    viewModel.filter(business, country)
                }
            }
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
}

