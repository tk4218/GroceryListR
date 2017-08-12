package com.tk4218.grocerylistr.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tk4218.grocerylistr.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tk4218 on 8/11/2017.
 */

public class CalendarMealAdapter extends BaseAdapter {

    private Context mContext;
    private HashMap<Integer,ArrayList<String>> mCalendarMeals;

    public  CalendarMealAdapter(Context context, HashMap<Integer, ArrayList<String>> calendarMeals){
        mContext = context;
        mCalendarMeals = calendarMeals;
    }
    @Override
    public int getCount() {
        return mCalendarMeals.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_calendar_meals, null);
        }

        TextView recipeType = (TextView) convertView.findViewById(R.id.calendar_meal_type);
        recipeType.setText(mCalendarMeals.get(position).get(0));

        TextView recipeName = (TextView) convertView.findViewById(R.id.calendar_recipe_meal);
        recipeName.setText("No Meal Selected for Today.");

        ImageView recipeTypeImage = (ImageView) convertView.findViewById(R.id.calendar_meal_type_image);

        switch(position){
            case 0:
                recipeTypeImage.setImageResource(R.drawable.image_breakfast);
                break;
            case 1:
                recipeTypeImage.setImageResource(R.drawable.image_lunch);
                break;
            case 2:
                recipeTypeImage.setImageResource(R.drawable.image_dinner);
                break;
        }

        return convertView;
    }
}
