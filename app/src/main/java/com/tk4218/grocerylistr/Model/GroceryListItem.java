package com.tk4218.grocerylistr.Model;

import android.util.Log;

import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;


public class GroceryListItem {
    private QueryBuilder mQb = new QueryBuilder();

    private int mGroceryListItemKey;
    private int mGroceryListKey;
    private Ingredient mIngredient;
    private MeasurementConverter mIngredientAmount = new MeasurementConverter();
    private boolean mAddedToCart;

    public GroceryListItem(int groceryListItemKey){
        JSONResult groceryListItem = mQb.getGroceryListItem(groceryListItemKey);
        if(groceryListItem.getCount() > 0){
            setGroceryListItemKey(groceryListItemKey);
            setGroceryListKey(groceryListItem.getInt("GroceryListKey"));
            setIngredient(groceryListItem.getInt("IngredientKey"));
            setIngredientUnit(groceryListItem.getString("IngredientUnit"));
            setIngredientAmount(groceryListItem.getDouble("IngredientAmount"));
            setAddedToCart(groceryListItem.getBoolean("AddedToCart"));
        }
    }

    public GroceryListItem(int groceryListItemKey, int groceryListKey, int ingredientKey, double ingredientAmount, String ingredientUnit, boolean addedToCart){
        setGroceryListItemKey(groceryListItemKey);
        setGroceryListKey(groceryListKey);
        setIngredient(ingredientKey);
        setIngredientUnit(ingredientUnit);
        setIngredientAmount(ingredientAmount);
        setAddedToCart(addedToCart);
    }

    public void setGroceryListItemKey(int groceryListItemKey){ mGroceryListItemKey = groceryListItemKey; }
    public int getGroceryListItemKey(){ return mGroceryListItemKey; }

    public void setGroceryListKey(int groceryListKey){ mGroceryListKey = groceryListKey; }
    public int getGroceryListKey(){ return mGroceryListKey; }

    public void setIngredient(int ingredientKey){ mIngredient = new Ingredient(ingredientKey); }
    public Ingredient getIngredient(){ return mIngredient; }

    public void setIngredientAmount(double amount){
        mIngredientAmount.setmMeasurementAmount(amount);
    }
    public boolean addIngredientAmount(double amount, String unit) {
        return mIngredientAmount.add(amount, unit);
    }
    public double getIngredientAmount(){
        return mIngredientAmount.getMeasurementAmount();
    }
    public String getFormattedIngredientAmount(){
        return toFraction(getIngredientAmount(), 10);
    }

    public void setIngredientUnit(String ingredientUnit){
        mIngredientAmount.setMesurementUnit(ingredientUnit);
    }
    public String getIngredientUnit(){
        return mIngredientAmount.getMeasurementUnit();
    }

    public void setAddedToCart(boolean addedToCart){ mAddedToCart = addedToCart;}
    public boolean getAddedToCart(){ return mAddedToCart; }

    private String toFraction(double d, int factor) {
        StringBuilder sb = new StringBuilder();
        if (d < 0) {
            sb.append('-');
            d = -d;
        }
        long l = (long) d;
        if (l != 0) sb.append(l);
        d -= l;
        double error = Math.abs(d);
        int bestDenominator = 1;
        for(int i=2;i<=factor;i++) {
            double error2 = Math.abs(d - (double) Math.round(d * i) / i);
            if (error2 < error) {
                error = error2;
                bestDenominator = i;
            }
        }
        if (bestDenominator > 1) {
            if (l != 0) sb.append(' ');
            sb.append(Math.round(d * bestDenominator)).append('/').append(bestDenominator);
        }
        return sb.toString();
    }

}
