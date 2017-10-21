package com.tk4218.grocerylistr.Model;

import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Tk4218 on 10/14/2017.
 */

public class GroceryList {
    QueryBuilder mQb = new QueryBuilder();

    private int mGroceryListKey;
    private Date mMealPlanDateStart;
    private Date mMealPlanDateEnd;
    private boolean mGroceryListCompleted;
    private Date mCompletedDate;
    private ArrayList<GroceryListItem> mGroceryListItems;

    public GroceryList(){

    }

    public GroceryList(int groceryListKey){
        JSONResult groceryList = mQb.getGroceryList(groceryListKey);
        if(groceryList.getCount() > 0){
            setGroceryListKey(groceryListKey);
            setMealPlanDateStart(groceryList.getDate("MealPlanDateStart"));
            setMealPlanDateEnd(groceryList.getDate("MealPlanDateEnd"));
            setGroceryListCompleted(groceryList.getBoolean("GroceryListCompleted"));
            setCompletedDate(groceryList.getDate("CompletedDate"));
            JSONResult grocerListItems = mQb.getGroceryListItems(groceryListKey);
            setGroceryListItems(grocerListItems);
        }
    }

    public void setGroceryListKey(int groceryListKey){ mGroceryListKey = groceryListKey; }
    public int getGroceryListKey(){ return  mGroceryListKey; }

    public void setMealPlanDateStart(Date startDate){ mMealPlanDateStart = startDate; }
    public Date getMealPlanDateStart(){ return  mMealPlanDateStart; }

    public void setMealPlanDateEnd(Date endDate){ mMealPlanDateEnd = endDate; }
    public Date getMealPlanDateEnd(){ return  mMealPlanDateEnd; }

    public void setGroceryListCompleted(boolean completed){ mGroceryListCompleted = completed; }
    public boolean getGroceryListCompleted(){ return  mGroceryListCompleted; }

    public void setCompletedDate(Date completedDate){ mCompletedDate = completedDate; }
    public Date getCompletedDate(){ return  mCompletedDate; }

    public  void setGroceryListItems(JSONResult groceryListItems){
        mGroceryListItems = new ArrayList<>();
        for(int i = 0; i < groceryListItems.getCount(); i++){
            mGroceryListItems.add(new GroceryListItem(groceryListItems.getInt(i,     "GroceryListItemKey"),
                                                      mGroceryListKey,
                                                      groceryListItems.getInt(i,     "IngredientKey"),
                                                      groceryListItems.getDouble(i,  "IngredientAmount"),
                                                      groceryListItems.getString(i,  "IngredientUnit"),
                                                      groceryListItems.getBoolean(i, "AddedToCart")));
        }
    }
    public ArrayList<GroceryListItem> getGroceryListItems(){ return mGroceryListItems; }

    public ArrayList<GroceryListItem> getGroceryListItems(String ingredientType){
        ArrayList<GroceryListItem> filteredGroceryListItems = new ArrayList<>();
        for(GroceryListItem item : mGroceryListItems){

        }
        return filteredGroceryListItems;
    }

    public int generateGroceryList(Date mealPlanDateStart, Date mealPlanDateEnd){
        return 0;
    }

}
