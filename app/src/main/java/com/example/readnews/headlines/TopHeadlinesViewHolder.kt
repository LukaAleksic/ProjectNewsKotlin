package com.example.readnews.headlines

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.readnews.R
import com.example.readnews.databinding.ReadnewsItemBinding

class TopHeadlinesViewHolder(val viewDataBinding: ReadnewsItemBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        const val layout = R.layout.readnews_item
    }
}