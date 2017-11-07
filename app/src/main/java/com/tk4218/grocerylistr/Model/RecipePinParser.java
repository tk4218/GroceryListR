package com.tk4218.grocerylistr.Model;
import android.util.Log;

import java.util.ArrayList;
import java.util.regex.*;

/**
 * Created by Tk4218 on 11/6/2017.
 */

public class RecipePinParser {
    private String mMetadata;
    private boolean mValidRecipe;

    private Pattern mPattern;
    private Matcher mMatch;


    public RecipePinParser(String metadata){
        mMetadata = metadata;
        mPattern = Pattern.compile("(?<=\"recipe\":\\{).*(?=\\}\\})");
        mMatch = mPattern.matcher(mMetadata);
        if(mMatch.find()){
            mMetadata = mMatch.group();
            mValidRecipe = true;
        } else {
            mValidRecipe = false;
        }
    }

    public boolean isValidRecipe(){
        return mValidRecipe;
    }

    public String getRecipeName(){
        mPattern = Pattern.compile("(?<=\"name\":\")(.*?)(?=\",\")");
        mMatch = mPattern.matcher(mMetadata);
        if(mMatch.find()){
            return mMatch.group();
        }
        return "";
    }

    public ArrayList<Ingredient> getRecipeIngredients(){
        ArrayList<Ingredient> recipeIngredients = new ArrayList<>();

        mPattern = Pattern.compile("(?<=\"ingredients\":\\[).*(?=\\])");
        mMatch = mPattern.matcher(mMetadata);
        if(mMatch.find()){
            Log.d("PARSER", mMatch.group());
            String ingredientCategories[] = mMatch.group().split("\\{\"category\":");
            for(int i = 1; i < ingredientCategories.length; i++){

                String ingredientDetail[] = ingredientCategories[i].split(",\"ingredients\":\\[");

                String ingredientCategory = ingredientDetail[0].replace("\"", "");
                Log.d("PARSER", "Ingredient Category: " + ingredientCategory);
                String ingredients[] = ingredientDetail[1].split("\\},\\{");

                for(String ingredient : ingredients){

                    Pattern namePattern = Pattern.compile("(?<=\"name\":\").*(?=\"\\})");
                    Matcher nameMatch = namePattern.matcher(ingredient);
                    String ingredientName = "";
                    if(nameMatch.find()){
                        ingredientName = nameMatch.group();
                        Log.d("PARSER", "Ingredient Name: " + ingredientName);
                    }

                    Pattern amountPattern = Pattern.compile("(?<=\"amount\":\").*(?=\",\")");
                    Matcher amountMatch = amountPattern.matcher(ingredient);
                    String ingredientAmount = "";
                    if(amountMatch.find()){
                        ingredientAmount = amountMatch.group().replaceFirst("\\\\\\/", "/");
                        Log.d("PARSER", "Ingredient Amount: " + ingredientAmount);

                    }
                }
            }
        }
        return  recipeIngredients;
    }
}


