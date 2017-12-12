package com.tk4218.grocerylistr.CustomLayout;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tk4218.grocerylistr.Model.MealPlan;
import com.tk4218.grocerylistr.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by taylo on 12/10/2017.
 */

public class CalendarAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<MealPlan> mDaysOfMonth;
    int mCurrentMonth;
    final SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());

    public CalendarAdapter(Context context, ArrayList<MealPlan> daysOfMonth, int currentMonth) {
        mContext = context;
        mDaysOfMonth = daysOfMonth;
        mCurrentMonth = currentMonth;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return mDaysOfMonth.size();
    }

    @Override
    public MealPlan getItem(int position) {
        return mDaysOfMonth.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gridview_calendar_day, null);
        }

        TextView dayOfMonth = (TextView) convertView.findViewById(R.id.day_of_month);
        dayOfMonth.setText(dayFormat.format(mDaysOfMonth.get(position).getMealPlanDate()));
        ListView dayMeals = (ListView) convertView.findViewById(R.id.listview_meals);
        dayMeals.setAdapter(new CalendarDayAdapter(mContext, mDaysOfMonth.get(position).getMealPlanMeals()));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDaysOfMonth.get(position).getMealPlanDate());
        if(calendar.get(Calendar.MONTH) != mCurrentMonth){
            dayOfMonth.setTextColor(Color.GRAY);
        } else {
            dayOfMonth.setTextColor(Color.BLACK);
        }

        return convertView;
    }
}
