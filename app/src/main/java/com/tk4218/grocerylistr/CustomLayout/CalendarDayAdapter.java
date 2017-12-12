package com.tk4218.grocerylistr.CustomLayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tk4218.grocerylistr.Model.Meal;
import com.tk4218.grocerylistr.R;

import java.util.ArrayList;

/**
 * Created by taylo on 12/10/2017.
 */

public class CalendarDayAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<Meal> mMeals;

    public CalendarDayAdapter(Context context, ArrayList<Meal> meals){
        mContext = context;
        mMeals = meals;
    }

    @Override
    public int getCount() {
        return mMeals.size();
    }

    @Override
    public Meal getItem(int position) {
        return mMeals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_day_meals, null);
        }

        TextView recipeName = (TextView) convertView.findViewById(R.id.list_day_meal);
        recipeName.setText(mMeals.get(position).getRecipe().getRecipeName());

        return convertView;
    }
}
