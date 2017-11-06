package com.tk4218.grocerylistr.Model;

import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;

public class Ingredient {
    private QueryBuilder mQb = new QueryBuilder();

    private int mIngredientKey;
    private String mIngredientName;
    private String mIngredientType;
    private int mShelfLife;
    private double mIngredientAmount;
    private String mIngredientUnit;

    public Ingredient(){

    }

    public Ingredient(int ingredientKey){
        JSONResult ingredient = mQb.getIngredient(ingredientKey);
        if(ingredient.getCount() > 0){
            setIngredientKey(ingredientKey);
            setIngredientName(ingredient.getString("IngredientName"));
            setIngredientType(ingredient.getString("IngredientType"));
            setShelfLife(ingredient.getInt("ShelfLife"));
        }
    }

    public Ingredient(String ingredientName){
        setIngredientName(ingredientName);
        JSONResult ingredient = mQb.getIngredientByName(ingredientName);
        if(ingredient.getCount() > 0){
            setIngredientKey(ingredient.getInt("IngredientKey"));
            setIngredientType(ingredient.getString("IngredientType"));
            setShelfLife(ingredient.getInt("ShelfLife"));
        }
    }

    public Ingredient(int recipeKey, String ingredientName){
        JSONResult ingredient = mQb.getRecipeIngredient(recipeKey, ingredientName);
        if(ingredient.getCount() > 0){
            setIngredientKey(ingredient.getInt("IngredientKey"));
            setIngredientName(ingredientName);
            setIngredientType(ingredient.getString("IngredientType"));
            setShelfLife(ingredient.getInt("ShelfLife"));
            setIngredientAmount(ingredient.getDouble("IngredientAmount"));
            setIngredientUnit(ingredient.getString("IngredientUnit"));
        }
    }

    public Ingredient(int ingredientKey, String ingredientName, String ingredientType, int shelfLife, double ingredientAmount, String ingredientUnit){
        setIngredientKey(ingredientKey);
        setIngredientName(ingredientName);
        setIngredientType(ingredientType);
        setShelfLife(shelfLife);
        setIngredientAmount(ingredientAmount);
        setIngredientUnit(ingredientUnit);
    }

    public void setIngredientKey(int ingredientKey){
        mIngredientKey = ingredientKey;
    }
    public int getIngredientKey(){
        return mIngredientKey;
    }
    public void setIngredientName(String ingredientName){
        mIngredientName = ingredientName;
    }
    public String getIngredientName(){
        return mIngredientName;
    }
    public void setIngredientType(String ingredientType){
        mIngredientType = ingredientType;
    }
    public String getIngredientType(){
        return mIngredientType;
    }
    public void setShelfLife(int shelfLife){
        mShelfLife = shelfLife;
    }
    public int getShelfLife(){
        return mShelfLife;
    }
    public void setIngredientAmount(double ingredientAmount){ mIngredientAmount = ingredientAmount; }
    public double getIngredientAmount(){
        return  mIngredientAmount;
    }
    public String getFormattedIngredientAmount() { return toFraction(mIngredientAmount, 1000);}
    public void setIngredientUnit(String ingredientUnit){ mIngredientUnit = ingredientUnit; }
    public String getIngredientUnit(){
        return mIngredientUnit;
    }

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
