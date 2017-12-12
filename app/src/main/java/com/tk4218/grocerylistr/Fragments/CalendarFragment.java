package com.tk4218.grocerylistr.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.tk4218.grocerylistr.Adapters.CalendarMealAdapter;
import com.tk4218.grocerylistr.CustomLayout.CalendarAdapter;
import com.tk4218.grocerylistr.Model.GroceryList;
import com.tk4218.grocerylistr.Model.Meal;
import com.tk4218.grocerylistr.Model.MealPlan;
import com.tk4218.grocerylistr.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public  class CalendarFragment extends Fragment {
    private static final int  DAYS_COUNT = 42;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");


    private GridView mCalendarDays;
    private Date mCurrentDate;

    public CalendarFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_calendar, container, false);
        mCurrentDate = new Date();

        TextView calendarMonth = (TextView) rootView.findViewById(R.id.calendar_month);
        calendarMonth.setText(dateFormat.format(mCurrentDate));

        mCalendarDays = (GridView) rootView.findViewById(R.id.grid_month_days);

        return rootView;
    }

    public void onResume() {
        super.onResume();

        /*--------------------------------------------------------------------
         * Retrieving Recipes from the database. Doing it in onResume
         * guarantees the list will be updated upon returning to the fragment.
         *--------------------------------------------------------------------*/
        new RetrieveCalendar().execute(mCurrentDate);
    }

    private class RetrieveCalendar extends AsyncTask<Date, Void, ArrayList<MealPlan>> {

        @Override
        protected ArrayList<MealPlan> doInBackground(Date... params) {

            ArrayList<MealPlan> cells = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mCurrentDate);

            // determine the cell for current month's beginning
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

            // move calendar backwards to the beginning of the week
            calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

            // fill cells (42 days calendar as per our business logic)
            while (cells.size() < DAYS_COUNT)
            {
                cells.add(new MealPlan(calendar.getTime()));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            return cells;
        }

        @Override
        protected void onPostExecute(final ArrayList<MealPlan> result) {
            if(isCancelled() || getActivity() == null) return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(mCurrentDate);
                    mCalendarDays.setAdapter(new CalendarAdapter(getContext(), result, calendar.get(Calendar.MONTH)));
                }
            });

        }
    }
}