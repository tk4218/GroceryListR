package com.tk4218.grocerylistr.Model;

import java.util.Date;

/**
 * Created by Tk4218 on 10/11/2017.
 */

public class Meal {

    private Date mMealPlanDate;
    private String mMealType;
    private int mSequence;
    private Recipe mRecipe;
    private boolean mMealCompleted;

    public Meal(){
    }

    public Meal(Date mealPlanDate, String mealType, int sequence){

    }

    public Meal(Date mealPlanDate, String mealType, int sequence, int recipeKey, boolean mealCompleted){
        setMealPlanDate(mealPlanDate);
        setMealType(mealType);
        setMealSequence(sequence);
        setRecipe(recipeKey);
        setMealCompleted(mealCompleted);
    }

    public void setMealPlanDate(Date mealPlanDate){ mMealPlanDate = mealPlanDate; }
    public Date getMealPlanDate(){ return mMealPlanDate; }

    public void setMealType(String mealType){ mMealType = mealType; }
    public String getMealType(){ return mMealType; }

    public void setMealSequence(int sequence){ mSequence = sequence; }
    public int getMealSequence(){ return mSequence; }

    public void setRecipe(int recipeKey){ mRecipe = new Recipe(recipeKey); }
    public Recipe getRecipe(){ return mRecipe; }

    public void setMealCompleted(boolean mealCompleted){ mMealCompleted = mealCompleted; }
    public boolean getMealCompleted(){ return mMealCompleted; }
}
