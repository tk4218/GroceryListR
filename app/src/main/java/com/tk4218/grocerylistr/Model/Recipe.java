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

    private boolean mRetrieved;

    public Recipe(int recipeKey){
        final JSONResult recipe = mQb.getRecipe(recipeKey);
        if(recipe.getCount() > 0){
            setRecipeKey(recipeKey);
            setPinterestId(recipe.getString("PinterestId"));
            setRecipeName(recipe.getString("RecipeName"));
            setMealType(recipe.getString("MealType"));
            setCuisineType(recipe.getString("CuisineType"));
            setRecipeImage(recipe.getString("RecipeImage"));
            setFavorite(recipe.getBoolean("Favorite"));
            setRating(recipe.getInt("Rating"));
            setLastEdited(recipe.getDate("LastEdited"));

            if(mPinterestId.equals("")){
                JSONResult recipeIngredients = mQb.getRecipeIngredients(recipeKey);
                setIngredients(recipeIngredients);
            } else {
                mRetrieved = false;
                PDKClient.getInstance().getPin(mPinterestId, "image,metadata", new PDKCallback(){
                    @Override
                    public void onSuccess(PDKResponse response) {
                        Log.d("RECIPE URL", response.getPin().getImageUrl());
                        setRecipeImage(response.getPin().getImageUrl());
                        RecipePinParser parser = new RecipePinParser(response.getPin().getMetadata());
                        if(parser.isValidRecipe()){
                            setRecipeName(parser.getRecipeName());
                            Log.d("RECIPE URL", response.getPin().getImageUrl());
                            setRecipeImage(response.getPin().getImageUrl());
                            ArrayList<Ingredient> ingredients = parser.getRecipeIngredients();
                            setIngredients(ingredients);
                            mRetrieved = true;
                        }
                    }

                    @Override
                    public void onFailure(PDKException exception) {
                        Log.e("Error", "Pinterest Recipe Not Found");
                    }
                });

                while(!mRetrieved){
                    Log.d("RECIPE", "Retrieving Recipe from Pinterest...");
                    try {
                        Thread.sleep(10);
                    } catch(Exception e){ e.printStackTrace(); }
                }
            }
        }
    }

    public Recipe(final String pinterestId){
        JSONResult recipe = mQb.getPinterestRecipe(pinterestId);
        if(recipe.getCount() > 0){
            setPinterestId(pinterestId);
            setRecipeKey(recipe.getInt("RecipeKey"));
            setMealType(recipe.getString("MealType"));
            setCuisineType(recipe.getString("CuisineType"));
            setFavorite(recipe.getBoolean("Favorite"));
            setRating(recipe.getInt("Rating"));

            PDKClient.getInstance().getPin(pinterestId, "id,image,metadata", new PDKCallback() {
                @Override
                public void onSuccess(PDKResponse response) {
                    RecipePinParser parser = new RecipePinParser(response.getPin().getMetadata());
                    if(parser.isValidRecipe()){
                        setRecipeName(parser.getRecipeName());
                        Log.d("RECIPE URL", response.getPin().getImageUrl());
                        setRecipeImage(response.getPin().getImageUrl());
                        ArrayList<Ingredient> ingredients = parser.getRecipeIngredients();
                        setIngredients(ingredients);
                    }
                }

                @Override
                public void onFailure(PDKException exception) {
                    Log.e("Error", "Pinterest Recipe Not Found");
                }
            });
        } else {
            PDKClient.getInstance().getPin(pinterestId, "id,image,metadata", new PDKCallback(){
                @Override
                public void onSuccess(PDKResponse response) {
                    RecipePinParser parser = new RecipePinParser(response.getPin().getMetadata());
                    if(parser.isValidRecipe()){
                        setRecipeName(parser.getRecipeName());
                        mPinterestId = pinterestId;
                        mRecipeKey = mQb.insertRecipe(pinterestId, getRecipeName(), "", "", "");
                        Log.d("RECIPE URL", response.getPin().getImageUrl());
                        setRecipeImage(response.getPin().getImageUrl());
                        ArrayList<Ingredient> ingredients = parser.getRecipeIngredients();
                        setIngredients(ingredients);
                    }
                }

                @Override
                public void onFailure(PDKException exception) {
                    Log.e("Error", "Pinterest Recipe Not Found");
                }
            });
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

        if(mRecipeKey == 0 && !mPinterestId.equals("")){
            PDKClient.getInstance().getPin(pinterestId, "id,image,metadata", new PDKCallback(){
                @Override
                public void onSuccess(PDKResponse response) {
                    mPinterestId = pinterestId;
                    mRecipeKey = mQb.insertRecipe(pinterestId, recipeName, mealType, cuisineType, "");
                    Log.d("RECIPE URL", response.getPin().getImageUrl());
                    setRecipeImage(response.getPin().getImageUrl());
                }

                @Override
                public void onFailure(PDKException exception) {
                    Log.e("Error", "Pinterest Recipe Not Found");
                }
            });
        }

        if(!mPinterestId.equals("")){
            PDKClient.getInstance().getPin(mPinterestId, "image,metadata", new PDKCallback(){
                @Override
                public void onSuccess(PDKResponse response) {
                    Log.d("RECIPE URL", response.getPin().getImageUrl());
                    setRecipeImage(response.getPin().getImageUrl());

                    RecipePinParser parser = new RecipePinParser(response.getPin().getMetadata());
                    if(parser.isValidRecipe()){
                        setRecipeName(parser.getRecipeName());
                        Log.d("RECIPE URL", response.getPin().getImageUrl());
                        setRecipeImage(response.getPin().getImageUrl());
                        ArrayList<Ingredient> ingredients = parser.getRecipeIngredients();
                        setIngredients(ingredients);
                    }
                }

                @Override
                public void onFailure(PDKException exception) {
                    Log.e("Error", "Pinterest Recipe Not Found");
                }
            });
        }
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
                                                 ingredients.getString(i, "IngredientUnit")));
        }
    }

    public void addIngredient(Ingredient ingredient){
        mIngredients.add(ingredient);
    }
}
