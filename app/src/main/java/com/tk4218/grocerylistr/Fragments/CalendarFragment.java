package com.tk4218.grocerylistr.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tk4218.grocerylistr.Adapters.CalendarMealAdapter;
import com.tk4218.grocerylistr.R;

import java.util.ArrayList;
import java.util.HashMap;

public  class CalendarFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public CalendarFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CalendarFragment newInstance(int sectionNumber) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        ListView calendarMeals = (ListView) rootView.findViewById(R.id.listView);

        HashMap<Integer, ArrayList<String>> meals = new HashMap<Integer, ArrayList<String>>();

        ArrayList<String> breakfast = new ArrayList<>();
        ArrayList<String> lunch = new ArrayList<>();
        ArrayList<String> dinner = new ArrayList<>();

        breakfast.add("Breakfast");
        lunch.add("Lunch");
        dinner.add("Dinner");

        meals.put(0, breakfast);
        meals.put(1, lunch);
        meals.put(2, dinner);

        CalendarMealAdapter calendarMealAdapter = new CalendarMealAdapter(getContext(), meals);
        calendarMeals.setAdapter(calendarMealAdapter);
        return rootView;
    }
}