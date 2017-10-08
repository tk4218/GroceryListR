package com.tk4218.grocerylistr.Model;

/**
 * Created by tk4218 on 4/30/2017.
 */
public class Ingredient {

    private int mIngredientKey;
    private String mIngredientName;
    private String mIngredientType;
    private double mIngredientAmount;
    private  String mIngredientMeasurement;
    private int mShelfLife;

    public Ingredient(){

    }

    public Ingredient(int ingredientKey, String ingredientName, String ingredientType, int shelfLife){
        setIngredientKey(ingredientKey);
        setIngredientName(ingredientName);
        setIngredientType(ingredientType);
        setShelfLife(shelfLife);
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
    public void setIngredientAmount(double ingredientAmount){
        mIngredientAmount = ingredientAmount;
    }
    public double getIngredientAmount(){
        return  mIngredientAmount;
    }
    public void setIngredientMeasurement(String ingredientMeasurement){
        mIngredientMeasurement = ingredientMeasurement;
    }
    public String getIngredientMeasurement(){
        return mIngredientMeasurement;
    }
}
