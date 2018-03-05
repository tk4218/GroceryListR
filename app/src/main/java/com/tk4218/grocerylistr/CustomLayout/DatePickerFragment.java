package com.tk4218.grocerylistr.CustomLayout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Model.ApplicationSettings;
import com.tk4218.grocerylistr.Model.MealPlan;
import com.tk4218.grocerylistr.Model.Recipe;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private ApplicationSettings mSettings;
    private int mRecipeKey;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        mSettings = new ApplicationSettings(getActivity());

        Bundle arguments = getArguments();
        mRecipeKey = arguments.getInt("recipeKey");

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        new InsertMealPlan().execute(year, monthOfYear, dayOfMonth);
    }

    private class InsertMealPlan extends AsyncTask<Integer, Void, Void> {
        private QueryBuilder mQb = new QueryBuilder();

        @Override
        protected Void doInBackground(Integer... params) {
            Recipe recipe = new Recipe(mRecipeKey, mSettings.getUser());

            Calendar calendar = Calendar.getInstance();
            calendar.set(params[0], params[1], params[2]);

            Date mealPlanDate = calendar.getTime();
            MealPlan mealPlan = new MealPlan(mSettings.getUser(), mealPlanDate);

            mQb.insertMealPlan(mSettings.getUser(), mealPlanDate, recipe.getMealType(), mealPlan.getMealTypeMeals(recipe.getMealType()).size(), recipe.getRecipeKey(), 0, false);
            return null;
        }
    }
}