package com.example.readnews.everything

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readnews.R
import com.example.readnews.databinding.FragmentEverythingBinding
import com.example.readnews.util.DATE_FORMAT_DAY_MONTH_YEAR
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class EverythingFragment : Fragment() {

    private val c = Calendar.getInstance()
    private var currentYear: Int = 1
    private var oldMonth: Int = 0
    private var currentMonth: Int = 0
    private var currentDay: Int = 1

    private var maxDate: Long = 1
    private var minDate: Long = 1

    private lateinit var oldDate: Date
    private lateinit var currentDate: Date


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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentEverythingBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_everything,
            container,
            false
        )
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
        viewModel.eventNetworkError.observe(viewLifecycleOwner, Observer { isNetworkError ->
            if (isNetworkError) onNetworkError()
        })

        assignDates()

        val fromBtn = binding.root.findViewById<Button>(R.id.fromButton)
        fromBtn.setOnClickListener {
            onDateButtonClicked(fromBtn)
        }

        val toBtn = binding.root.findViewById<Button>(R.id.toButton)
        toBtn.setOnClickListener {
            onDateButtonClicked(toBtn)
        }

        fromBtn.text = getDateDisplay(oldDate)
        toBtn.text = getDateDisplay(currentDate)

        binding.root.findViewById<Button>(R.id.filterButton).setOnClickListener {
            val country: String
            val sortBy: String
            val from: String
            val to: String
            val keyword: String
            var filter = true
            binding.apply {
                country = if (countrySpinner.selectedItem.toString() == "all")
                    ""
                else
                    countrySpinner.selectedItem.toString()
                sortBy = if (sortBySpinner.selectedItem.toString() == "date")
                    "publishedAt"
                else
                    sortBySpinner.selectedItem.toString()
                from = fromButton.text.toString()
                to = toButton.text.toString()
                val sdf = SimpleDateFormat(DATE_FORMAT_DAY_MONTH_YEAR, Locale.getDefault())
                if (sdf.parse(from).after(sdf.parse(to))) {
                    Snackbar.make(view!!, getString(R.string.date_error), Snackbar.LENGTH_LONG)
                        .show()
                    filter = false
                }
                keyword = keywords.text.toString().replace(";", "+")
                if (filter && keyword == "") {
                    Snackbar.make(view!!, getString(R.string.keywords_error), Snackbar.LENGTH_LONG)
                        .show()
                    filter = false
                }
            }
            if (filter)
                viewModel.filter(country, sortBy, from, to, keyword)
        }

        val extendButton = binding.root.findViewById<ImageButton>(R.id.extendButton)
        extendButton.setOnClickListener {
            binding.apply {
                if (extended) {
                    countryLayout.visibility = View.GONE
                    sortLayout.visibility = View.GONE
                    dateLayout.visibility = View.GONE
                    keywordLayout.visibility = View.GONE
                    extended = false
                    extendButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_extend_filter
                        )
                    )
                } else {
                    countryLayout.visibility = View.VISIBLE
                    sortLayout.visibility = View.VISIBLE
                    dateLayout.visibility = View.VISIBLE
                    keywordLayout.visibility = View.VISIBLE
                    extended = true
                    extendButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_fold_filter
                        )
                    )
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
                Snackbar.make(view!!, getString(R.string.network_error), Snackbar.LENGTH_LONG)
                    .show()
                viewModel.onNetworkErrorShown()
            }
        }
    }

    private fun getDateDisplay(date: Date): String {
        return SimpleDateFormat(DATE_FORMAT_DAY_MONTH_YEAR, Locale.getDefault()).format(date)
    }

    private fun assignDates() {
        currentYear = c.get(Calendar.YEAR)
        oldMonth = c.get(Calendar.MONTH)
        currentMonth = oldMonth + 1
        currentDay = c.get(Calendar.DAY_OF_MONTH)

        currentDate = c.time
        maxDate = c.timeInMillis
        c.add(Calendar.MONTH, -1)
        oldDate = c.time
        minDate = c.timeInMillis
    }

    private fun onDateButtonClicked(btn: Button) {
        val dpd = DatePickerDialog(
            context!!,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in TextView
                c.set(year,monthOfYear,dayOfMonth)
                btn.text = getDateDisplay(c.time)
            },
            currentYear,
            oldMonth,
            currentDay
        )
        dpd.datePicker.minDate = minDate
        dpd.datePicker.maxDate = maxDate
        dpd.show()
    }
}

