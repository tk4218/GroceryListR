package com.tk4218.grocerylistr.Model;

import android.util.Log;

import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by tk4218 on 4/30/2017.
 */
public class Recipe {
    private QueryBuilder mQb = new QueryBuilder();

    private int mRecipeKey;
    private String mPinterestId;
    private String mRecipeName;
    private String mMealType;
    private String mCuisineType;
    private String mRecipeImage;
    private boolean mFavorite;
    private int mRating;
    private Date mLastEdited;
    private ArrayList<Ingredient> mIngredients;

    public Recipe(int recipeKey){
        final JSONResult recipe = mQb.getRecipe(recipeKey);
        if(recipe.getCount() > 0) {
            setRecipeKey(recipeKey);
            setPinterestId(recipe.getString("PinterestId"));
            setRecipeName(recipe.getString("RecipeName"));
            setMealType(recipe.getString("MealType"));
            setCuisineType(recipe.getString("CuisineType"));
            setRecipeImage(recipe.getString("RecipeImage"));
            setFavorite(recipe.getBoolean("Favorite"));
            setRating(recipe.getInt("Rating"));
            setLastEdited(recipe.getDate("LastEdited"));

            JSONResult recipeIngredients = mQb.getRecipeIngredients(recipeKey);
            setIngredients(recipeIngredients);
        }
    }

    public Recipe(final String pinterestId){
        JSONResult recipe = mQb.getPinterestRecipe(pinterestId);
        if(recipe.getCount() > 0) {
            setRecipeKey(recipe.getInt("RecipeKey"));
            setPinterestId(pinterestId);
            setRecipeName(recipe.getString("RecipeName"));
            setMealType(recipe.getString("MealType"));
            setCuisineType(recipe.getString("CuisineType"));
            setRecipeImage(recipe.getString("RecipeImage"));
            setFavorite(recipe.getBoolean("Favorite"));
            setRating(recipe.getInt("Rating"));
            setLastEdited(recipe.getDate("LastEdited"));

            JSONResult recipeIngredients = mQb.getRecipeIngredients(mRecipeKey);
            setIngredients(recipeIngredients);
        }
    }

    public Recipe (int recipeKey, final String pinterestId, final String recipeName, final String mealType, final String cuisineType, String recipeImage, boolean favorite, int rating, Date lastEdited, ArrayList<Ingredient> ingredients){
        setRecipeKey(recipeKey);
        setPinterestId(pinterestId);
        setRecipeName(recipeName);
        setMealType(mealType);
        setCuisineType(cuisineType);
        setRecipeImage(recipeImage);
        setFavorite(favorite);
        setRating(rating);
        setLastEdited(lastEdited);
        setIngredients(ingredients);
    }

    public int getRecipeKey(){
        return mRecipeKey;
    }

    public void setRecipeKey(int recipeKey){
        mRecipeKey = recipeKey;
    }

    public String getPinterestId() { return mPinterestId; }

    public void setPinterestId(String pinterestId){ mPinterestId = pinterestId; }

    public String getRecipeName(){
        return mRecipeName;
    }

    public void setRecipeName(String recipeName){
        mRecipeName = recipeName;
    }

    public String getMealType(){
        return mMealType;
    }

    public void setMealType(String mealType){
        mMealType = mealType;
    }

    public String getCuisineType(){
        return mCuisineType;
    }

    public void setCuisineType(String cuisineType){
        mCuisineType = cuisineType;
    }

    public  String getRecipeImage(){ return mRecipeImage; }

    public void setRecipeImage(String recipeImage) { mRecipeImage = recipeImage; }

    public boolean getFavorite(){ return mFavorite; }

    public void setFavorite(boolean favorite) { mFavorite = favorite; }

    public int getRating(){ return mRating; }

    public void setRating(int rating){ mRating = rating;}

    public Date getLastEdited() { return mLastEdited; }

    public void setLastEdited(Date lastEdited){ mLastEdited = lastEdited; }

    public ArrayList<Ingredient> getIngredients(){
        return mIngredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {this.mIngredients = ingredients; }

    public void setIngredients(JSONResult ingredients){
        if(mIngredients == null)
            mIngredients = new ArrayList<>();

        this.mIngredients.clear();

        for(int i = 0; i < ingredients.getCount(); i++){
            this.mIngredients.add(new Ingredient(ingredients.getInt(i, "IngredientKey"),
                                                 ingredients.getString(i, "IngredientName"),
                                                 ingredients.getString(i, "IngredientType"),
                                                 ingredients.getInt(i, "ShelfLife"),
                                                 ingredients.getDouble(i, "IngredientAmount"),
                                                 ingredients.getString(i, "IngredientUnit"),
                                                 ingredients.getString(i, "Preparation1"),
                                                 ingredients.getString(i, "Preparation2")));
        }
    }

    public void addIngredient(Ingredient ingredient){
        mIngredients.add(ingredient);
    }
}
