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
import com.example.readnews.util.DATE_FORMAT_DAY_MONTH_YEAR
import java.text.SimpleDateFormat
import java.util.*

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
                if (article != null) {
                    binding.news = article
                    binding.publishedAt.text = cleanDateHour(article!!.publishedAt)
                }

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

    private fun cleanDateHour(date: String): String {
        val dateHour = date.split("T")
        val yearMonthDay = dateHour[0].split("-")
        val hourMinuteSecond = dateHour[1].replace("Z", "")

        val c = Calendar.getInstance()
        c.set(yearMonthDay[0].toInt(), yearMonthDay[1].toInt() - 1, yearMonthDay[2].toInt())
        val cleanDate = getDateHourDisplay(c.time)
        return "$cleanDate, $hourMinuteSecond"
    }

    private fun getDateHourDisplay(date: Date): String {
        return SimpleDateFormat(DATE_FORMAT_DAY_MONTH_YEAR, Locale.getDefault()).format(date)
    }


}