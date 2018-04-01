package com.tk4218.grocerylistr.CustomLayout;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tk4218.grocerylistr.Model.CalendarRecipes;
import com.tk4218.grocerylistr.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*
 * Created by taylo on 12/10/2017.
 */

public class CalendarAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<CalendarRecipes> mDaysOfMonth;
    private int mCurrentMonth;
    private final SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());

    public CalendarAdapter(Context context, ArrayList<CalendarRecipes> daysOfMonth, int currentMonth) {
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
    public CalendarRecipes getItem(int position) {
        return mDaysOfMonth.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(inflater == null){
                return null;
            }
            convertView = inflater.inflate(R.layout.gridview_calendar_day, parent, false);
        }

        LinearLayout calendarDay = convertView.findViewById(R.id.list_calendar_day);
        TextView dayOfMonth = convertView.findViewById(R.id.day_of_month);
        dayOfMonth.setText(dayFormat.format(mDaysOfMonth.get(position).getCalendarDate()));
        ListView dayMeals = convertView.findViewById(R.id.listview_meals);
        dayMeals.setAdapter(new CalendarDayAdapter(mContext, mDaysOfMonth.get(position).getCalendarRecipes(), mDaysOfMonth.get(position).getCalendarDate()));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDaysOfMonth.get(position).getCalendarDate());
        if(calendar.get(Calendar.MONTH) != mCurrentMonth){
            dayOfMonth.setTextColor(Color.LTGRAY);
        } else {
            dayOfMonth.setTextColor(Color.BLACK);
        }


        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if(mDaysOfMonth.get(position).getCalendarDate().equals(calendar.getTime())){
            calendarDay.setBackgroundResource(R.drawable.bg_current_day);
        }

        ViewGroup.LayoutParams params = convertView.getLayoutParams();
        TypedValue tv = new TypedValue();
        mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = mContext.getResources().getDimensionPixelSize(tv.resourceId);
        params.height = ((parent.getHeight() - actionBarHeight) / 6);
        convertView.setLayoutParams(params);
        return convertView;
    }
}
