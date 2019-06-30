package com.tk4218.grocerylistr.customlayout

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView

import com.tk4218.grocerylistr.model.CalendarRecipes
import com.tk4218.grocerylistr.R

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale

/*
 * Created by taylor on 12/10/2017.
 */

class CalendarAdapter(private val mContext: Context?, private val mDaysOfMonth: ArrayList<CalendarRecipes>, private val mCurrentMonth: Int) : BaseAdapter() {
    private val dayFormat = SimpleDateFormat("dd", Locale.getDefault())

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return mDaysOfMonth.size
    }

    override fun getItem(position: Int): CalendarRecipes {
        return mDaysOfMonth[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view = convertView
        if (view == null) {
            val inflater = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.gridview_calendar_day, parent, false)
        }

        val calendarDay = view!!.findViewById<LinearLayout>(R.id.list_calendar_day)
        val dayOfMonth = view.findViewById<TextView>(R.id.day_of_month)
        dayOfMonth.text = dayFormat.format(mDaysOfMonth[position].calendarDate)
        val dayMeals = view.findViewById<ListView>(R.id.listview_meals)
        dayMeals.adapter = CalendarDayAdapter(mContext, mDaysOfMonth[position].recipes, mDaysOfMonth[position].calendarDate!!)

        val calendar = Calendar.getInstance()
        calendar.time = mDaysOfMonth[position].calendarDate
        if (calendar.get(Calendar.MONTH) != mCurrentMonth) {
            dayOfMonth.setTextColor(Color.LTGRAY)
        } else {
            dayOfMonth.setTextColor(Color.BLACK)
        }

        calendar.time = Date()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (mDaysOfMonth[position].calendarDate == calendar.time) {
            calendarDay.setBackgroundResource(R.drawable.bg_current_day)
        }

        val params = view.layoutParams
        val tv = TypedValue()
        mContext?.theme?.resolveAttribute(android.R.attr.actionBarSize, tv, true)
        val actionBarHeight = mContext?.resources?.getDimensionPixelSize(tv.resourceId) ?: 0
        params.height = (parent.height - actionBarHeight) / 6
        view.layoutParams = params
        return view
    }
}
