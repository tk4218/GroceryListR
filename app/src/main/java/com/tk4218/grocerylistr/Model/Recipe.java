package com.tk4218.grocerylistr.Model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by tk4218 on 4/30/2017.
 */
public class Recipe {

    private int recipeKey;
    private String recipeName;
    private String mealType;
    private String mealStyle;
    private String recipeImage;
    private boolean favorite;
    private Date lastMade;
    private Date when;
    private ArrayList<Ingredient> ingredients;

    public Recipe(int RecipeKey){
        //TODO: write query to retrieve recipe from tableRecipe
    }

    public Recipe (int recipeKey, String recipeName, String mealType, String mealStyle, String recipeImage, boolean favorite, Date lastMade, Date when, ArrayList<Ingredient> ingredients){
        setRecipeKey(recipeKey);
        setRecipeName(recipeName);
        setMealType(mealType);
        setMealStyle(mealStyle);
        setRecipeImage(recipeImage);
        setFavorite(favorite);
        setLastMade(lastMade);
        setWhen(when);
        setIngredients(ingredients);
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

    public  String getRecipeImage(){ return recipeImage; }

    public void setRecipeImage(String recipeImage) { this.recipeImage = recipeImage; }

    public boolean getFavorite(){ return favorite; }

    public void setFavorite(boolean favorite) { this.favorite = favorite; }

    public Date getLastMade(){ return lastMade; }

    public void setLastMade(Date lastMade){ this.lastMade = lastMade; }

    public Date getWhen() { return when; }

    public void setWhen(Date when){ this.when = when; }

    public ArrayList<Ingredient> getIngredients(){
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {this.ingredients = ingredients; }

    public void addIngredient(Ingredient ingredient){
        ingredients.add(ingredient);
    }

}
