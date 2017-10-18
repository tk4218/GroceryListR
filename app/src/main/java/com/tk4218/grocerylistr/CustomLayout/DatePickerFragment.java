package com.tk4218.grocerylistr.CustomLayout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Model.MealPlan;
import com.tk4218.grocerylistr.Model.Recipe;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tk4218 on 10/10/2017.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private int mRecipeKey;
    private QueryBuilder mQb = new QueryBuilder();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

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
        Recipe recipe = new Recipe(mRecipeKey);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);

        Date mealPlanDate = calendar.getTime();
        MealPlan mealPlan = new MealPlan(mealPlanDate);

        mQb.insertMealPlan(mealPlanDate, recipe.getMealType(), mealPlan.getMealTypeMeals(recipe.getMealType()).size(), recipe.getRecipeKey(), 0, false);
    }
}