package com.tk4218.grocerylistr.model;

import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/*
 * Created by Tk4218 on 10/14/2017.
 */

public class GroceryList {
    QueryBuilder mQb = new QueryBuilder();

    private int mGroceryListKey;
    private String mUsername;
    private Date mMealPlanDateStart;
    private Date mMealPlanDateEnd;
    private boolean mGroceryListCompleted;
    private Date mCompletedDate;
    private ArrayList<GroceryListItem> mGroceryListItems;

    public GroceryList(String username){
        setUsername(username);
    }

    public GroceryList(int groceryListKey){
        JSONResult groceryList = mQb.getGroceryList(groceryListKey);
        if(groceryList.getCount() > 0){
            setGroceryListKey(groceryListKey);
            setUsername(groceryList.getString("Username"));
            setMealPlanDateStart(groceryList.getDate("MealPlanDateStart"));
            setMealPlanDateEnd(groceryList.getDate("MealPlanDateEnd"));
            setGroceryListCompleted(groceryList.getBoolean("GroceryListCompleted"));
            setCompletedDate(groceryList.getDate("CompletedDate"));
            JSONResult grocerListItems = mQb.getGroceryListItems(groceryListKey);
            setGroceryListItems(grocerListItems);
        }
    }

    public GroceryList(int groceryListKey, Date mealPlanDateStart, Date mealPlanDateEnd, boolean groceryListCompleted, Date completedDate, ArrayList<GroceryListItem> groceryListItems){
        setGroceryListKey(groceryListKey);
        setMealPlanDateStart(mealPlanDateStart);
        setMealPlanDateEnd(mealPlanDateEnd);
        setGroceryListCompleted(groceryListCompleted);
        setCompletedDate(completedDate);
        setGroceryListItems(groceryListItems);
    }

    public void setGroceryListKey(int groceryListKey){ mGroceryListKey = groceryListKey; }
    public int getGroceryListKey(){ return  mGroceryListKey; }

    private void setUsername(String username){ mUsername = username; }
    public String getUsername(){ return mUsername; }

    public void setMealPlanDateStart(Date startDate){ mMealPlanDateStart = startDate; }
    public Date getMealPlanDateStart(){ return  mMealPlanDateStart; }

    public void setMealPlanDateEnd(Date endDate){ mMealPlanDateEnd = endDate; }
    public Date getMealPlanDateEnd(){ return  mMealPlanDateEnd; }

    public void setGroceryListCompleted(boolean completed){ mGroceryListCompleted = completed; }
    public boolean getGroceryListCompleted(){ return  mGroceryListCompleted; }

    public void setCompletedDate(Date completedDate){ mCompletedDate = completedDate; }
    public Date getCompletedDate(){ return  mCompletedDate; }

    public void setGroceryListItems(ArrayList<GroceryListItem> groceryListItems){
        mGroceryListItems  = groceryListItems;
    }

    public  void setGroceryListItems(JSONResult groceryListItems){
        mGroceryListItems = new ArrayList<>();
        for(int i = 0; i < groceryListItems.getCount(); i++){
            mGroceryListItems.add(new GroceryListItem(groceryListItems.getInt(i,     "GroceryListItemKey"),
                                                      mGroceryListKey,
                                                      groceryListItems.getString(i,     "IngredientKey"),
                                                      groceryListItems.getDouble(i,  "IngredientAmount"),
                                                      groceryListItems.getString(i,  "IngredientUnit"),
                                                      groceryListItems.getBoolean(i, "AddedToCart")));
        }
        sortGroceryListItems();
    }

    public void addGroceryListItem(String ingredientKey, double ingredientAmount, String ingredientUnit, boolean sortList){
        for(GroceryListItem item : mGroceryListItems){
            if(item.getIngredient().getIngredientKey().equals(ingredientKey)){
                if(item.addIngredientAmount(ingredientAmount, ingredientUnit))
                    return;
            }
        }
        mGroceryListItems.add(new GroceryListItem(0, mGroceryListKey, ingredientKey, ingredientAmount, ingredientUnit, false));
        if(sortList) sortGroceryListItems();
    }

    public void addGroceryListItem(int groceryListItemKey, String ingredientKey, double ingredientAmount, String ingredientUnit, boolean sortList){
        for(GroceryListItem item : mGroceryListItems){
            if(item.getIngredient().getIngredientKey().equals(ingredientKey)){
                if(item.addIngredientAmount(ingredientAmount, ingredientUnit))
                    return;
            }
        }
        mGroceryListItems.add(new GroceryListItem(groceryListItemKey, mGroceryListKey, ingredientKey, ingredientAmount, ingredientUnit, false));
        if(sortList) sortGroceryListItems();
    }

    public boolean removeGroceryListItem(int groceryListItemKey){
        for(int i = 0; i < mGroceryListItems.size(); i++){
            if(mGroceryListItems.get(i).getGroceryListItemKey() == groceryListItemKey){
                mGroceryListItems.remove(i);
                return true;
            }
        }
        return false;
    }
    public ArrayList<GroceryListItem> getGroceryListItems(){ return mGroceryListItems; }

