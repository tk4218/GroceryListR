package com.tk4218.grocerylistr.Model;

import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;

import java.util.ArrayList;
import java.util.Date;

/*
 * Created by tk4218 on 4/30/2017.
 */
public class Recipe{
    private QueryBuilder mQb = new QueryBuilder();

    private int mRecipeKey;
    private int mRecipeEditKey;
    private String mPinterestId;
    private String mRecipeName;
    private String mMealType;
    private String mCuisineType;
    private String mRecipeImage;
    private boolean mFavorite;
    private int mRating;
    private Date mLastEdited;
    private boolean mUserRecipe;
    private boolean mUserEdited;
    private ArrayList<Ingredient> mIngredients;

    public Recipe(int recipeKey, String username){
        JSONResult recipe = mQb.getRecipe(recipeKey);

        if(recipe.getCount() > 0){
            setRecipeKey(recipeKey);
            setPinterestId(recipe.getString("PinterestId"));
            setRecipeName(recipe.getString("RecipeName"));
            setMealType(recipe.getString("MealType"));
            setCuisineType(recipe.getString("CuisineType"));
            setRecipeImage(recipe.getString("RecipeImage"));
            setRating(recipe.getInt("Rating"));
            setLastEdited(recipe.getDate("LastEdited"));
        }

        if(!username.equals("")){
            JSONResult userRecipe = mQb.getUserRecipe(username, recipeKey);
            if(userRecipe.getCount() > 0) {
                setUserRecipe(true);
                setFavorite(userRecipe.getBoolean("Favorite"));

                if(userRecipe.getInt("RecipeEditKey") != 0){
                    setUserEdited(true);
                    setRecipeEditKey(userRecipe.getInt("RecipeEditKey"));
                    JSONResult userEditRecipe = mQb.getUserEditRecipe(userRecipe.getInt("RecipeEditKey"));
                    if(userEditRecipe.getCount() > 0){
                        setRecipeName(userEditRecipe.getString("RecipeName"));
                        setMealType(userEditRecipe.getString("MealType"));
                        setCuisineType(userEditRecipe.getString("CuisineType"));
                        setLastEdited(userEditRecipe.getDate("LastEdited"));
                    }
                }
            }
        }

        if (recipe.getCount() > 0) {
            JSONResult recipeIngredients = mQb.getRecipeIngredients(recipeKey);
            if(mUserEdited){
                boolean deleted;
                JSONResult userRecipeIngredients = mQb.getUserRecipeIngredients(username, recipeKey);
                if(userRecipeIngredients.getCount() > 0){
                    do {
                        deleted = false;
                        if(recipeIngredients.findFirst("IngredientKey", userRecipeIngredients.getInt("IngredientKey"))){
                            if(userRecipeIngredients.getBoolean("RemoveIngredient")){
                                recipeIngredients.deleteRow(recipeIngredients.getPosition());
                                deleted = true;
                            }
                        }else{
                            recipeIngredients.addRow();
                        }

                        if(!deleted){
                            recipeIngredients.putInt("IngredientKey", userRecipeIngredients.getInt("IngredientKey"));
                            recipeIngredients.putString("IngredientName", userRecipeIngredients.getString("IngredientName"));
                            recipeIngredients.putString("IngredientType", userRecipeIngredients.getString("IngredientType"));
                            recipeIngredients.putInt("ShelfLife", userRecipeIngredients.getInt("ShelfLife"));
                            recipeIngredients.putDouble("IngredientAmount", userRecipeIngredients.getDouble("IngredientAmount"));
                            recipeIngredients.putString("IngredientUnit", userRecipeIngredients.getString("IngredientUnit"));
                            recipeIngredients.putString("Preparation1", userRecipeIngredients.getString("Preparation1"));
                            recipeIngredients.putString("Preparation2", userRecipeIngredients.getString("Preparation2"));
                        }
                    }while(userRecipeIngredients.moveNext());
                }
            }
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

    public int getRecipeEditKey(){
        return mRecipeEditKey;
    }

    public void setRecipeEditKey(int recipeKey){
        mRecipeEditKey = recipeKey;
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

    public boolean isUserRecipe(){ return mUserRecipe; }

    public void setUserRecipe(boolean userRecipe){ mUserRecipe = userRecipe; }

    public boolean isUserEdited(){ return mUserEdited; }

    public void setUserEdited(boolean userEdited){ mUserEdited = userEdited; }

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
