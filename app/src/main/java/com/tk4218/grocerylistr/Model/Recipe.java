package com.tk4218.grocerylistr.Model;

import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by tk4218 on 4/30/2017.
 */
public class Recipe {
    private QueryBuilder mQb = new QueryBuilder();

    private int mRecipeKey;
    private String mRecipeName;
    private String mMealType;
    private String mCuisineType;
    private String mRecipeImage;
    private boolean mFavorite;
    private int mRating;
    private Date mLastMade;
    private Date mLastEdited;
    private ArrayList<Ingredient> mIngredients;

    public Recipe(int recipeKey){
        JSONResult recipe = mQb.getRecipe(recipeKey);
        if(recipe.getCount() > 0){
            setRecipeKey(recipeKey);
            setRecipeName(recipe.getString("RecipeName"));
            setMealType(recipe.getString("MealType"));
            setCuisineType(recipe.getString("CuisineType"));
            setRecipeImage(recipe.getString("RecipeImage"));
            setFavorite(recipe.getBoolean("Favorite"));
            setRating(recipe.getInt("Rating"));
            setLastMade(recipe.getDate("LastMade"));
            setLastEdited(recipe.getDate("LastEdited"));
        }

        JSONResult recipeIngredients = mQb.getRecipeIngredients(recipeKey);
        setIngredients(recipeIngredients);
    }

    public Recipe (int recipeKey, String recipeName, String mealType, String mealStyle, String recipeImage, boolean favorite, int rating, Date lastMade, Date lastEdited, ArrayList<Ingredient> ingredients){
        setRecipeKey(recipeKey);
        setRecipeName(recipeName);
        setMealType(mealType);
        setCuisineType(mealStyle);
        setRecipeImage(recipeImage);
        setFavorite(favorite);
        setRating(rating);
        setLastMade(lastMade);
        setLastEdited(lastEdited);
        setIngredients(ingredients);
    }

    public int getRecipeKey(){
        return mRecipeKey;
    }

    public void setRecipeKey(int recipeKey){
        mRecipeKey = recipeKey;
    }

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

    public Date getLastMade(){ return mLastMade; }

    public void setLastMade(Date lastMade){ mLastMade = lastMade; }

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
                                                 ingredients.getString(i, "IngredientUnit")));
        }
    }

    public void addIngredient(Ingredient ingredient){
        mIngredients.add(ingredient);
    }
}
