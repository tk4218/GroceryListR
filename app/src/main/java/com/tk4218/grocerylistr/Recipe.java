package com.tk4218.grocerylistr;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;


/*
 * Created by tk4218 on 4/30/2017.
 */
@SuppressWarnings({"unused", "WeakerAccess", "SameParameterValue"})
public class Recipe extends BaseObservable{
    private String mRecipeKey = "";
    private String mRecipeEditKey = "";
    private String mPinterestId = "";

    private String mRecipeName = "";
    private static String mMealType = "";
    private static String mCuisineType = "";
    private String mRecipeImage = "";

    private boolean mFavorite;
    private double mRating;
    private int mRatingCount;
    private double mUserRating;

    private Date mLastEdited;
    private boolean mUserRecipe;
    private boolean mUserEdited;

    private ArrayList<Ingredient> mIngredients;

    private static int mMealTypeSpinnerPosition;
    private static String[] mealTypes;
    private static int mCuisineTypeSpinnerPosition;
    private static String[] cuisineTypes;

    public Recipe(){
        mIngredients = new ArrayList<>();
    }

    public Recipe(Context context){
        mIngredients = new ArrayList<>();
        mealTypes = context.getResources().getStringArray(R.array.meal_types);
        cuisineTypes = context.getResources().getStringArray(R.array.cuisine_type);
    }

    public Recipe(String recipeKey, String username){

    }

    public Recipe (String recipeKey, final String pinterestId, final String recipeName, final String mealType, final String cuisineType, String recipeImage, boolean favorite, double rating, Date lastEdited, boolean userRecipe){
        setRecipeKey(recipeKey);
        setPinterestId(pinterestId);
        setRecipeName(recipeName);
        setMealType(mealType);
        setCuisineType(cuisineType);
        setRecipeImage(recipeImage);
        setFavorite(favorite);
        setRating(rating);
        setLastEdited(lastEdited);
        setUserRecipe(userRecipe);
    }

    public String getRecipeKey(){
        return mRecipeKey;
    }
    public void setRecipeKey(String recipeKey){
        mRecipeKey = recipeKey;
    }

    public String getRecipeEditKey(){
        return mRecipeEditKey;
    }
    public void setRecipeEditKey(String recipeKey){
        mRecipeEditKey = recipeKey;
    }

    public String getPinterestId(){
        return mPinterestId;
    }
    public void setPinterestId(String pinterestId){
        mPinterestId = pinterestId;
    }

    @Bindable
    public String getRecipeName(){
        return mRecipeName;
    }
    public void setRecipeName(String recipeName){
        if (!recipeName.equals(mRecipeName)){
            mRecipeName = recipeName;
            notifyPropertyChanged(BR.recipeName);
        }
    }

    @Bindable
    public String getMealType(){
        return mMealType;
    }
    public void setMealType(String mealType){
        if(!mealType.equals(mMealType)){
            mMealType = mealType;
            notifyPropertyChanged(BR.mealType);
        }
    }

    @Exclude
    @Bindable
    public int getMealTypeSpinnerPosition(){
        return mMealTypeSpinnerPosition;
    }
    public void setMealTypeSpinnerPosition(int mealTypeSpinnerPosition){
        mMealTypeSpinnerPosition = mealTypeSpinnerPosition;
        mMealType = mealTypes[mealTypeSpinnerPosition];
    }

    @Bindable
    public String getCuisineType(){
        return mCuisineType;
    }
    public void setCuisineType(String cuisineType){
        if(!cuisineType.equals(mCuisineType)) {
            mCuisineType = cuisineType;
            notifyPropertyChanged(BR.cuisineType);
        }
    }

    @Exclude
    @Bindable
    public int getCuisineTypeSpinnerPosition(){
        return mCuisineTypeSpinnerPosition;
    }
    public void setCuisineTypeSpinnerPosition(int cuisineTypeSpinnerPosition){
        mCuisineTypeSpinnerPosition = cuisineTypeSpinnerPosition;
        mCuisineType = cuisineTypes[cuisineTypeSpinnerPosition];
    }

    @Bindable
    public  String getRecipeImage(){
        return mRecipeImage;
    }
    public void setRecipeImage(String recipeImage) {
        if(!recipeImage.equals(mRecipeImage)){
            mRecipeImage = recipeImage;
            notifyPropertyChanged(BR.recipeImage);
        }
    }

    @Bindable
    public boolean getFavorite(){
        return mFavorite;
    }
    public void setFavorite(boolean favorite) {
        if(favorite != mFavorite){
            mFavorite = favorite;
            notifyPropertyChanged(BR.favorite);
        }
    }

    @Bindable
    public double getRating(){
        return mRating;
    }
    public void setRating(double rating){
        if(rating != mRating){
            mRating = rating;
            notifyPropertyChanged(BR.rating);
        }
    }

    @Bindable
    public int getRatingCount() {
        return mRatingCount;
    }
    public void setRatingCount(int ratingCount){
        if(ratingCount != mRatingCount) {
            mRatingCount = ratingCount;
            notifyPropertyChanged(BR.ratingCount);
        }
    }

    @Bindable
    public double getUserRating(){
        return mUserRating;
    }
    public void setUserRating(double userRating){
        if(userRating != mUserRating) {
            mUserRating = userRating;
            notifyPropertyChanged(BR.userRating);
        }
    }

    public Date getLastEdited() {
        return mLastEdited;
    }
    public void setLastEdited(Date lastEdited){
        mLastEdited = lastEdited;
    }

    public boolean isUserRecipe(){
        return mUserRecipe;
    }
    public void setUserRecipe(boolean userRecipe){
        mUserRecipe = userRecipe;
    }

    public boolean isUserEdited() {
        return mUserEdited;
    }
    public void setUserEdited(boolean userEdited){
        mUserEdited = userEdited;
    }

    public ArrayList<Ingredient> getIngredients(){
        return mIngredients;
    }
    public void setIngredients(ArrayList<Ingredient> ingredients){
        mIngredients = ingredients;
    }
    public void addIngredient(Ingredient ingredient){
        mIngredients.add(ingredient);
    }

    @BindingAdapter("android:imageUrl")
    public static void loadImage(ImageView view, String recipeImage) {
        if(recipeImage != null) {
            Picasso.with(view.getContext())
                    .load(new File(recipeImage))
                    .fit()
                    .centerCrop()
                    .into(view);
        }
    }

    public static Recipe getRecipe(final String recipeKey){
        final Recipe[] recipe = new Recipe[1];
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recipeRef = database.getReference("recipe/" + recipeKey);
        recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 recipe[0] = dataSnapshot.getValue(Recipe.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Recipe Error", "Unable to Retrieve Recipe: " + recipeKey);
            }
        });
        return recipe[0];
    }

    public static ArrayList<Recipe> getRecipeList(){
        final ArrayList<Recipe> recipeList = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recipeRef = database.getReference("recipe");
        recipeRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                recipeList.add(dataSnapshot.getValue(Recipe.class));
            }
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            public void onCancelled(@NonNull DatabaseError databaseError) {  }
        });
        return recipeList;
    }

    public boolean save(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recipeRef = database.getReference("recipe");

        if(mRecipeKey != null){
            DatabaseReference updateRecipeRef = recipeRef.child(mRecipeKey);
            updateRecipeRef.setValue(this);
        } else {
            DatabaseReference newRecipeRef = recipeRef.push();
            mRecipeKey = newRecipeRef.getKey();
            newRecipeRef.setValue(this);
        }
        return true;
    }
}

