package com.tk4218.grocerylistr.fragments

import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.tk4218.grocerylistr.customlayout.CalendarAdapter
import com.tk4218.grocerylistr.model.CalendarRecipes
import com.tk4218.grocerylistr.R
import com.tk4218.grocerylistr.model.User

import kotlinx.android.synthetic.main.view_calendar.*

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

interface CalendarLoadedCallback {
    fun onCalendarLoaded(calendarRecipes: ArrayList<CalendarRecipes>)
}

class CalendarFragment : Fragment(), CalendarLoadedCallback {
    private var dateFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
    private val mCalendar = Calendar.getInstance()

    companion object {
        private const val DAYS_COUNT = 42
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(): CalendarFragment {
            return CalendarFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mCalendar.time = Date()
        return inflater.inflate(R.layout.view_calendar, container, false)
    }

    override fun onResume() {
        super.onResume()

        calendar_month.text = dateFormat.format(mCalendar.time)
        button_prev_month.setOnClickListener {
            mCalendar.add(Calendar.MONTH, -1)
            calendar_month.text = dateFormat.format(mCalendar.time)
            calendar_loading.visibility = View.VISIBLE
            populateCalendar()
        }

        button_next_month.setOnClickListener {
            mCalendar.add(Calendar.MONTH, 1)
            calendar_month.text = dateFormat.format(mCalendar.time)
            calendar_loading.visibility = View.VISIBLE
            populateCalendar()
        }

        /*--------------------------------------------------------------------
         * Retrieving Recipes from the database. Doing it in onResume
         * guarantees the list will be updated upon returning to the fragment.
         *--------------------------------------------------------------------*/
        calendar_loading.visibility = View.VISIBLE
        calendar_header.visibility = View.GONE
        calendar_content.visibility = View.GONE
        populateCalendar()
    }

    private fun populateCalendar() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        val beginDate = calendar.time
        calendar.add(Calendar.DAY_OF_MONTH, DAYS_COUNT)
        val endDate = calendar.time

        CalendarRecipes.getCalendar(User.currentUsername(context!!), beginDate, endDate, this)
    }

    override fun onCalendarLoaded(calendarRecipes: ArrayList<CalendarRecipes>) {
        grid_month_days.adapter = CalendarAdapter(context, calendarRecipes, mCalendar.get(Calendar.MONTH))
        calendar_loading.visibility = View.GONE
        calendar_content.visibility = View.VISIBLE
        calendar_header.visibility = View.VISIBLE
    }
}