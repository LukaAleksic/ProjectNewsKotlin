package com.example.readnews.readnews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readnews.R
import com.example.readnews.databinding.FragmentReadNewsBinding
import com.example.readnews.databinding.ReadnewsItemBinding
import com.example.readnews.domain.Article
import com.example.readnews.viewmodels.ReadNewsViewModel

class ReadNewsFragment : Fragment() {


    private val viewModel: ReadNewsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, ReadNewsViewModel.Factory(activity.application))
            .get(ReadNewsViewModel::class.java)
    }

    /**
     * RecyclerView Adapter for converting a list of News to cards.
     */
    private var viewModelAdapter: ReadNewsAdapter? = null

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
        val binding: FragmentReadNewsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_read_news,
            container,
            false)
        // Set the lifecycleOwner so DataBinding can observe LiveData
        binding.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = viewModel

        viewModelAdapter = ReadNewsAdapter(NewsClick {
        //TODO(navigation)
        })

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }


        // Observer for the network error.
        viewModel.eventNetworkError.observe(this, Observer<Boolean> { isNetworkError ->
            if (isNetworkError) onNetworkError()
        })

        return binding.root
    }

    /**
     * Method for displaying a Toast error message for network errors.
     */
    private fun onNetworkError() {
        if(!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }
}

/**
 * Click listener for Articles. By giving the block a name it helps a reader understand what it does.
 *
 */
class NewsClick(val block: (Article) -> Unit) {
    /**
     * Called when an article is clicked
     *
     * @param news the news that was clicked
     */
    fun onClick(news: Article) = block(news)
}

/**
 * RecyclerView Adapter for setting up data binding on the items in the list.
 */
class ReadNewsAdapter(val callback: NewsClick) : RecyclerView.Adapter<ReadNewsViewHolder>() {

    /**
     * The news that our Adapter will show
     */
    var news: List<Article> = emptyList()
        set(value) {
            field = value
            // Notify any registered observers that the data set has changed. This will cause every
            // element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadNewsViewHolder {
        val withDataBinding: ReadnewsItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            ReadNewsViewHolder.LAYOUT,
            parent,
            false)
        return ReadNewsViewHolder(withDataBinding)
    }

    override fun getItemCount() = news.size

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     */
    override fun onBindViewHolder(holder: ReadNewsViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.news = news[position]
            it.newsCallback = callback
        }
    }

}

class ReadNewsViewHolder(val viewDataBinding: ReadnewsItemBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.readnews_item
    }
}
