package com.tk4218.grocerylistr.Model;

import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Recipe;

import java.util.ArrayList;
import java.util.Date;

/*
 * Created by Tk4218 on 10/11/2017.
 */

public class CalendarRecipes {
    private QueryBuilder mQb = new QueryBuilder();

    private String mUsername;
    private Date mCalendarDate;
    private ArrayList<Recipe> mRecipes;

    public CalendarRecipes(String username, Date calendarDate){
        setUsername(username);
        setCalendarDate(calendarDate);

        JSONResult calendarRecipes = mQb.getCalendarRecipes(mUsername, calendarDate);
        setCalendarRecipes(calendarRecipes);
    }

    public CalendarRecipes(String username, Date calendarDate, ArrayList<Recipe> recipes){
        setUsername(username);
        setCalendarDate(calendarDate);
        setCalendarRecipes(recipes);
    }

    private void setUsername(String username){ mUsername = username; }

    public void setCalendarDate(Date calendarDate){
        mCalendarDate = calendarDate;
    }
    public Date getCalendarDate(){
        return mCalendarDate;
    }

    public void setCalendarRecipes(JSONResult calendarRecipes){
        mRecipes = new ArrayList<>();
        if(calendarRecipes.getCount() == 0) { return; }

        JSONResult breakfastRecipes = calendarRecipes.filter("MealType", "Breakfast");
        breakfastRecipes.sort("Sequence", JSONResult.SORT_ASCENDING);
        convertMeals(breakfastRecipes);

        JSONResult lunchRecipes = calendarRecipes.filter("MealType", "Lunch");
        breakfastRecipes.sort("Sequence", JSONResult.SORT_ASCENDING);
        convertMeals(lunchRecipes);

        JSONResult dinnerRecipes = calendarRecipes.filter("MealType", "Dinner");
        breakfastRecipes.sort("Sequence", JSONResult.SORT_ASCENDING);
        convertMeals(dinnerRecipes);
    }

    public void setCalendarRecipes(ArrayList<Recipe> calendarRecipes){
        mRecipes = calendarRecipes;
    }

    public void addRecipe(String recipeKey, String recipeName, String mealType){
        mRecipes.add(new Recipe(recipeKey, "", recipeName, mealType, "", "", false, 0, new Date(0), false));
    }

    public ArrayList<Recipe> getCalendarRecipes(){
        return mRecipes;
    }

    public Recipe getRecipe(String mealType, int sequence){
        int seq = 0;
        for (int i = 0; i < mRecipes.size(); i++) {
            if (mRecipes.get(i).getMealType().equals(mealType)){
                if(seq == sequence){
                    return mRecipes.get(i);
                }
                seq++;
            }
        }
        return null;
    }

    public ArrayList<Recipe> getRecipesByMealType(String mealType) {
        ArrayList<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < mRecipes.size(); i++) {
            if (mRecipes.get(i).getMealType().equals(mealType)) {
                recipes.add(mRecipes.get(i));
            }
        }
        return recipes;
    }

    private void convertMeals(JSONResult calendarRecipes){
        for(int i = 0; i < calendarRecipes.getCount(); i++) {
            addRecipe(calendarRecipes.getString(i, "RecipeKey"),
                      calendarRecipes.getString(i, "RecipeName"),
                      calendarRecipes.getString(i, "MealType"));
        }
    }
}


