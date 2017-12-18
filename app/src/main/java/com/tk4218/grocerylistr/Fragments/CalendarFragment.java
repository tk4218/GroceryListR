package com.tk4218.grocerylistr.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
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
import android.widget.Toast;

import com.tk4218.grocerylistr.Adapters.CalendarMealAdapter;
import com.tk4218.grocerylistr.CustomLayout.CalendarAdapter;
import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;
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
        QueryBuilder mQb = new QueryBuilder();

        @Override
        protected ArrayList<MealPlan> doInBackground(Date... params) {

            ArrayList<MealPlan> cells = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mCurrentDate);
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,0,0);
            calendar.set(Calendar.MILLISECOND, 0);

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);


            Date beginDate = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH, DAYS_COUNT);
            Date endDate = calendar.getTime();

            JSONResult mealPlans = mQb.getMonthMealPlans(beginDate, endDate);
            if(mealPlans.getCount() == 0){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            calendar.setTime(beginDate);

            for(int i = 0; i < DAYS_COUNT; i++){
                cells.add(new MealPlan(calendar.getTime(), new ArrayList<Meal>()));
                if(mealPlans.findFirst("MealPlanDate", calendar.getTime())){
                    do{
                        cells.get(i).addMeal(new Meal(mealPlans.getDate("MealPlanDate"), mealPlans.getString("MealType"), mealPlans.getInt("Sequence"), mealPlans.getInt("RecipeKey"), mealPlans.getBoolean("MealCompleted")));
                    } while (mealPlans.findNext("MealPlanDate", calendar.getTime()));
                }
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