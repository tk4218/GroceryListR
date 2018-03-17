package com.tk4218.grocerylistr.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tk4218.grocerylistr.CustomLayout.CalendarAdapter;
import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Model.ApplicationSettings;
import com.tk4218.grocerylistr.Model.CalendarRecipes;
import com.tk4218.grocerylistr.Model.Recipe;
import com.tk4218.grocerylistr.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public  class CalendarFragment extends Fragment {
    private ApplicationSettings mSettings;

    private static final int  DAYS_COUNT = 42;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.US);

    private ProgressBar mLoading;
    private LinearLayout mCalendarHeader;
    private LinearLayout mCalendarContent;
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
        mSettings = new ApplicationSettings(getActivity());

        mCurrentDate = new Date();

        final TextView calendarMonth = rootView.findViewById(R.id.calendar_month);
        calendarMonth.setText(dateFormat.format(mCurrentDate));

        mLoading = rootView.findViewById(R.id.calendar_loading);
        mCalendarHeader = rootView.findViewById(R.id.calendar_header);
        mCalendarContent = rootView.findViewById(R.id.calendar_content);
        mCalendarDays = rootView.findViewById(R.id.grid_month_days);

        ImageButton prevMonth = rootView.findViewById(R.id.button_prev_month);
        prevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mCurrentDate);
                calendar.add(Calendar.MONTH, -1);

                mCurrentDate = calendar.getTime();
                calendarMonth.setText(dateFormat.format(mCurrentDate));
                mLoading.setVisibility(View.VISIBLE);

                new RetrieveCalendar().execute();
            }
        });

        ImageButton nextMonth = rootView.findViewById(R.id.button_next_month);
        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mCurrentDate);
                calendar.add(Calendar.MONTH, 1);

                mCurrentDate = calendar.getTime();
                calendarMonth.setText(dateFormat.format(mCurrentDate));
                mLoading.setVisibility(View.VISIBLE);

                new RetrieveCalendar().execute();
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
        mCalendarHeader.setVisibility(View.GONE);
        mCalendarContent.setVisibility(View.GONE);
        new RetrieveCalendar().execute(mCurrentDate);
    }

    private class RetrieveCalendar extends AsyncTask<Date, Void, ArrayList<CalendarRecipes>> {
        QueryBuilder mQb = new QueryBuilder();

        @Override
        protected ArrayList<CalendarRecipes> doInBackground(Date... params) {

            ArrayList<CalendarRecipes> cells = new ArrayList<>();
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

            JSONResult calendarRecipes = mQb.getMonthCalendarRecipes(mSettings.getUser(), beginDate, endDate);
            if(calendarRecipes == null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }

            calendar.setTime(beginDate);

            for(int i = 0; i < DAYS_COUNT; i++){
                cells.add(new CalendarRecipes(mSettings.getUser(), calendar.getTime(), new ArrayList<Recipe>()));
                if(calendarRecipes.findFirst("CalendarDate", calendar.getTime())){
                    do{
                        cells.get(i).addRecipe(calendarRecipes.getInt("RecipeKey"), calendarRecipes.getString("RecipeName"), calendarRecipes.getString("MealType"));
                    } while (calendarRecipes.findNext("CalendarDate", calendar.getTime()));
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            return cells;
        }

        @Override
        protected void onPostExecute(final ArrayList<CalendarRecipes> result) {
            if(isCancelled() || getActivity() == null) return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(mCurrentDate);
                    mCalendarDays.setAdapter(new CalendarAdapter(getContext(), result, calendar.get(Calendar.MONTH)));
                    mLoading.setVisibility(View.GONE);
                    mCalendarContent.setVisibility(View.VISIBLE);
                    mCalendarHeader.setVisibility(View.VISIBLE);
                }
            });

        }
    }
}