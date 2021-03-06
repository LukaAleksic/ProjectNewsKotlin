package com.example.readnews.everything

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readnews.R
import com.example.readnews.databinding.FragmentEverythingBinding
import com.example.readnews.headlines.NewsClick
import com.example.readnews.util.DATE_FORMAT_DAY_MONTH_YEAR
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class EverythingFragment : Fragment() {

    private lateinit var c : Calendar

    private lateinit var extendButton: ImageButton

    private lateinit var binding: FragmentEverythingBinding


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
    private var everythingAdapter: EverythingAdapter? = null

    private var extended = true

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
                everythingAdapter?.news = articles
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_everything,
            container,
            false
        )
        // Set the lifecycleOwner so DataBinding can observe LiveData
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        everythingAdapter =
            EverythingAdapter(NewsClick { article ->
                view?.findNavController()?.navigate(
                    EverythingFragmentDirections.actionNavigationEverythingToDetailsFragment(
                        article.url
                    )
                )
            })

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = everythingAdapter
        }


        // Observer for the network error.
        viewModel.eventNetworkError.observe(viewLifecycleOwner, Observer { isNetworkError ->
            if (isNetworkError) onNetworkError()
        })

        c = Calendar.getInstance()

        val fromBtn = binding.root.findViewById<Button>(R.id.fromButton)
        fromBtn.setOnClickListener {
            onDateButtonClicked(fromBtn)
        }

        val toBtn = binding.root.findViewById<Button>(R.id.toButton)
        toBtn.setOnClickListener {
            onDateButtonClicked(toBtn)
        }

        fromBtn.text = getDateDisplay(getOldDate())
        toBtn.text = getDateDisplay(getCurrentDate())

        binding.root.findViewById<Button>(R.id.filterButton).setOnClickListener {
            val country: String
            val sortBy: String
            val from: String
            val to: String
            val keyword: String
            var filter = true
            binding.apply {
                country =
                    if (countrySpinner.selectedItem.toString() == getString(R.string.allCountries)) {
                        ""
                    } else {
                        countrySpinner.selectedItem.toString()
                    }
                sortBy =
                    if (sortBySpinner.selectedItem.toString() == getString(R.string.publishedAtString)) {
                        getString(R.string.publishedAt)
                    } else {
                        sortBySpinner.selectedItem.toString()
                    }

                from = fromButton.text.toString()
                to = toButton.text.toString()
                val sdf = SimpleDateFormat(DATE_FORMAT_DAY_MONTH_YEAR, Locale.getDefault())
                if (sdf.parse(from).after(sdf.parse(to))) {
                    Snackbar.make(view!!, getString(R.string.date_error), Snackbar.LENGTH_LONG)
                        .show()
                    binding.root.findViewById<TextView>(R.id.errorKeyword).visibility = View.GONE
                    filter = false
                }
                keyword = keywords.text.toString()
                if (filter && keyword.isEmpty()) {
                    binding.root.findViewById<TextView>(R.id.errorKeyword).visibility = View.VISIBLE
                    Snackbar.make(view!!, getString(R.string.keywords_error), Snackbar.LENGTH_LONG)
                        .show()
                    filter = false
                }
            }
            if (filter) {
                binding.root.findViewById<TextView>(R.id.errorKeyword).visibility = View.GONE
                viewModel.filter(country, sortBy, from, to, keyword)
            }
        }

        extendButton = binding.root.findViewById<ImageButton>(R.id.extendButton)
        extendButton.setOnClickListener {
            if (extended) {
                hideExtendedLayout()
            } else {
                showExtendedLayout()
            }
        }

        return binding.root
    }


    /**
     * Method for displaying a Snackbar error message for network errors.
     */
    private fun onNetworkError() {
        view?.let { nonNullView ->
            val value = viewModel.isNetworkErrorShown.value
            if (value != null && !value) {
                Snackbar.make(
                    nonNullView,
                    getString(R.string.network_error),
                    Snackbar.LENGTH_LONG
                )
                    .show()
                viewModel.onNetworkErrorShown()
            }
        }
    }

    private fun getDateDisplay(date: Date): String {
        return SimpleDateFormat(DATE_FORMAT_DAY_MONTH_YEAR, Locale.getDefault()).format(date)
    }


    private fun onDateButtonClicked(btn: Button) {
        val dpd = DatePickerDialog(
            context!!,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in TextView
                val privCal = Calendar.getInstance()
                privCal.set(year, monthOfYear, dayOfMonth)
                btn.text = getDateDisplay(privCal.time)
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        )
        dpd.datePicker.minDate = getMinDateInMillis()
        dpd.datePicker.maxDate = getMaxDateInMillis()
        dpd.show()
    }

    private fun hideExtendedLayout() {
        binding.apply {
            countryLayout.visibility = View.GONE
            sortLayout.visibility = View.GONE
            dateLayout.visibility = View.GONE
            keywordLayout.visibility = View.GONE
            extended = false
            context?.let { context ->
                extendButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_extend_filter
                    )
                )
            }
        }
    }

    private fun showExtendedLayout() {
        binding.apply {
            countryLayout.visibility = View.VISIBLE
            sortLayout.visibility = View.VISIBLE
            dateLayout.visibility = View.VISIBLE
            keywordLayout.visibility = View.VISIBLE
            extended = true
            context?.let { context ->
                extendButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_fold_filter
                    )
                )
            }
        }
    }

    private fun getMinDateInMillis() : Long {
        c.add(Calendar.MONTH, -1)
        val minDate = c.timeInMillis
        c.add(Calendar.MONTH, +1)
        return minDate as Long
    }

    private fun getMaxDateInMillis() : Long {
        return c.timeInMillis
    }

    private fun getCurrentDate() : Date {
        return c.time
    }

    private fun getOldDate() : Date {
        c.add(Calendar.MONTH, -1)
        val oldDate =  c.time
        c.add(Calendar.MONTH, +1)
        return oldDate
    }
}

