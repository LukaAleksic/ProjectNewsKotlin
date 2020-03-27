package com.example.readnews.headlines

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.readnews.databinding.ReadnewsItemBinding
import com.example.readnews.domain.Article

/**
 * RecyclerView Adapter for setting up data binding on the items in the list.
 */
class TopHeadlinesAdapter(val callback: NewsClick) : RecyclerView.Adapter<TopHeadlinesViewHolder>() {

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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopHeadlinesViewHolder {
        val withDataBinding: ReadnewsItemBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                TopHeadlinesViewHolder.layout,
                parent,
                false
            )
        return TopHeadlinesViewHolder(withDataBinding)
    }

    override fun getItemCount() = news.size

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     */
    override fun onBindViewHolder(holder: TopHeadlinesViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.news = news[position]
            it.newsCallback = callback
        }
    }

}