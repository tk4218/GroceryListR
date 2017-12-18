package com.tk4218.grocerylistr.Model;

import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Tk4218 on 10/11/2017.
 */

public class MealPlan {
    private QueryBuilder mQb = new QueryBuilder();

    private Date mMealPlanDate;
    private ArrayList<Meal> mMeals;

    public MealPlan(Date mealPlanDate){
        setMealPlanDate(mealPlanDate);

        JSONResult mealPlan = mQb.getMealPlan(mealPlanDate);
        setMealPlanMeals(mealPlan);
    }

    public MealPlan(Date mealPlanDate, ArrayList<Meal> meals){
        setMealPlanDate(mealPlanDate);
        setMealPlanMeals(meals);
    }

    public void setMealPlanDate(Date mealPlanDate){
        mMealPlanDate = mealPlanDate;
    }
    public Date getMealPlanDate(){
        return mMealPlanDate;
    }

    public void setMealPlanMeals(JSONResult mealPlanMeals){
        mMeals = new ArrayList<>();
        if(mealPlanMeals.getCount() == 0) { return; }

        JSONResult breakfastMeals = mealPlanMeals.filter("MealType", "Breakfast");
        breakfastMeals.sort("Sequence", JSONResult.SORT_ASCENDING);
        convertMeals(breakfastMeals);

        JSONResult lunchMeals = mealPlanMeals.filter("MealType", "Lunch");
        breakfastMeals.sort("Sequence", JSONResult.SORT_ASCENDING);
        convertMeals(lunchMeals);

        JSONResult dinnerMeals = mealPlanMeals.filter("MealType", "Dinner");
        breakfastMeals.sort("Sequence", JSONResult.SORT_ASCENDING);
        convertMeals(dinnerMeals);
    }

    public void setMealPlanMeals(ArrayList<Meal> mealPlanMeals){
        mMeals = mealPlanMeals;
    }

    public void addMeal(Meal meal){ mMeals.add(meal); }

    public ArrayList<Meal> getMealPlanMeals(){
        return mMeals;
    }

    public Meal getMeal(String mealType, int sequence){
        for (int i = 0; i < mMeals.size(); i++) {
            if (mMeals.get(i).getMealType().equals(mealType) && mMeals.get(i).getMealSequence() == sequence) {
                return mMeals.get(i);
            }
        }
        return new Meal();
    }

    public ArrayList<Meal> getMealTypeMeals(String mealType) {
        ArrayList<Meal> mealTypeMeals = new ArrayList<>();
        for (int i = 0; i < mMeals.size(); i++) {
            if (mMeals.get(i).getMealType().equals(mealType)) {
                mealTypeMeals.add(mMeals.get(i));
            }
        }
        return mealTypeMeals;
    }

    private void convertMeals(JSONResult meals){
        for(int i = 0; i < meals.getCount(); i++) {
            mMeals.add(new Meal(meals.getDate(i, "MealPlanDate"),
                    meals.getString(i, "MealType"),
                    meals.getInt(i, "Sequence"),
                    meals.getInt(i, "RecipeKey"),
                    meals.getBoolean(i, "MealCompleted")));
        }
    }
}


