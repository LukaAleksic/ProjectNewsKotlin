package com.example.readnews.util

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

/**
 * Binding adapter used to hide the spinner once data is available.
 */
@BindingAdapter("isNetworkError", "playlist")
fun hideIfNetworkError(view: View, isNetworkError: Boolean, journal: Any?) {
    view.visibility = if (journal != null) View.GONE else View.VISIBLE

    if(isNetworkError) {
        view.visibility = View.GONE
    }
}

/**
 * Binding adapter used to display images from URL using Glide
 */
@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String?) {
    if(url !=null)
        Glide.with(imageView.context).load(url).into(imageView)
}