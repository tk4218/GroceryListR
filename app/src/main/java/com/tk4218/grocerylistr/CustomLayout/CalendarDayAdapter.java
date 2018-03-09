package com.tk4218.grocerylistr.CustomLayout;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tk4218.grocerylistr.Model.Meal;
import com.tk4218.grocerylistr.R;
import com.tk4218.grocerylistr.RecipeActivity;

import java.util.ArrayList;

public class CalendarDayAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Meal> mMeals;

    CalendarDayAdapter(Context context, ArrayList<Meal> meals){
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_day_meals, null);
        }

        TextView recipeName = convertView.findViewById(R.id.list_day_meal);
        recipeName.setText(mMeals.get(position).getRecipe().getRecipeName());
        recipeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RecipeActivity.class);
                intent.putExtra("recipeKey", mMeals.get(position).getRecipe().getRecipeKey());
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }
}
