package com.tk4218.grocerylistr.Model;

import java.util.ArrayList;

/**
 * Created by tk4218 on 4/30/2017.
 */
public class Recipe {

    private int recipeKey;
    private String recipeName;
    private String mealType;
    private String mealStyle;
    private ArrayList<Ingredient> ingredients;

    public Recipe(int RecipeKey){

    }

    public int getRecipeKey(){
        return recipeKey;
    }

    public void setRecipeKey(int recipeKey){
        this.recipeKey = recipeKey;
    }

    public String getRecipeName(){
        return recipeName;
    }

    public void setRecipeName(String recipeName){
        this.recipeName = recipeName;
    }

    public String getMealType(){
        return mealType;
    }

    public void setMealType(String mealType){
        this.mealType = mealType;
    }

    public String getMealStyle(){
        return mealStyle;
    }

    public void setMealStyle(String mealStyle){
        this.mealStyle = mealStyle;
    }

    public ArrayList<Ingredient> getIngredients(){
        return ingredients;
    }
    public void addIngredient(Ingredient ingredient){
        ingredients.add(ingredient);
    }

}
