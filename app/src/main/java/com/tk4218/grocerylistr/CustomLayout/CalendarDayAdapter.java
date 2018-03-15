package com.tk4218.grocerylistr.CustomLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Model.ApplicationSettings;
import com.tk4218.grocerylistr.Model.Meal;
import com.tk4218.grocerylistr.R;
import com.tk4218.grocerylistr.RecipeActivity;

import java.util.ArrayList;
import java.util.Date;

public class CalendarDayAdapter extends BaseAdapter {

    private ApplicationSettings mSettings;
    private Context mContext;
    private ArrayList<Meal> mMeals;

    CalendarDayAdapter(Context context, ArrayList<Meal> meals){
        mSettings = new ApplicationSettings(context);
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
            convertView = inflater.inflate(R.layout.listview_day_meals, parent, false);
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

        recipeName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteMealPlan(mMeals.get(position), position);
                return true;
            }
        });

        return convertView;
    }

    private void deleteMealPlan(final Meal meal, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Un-schedule Recipe from Calendar")
                .setMessage("Are you sure you want to remove " + meal.getRecipe().getRecipeName() + " from your calendar?")
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteMealPlan().execute(meal.getMealPlanDate(), meal.getRecipe().getRecipeKey());
                        mMeals.remove(position);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private class DeleteMealPlan extends AsyncTask<Object, Void, Void> {
        private QueryBuilder mQb = new QueryBuilder();
        @Override
        protected Void doInBackground(Object... params) {
            Date mealPlanDate = (Date) params[0];
            int recipeKey = (int) params[1];

            mQb.deleteMealPlan(mSettings.getUser(), mealPlanDate, recipeKey);
            return null;
        }
    }
}