    public ArrayList<GroceryListItem> getGroceryListItems(String ingredientType){
        ArrayList<GroceryListItem> filteredGroceryListItems = new ArrayList<>();
        for(GroceryListItem item : mGroceryListItems){
            if(item.getIngredient().getIngredientType().equals(ingredientType)){
                filteredGroceryListItems.add(item);
            }
        }
        return filteredGroceryListItems;
    }

    public int getGroceryListItemsRemaining(){
        int itemsRemaining = 0;
        for(GroceryListItem item : mGroceryListItems){
            if(!item.getAddedToCart()){
                itemsRemaining ++;
            }
        }
        return itemsRemaining;
    }

    public GroceryListItem findIngredient(String ingredientName){
        for(GroceryListItem item : mGroceryListItems){
            if(item.getIngredient().getIngredientName().equals(ingredientName)) return item;
        }
        return null;
    }

    public GroceryListItem findIngredient(String ingredientName, String ingredientUnit){
        for(GroceryListItem item : mGroceryListItems){
            if(item.getIngredient().getIngredientName().equals(ingredientName) && item.getIngredientUnit().equals(ingredientUnit)) return item;
        }
        return null;
    }

    public int generateGroceryList(Date mealPlanDateStart, Date mealPlanDateEnd, boolean createEmptyList){
        if(createEmptyList){
            JSONResult currentGroceryList = mQb.getCurrentGroceryList(mUsername);
            if(currentGroceryList.getCount() > 0)
                mQb.setGroceryListCurrent( false, currentGroceryList.getInt("GroceryListKey"));
            mGroceryListKey = mQb.insertGroceryList(mUsername, mealPlanDateStart, mealPlanDateEnd, true, false, new Date(0));
            mGroceryListItems = new ArrayList<>();

            return mGroceryListKey;
        }

        JSONResult groceryListIngredients = mQb.getIngredientsForGroceryList(mUsername, mealPlanDateStart, mealPlanDateEnd);

        if (groceryListIngredients.getCount() == 0) return 0;

        groceryListIngredients = groceryListIngredients.filter("RemoveIngredient", 0);

        if(mGroceryListItems == null){
            mGroceryListItems = new ArrayList<>();
        }

        for (int i = 0; i < groceryListIngredients.getCount(); i++){
            addGroceryListItem(groceryListIngredients.getString(i, "IngredientKey"),
                               groceryListIngredients.getDouble(i, "IngredientAmount"),
                               groceryListIngredients.getString(i, "IngredientUnit"), false);
        }
        sortGroceryListItems();

        if(mGroceryListItems.size() > 0){

            JSONResult currentGroceryList = mQb.getCurrentGroceryList(mUsername);
            if(currentGroceryList.getCount() > 0)
                mQb.setGroceryListCurrent(false, currentGroceryList.getInt("GroceryListKey"));

            mGroceryListKey = mQb.insertGroceryList(mUsername, mealPlanDateStart, mealPlanDateEnd, true, false, new Date(0));

            if(mGroceryListKey != 0){
                int groceryListItemKey;
                for(GroceryListItem item : mGroceryListItems){
                    groceryListItemKey = mQb.insertGroceryListItem(mGroceryListKey,
                                                                   0,
                                                                   item.getIngredientAmount(),
                                                                   item.getIngredientUnit(),
                                                                   item.getAddedToCart());
                    item.setGroceryListItemKey(groceryListItemKey);
                }
            }
        }
        return mGroceryListKey;
    }

    private void sortGroceryListByTypes(){
        Collections.sort(mGroceryListItems, new Comparator<GroceryListItem>() {
            @Override
            public int compare(GroceryListItem lhs, GroceryListItem rhs) {
                return lhs.getIngredient().getIngredientType().compareTo(rhs.getIngredient().getIngredientType());
            }
        });
    }

    public ArrayList<String> getIngredientTypes(){
        ArrayList<String> ingredientTypes = new ArrayList<>();
        sortGroceryListByTypes();
        String currentIngredientType = "";

        for(GroceryListItem items : mGroceryListItems){
            if(!items.getIngredient().getIngredientType().equals(currentIngredientType)){
                currentIngredientType = items.getIngredient().getIngredientType();
                ingredientTypes.add(currentIngredientType);
            }
        }

        return ingredientTypes;
    }

    private void sortGroceryListItems(){
        ArrayList<GroceryListItem> sortedItems = new ArrayList<>();
        ArrayList<String> ingredientTypes = getIngredientTypes();

        for(String ingredientType: ingredientTypes){
            ArrayList<GroceryListItem> items = getGroceryListItems(ingredientType);

            Collections.sort(items, new Comparator<GroceryListItem>() {
                @Override
                public int compare(GroceryListItem lhs, GroceryListItem rhs) {
                    return lhs.getIngredient().getIngredientName().compareTo(rhs.getIngredient().getIngredientName());
                }
            });
            sortedItems.addAll(items);
        }

        mGroceryListItems = sortedItems;
    }

}