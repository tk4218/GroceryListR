package com.tk4218.grocerylistr.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tk4218.grocerylistr.Model.Meal;
import com.tk4218.grocerylistr.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tk4218 on 8/11/2017.
 */

public class CalendarMealAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Meal> mCalendarMeals;

    public  CalendarMealAdapter(Context context, ArrayList<Meal> calendarMeals){
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
        recipeType.setText(mCalendarMeals.get(position).getMealType());

        TextView recipeName = (TextView) convertView.findViewById(R.id.calendar_recipe_meal);
        if(mCalendarMeals.get(position).getRecipe().getRecipeKey() != 0)
            recipeName.setText(mCalendarMeals.get(position).getRecipe().getRecipeName());
        else
            recipeName.setText("No meal selected for today");

        ImageView recipeTypeImage = (ImageView) convertView.findViewById(R.id.calendar_meal_type_image);

        switch(mCalendarMeals.get(position).getMealType().toLowerCase()){
            case "breakfast":
                recipeTypeImage.setImageResource(R.drawable.breakfast_icon);
                break;
            case "lunch":
                recipeTypeImage.setImageResource(R.drawable.lunch_icon);
                break;
            case "dinner":
                recipeTypeImage.setImageResource(R.drawable.dinner_icon);
                break;
        }

        return convertView;
    }
}
