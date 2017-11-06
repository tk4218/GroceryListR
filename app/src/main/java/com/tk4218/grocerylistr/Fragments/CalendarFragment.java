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
import android.widget.ListView;
import android.widget.TextView;

import com.tk4218.grocerylistr.Adapters.CalendarMealAdapter;
import com.tk4218.grocerylistr.Model.Meal;
import com.tk4218.grocerylistr.Model.MealPlan;
import com.tk4218.grocerylistr.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public  class CalendarFragment extends Fragment {

    private ListView mCalendarMeals;
    private MealPlan mMealPlan;
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
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        mCalendarMeals = (ListView) rootView.findViewById(R.id.listView);
        final TextView weekDaySelected = (TextView) rootView.findViewById(R.id.weekdaySelected);
        final TextView dateSelected = (TextView) rootView.findViewById(R.id.dateSelected);

        mCurrentDate = new Date();
        final SimpleDateFormat weekdayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        weekDaySelected.setText(weekdayFormat.format(mCurrentDate));
        dateSelected.setText(dateFormat.format(mCurrentDate));

        CalendarView calendarView = (CalendarView) rootView.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);

                mCurrentDate = calendar.getTime();
                weekDaySelected.setText(weekdayFormat.format(mCurrentDate));
                dateSelected.setText(dateFormat.format(mCurrentDate));

                new RetrieveCalendar().execute(mCurrentDate);
            }
        });

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

    private class RetrieveCalendar extends AsyncTask<Date, Void, Void> {

        @Override
        protected Void doInBackground(Date... params) {

            Date calendarDate = params[0];
            Log.d("DEBUG", "Attempting to retrieve calendar");
            mMealPlan = new MealPlan(calendarDate);

            ArrayList<Meal> meals = mMealPlan.getMealPlanMeals();
            if(mMealPlan.getMealTypeMeals("Breakfast").size() == 0){
                meals.add(0, new Meal(mCurrentDate, "Breakfast", 0, 0, false));
            }
            if(mMealPlan.getMealTypeMeals("Lunch").size() == 0){
                meals.add(mMealPlan.getMealTypeMeals("Breakfast").size(), new Meal(mCurrentDate, "Lunch", 0, 0, false));
            }
            if(mMealPlan.getMealTypeMeals("Dinner").size() == 0){
                meals.add(new Meal(mCurrentDate, "Dinner", 0, 0, false));
            }

            mMealPlan.setMealPlanMeals(meals);
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    CalendarMealAdapter calendarMealAdapter = new CalendarMealAdapter(getContext(), mMealPlan.getMealPlanMeals());
                    mCalendarMeals.setAdapter(calendarMealAdapter);
                }
            });
        }
    }
}