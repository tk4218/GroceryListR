package com.tk4218.grocerylistr.Model;
import android.util.Log;

import com.tk4218.grocerylistr.Ingredient;

import java.util.ArrayList;
import java.util.regex.*;


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
                    String ingredientName = "";
                    String preparation1 = "";
                    double amount = 0;
                    String unit = "";

                    Pattern namePattern = Pattern.compile("(?<=\"name\":\").*(?=\")");
                    Matcher nameMatch = namePattern.matcher(ingredient);
                    if(nameMatch.find()){
                        ingredientName = nameMatch.group();
                        String nameDetail [] = ingredientName.split(",");
                        if(nameDetail.length > 1){
                            ingredientName = nameDetail[0].trim();
                            preparation1 = nameDetail[1].trim();
                        }

                        Log.d("PARSER", "Ingredient Name: " + ingredientName);
                    }

                    Pattern amountPattern = Pattern.compile("(?<=\"amount\":\").*(?=\",\")");
                    Matcher amountMatch = amountPattern.matcher(ingredient);
                    if(amountMatch.find()){
                        String ingredientAmount = amountMatch.group().replaceFirst("\\\\\\/", "/");
                        Log.d("PARSER", "Ingredient Amount and Unit: " + ingredientAmount);
                        String amountRegex = "[0-9]+\\s([0-9]+\\/[0-9]+)*|([0-9]+\\/[0-9]+)|[0-9]+";
                        amountPattern = Pattern.compile(amountRegex);
                        amountMatch = amountPattern.matcher(ingredientAmount);
                        if(amountMatch.find()){
                            Log.d("PARSER", "Ingredient Amount: " + amountMatch.group());
                            amount = convertToDouble(amountMatch.group());
                            Log.d("PARSER", "Ingredient Actual Amount: " + amount);
                        }
                        unit = ingredientAmount.replaceFirst(amountRegex, "").trim();
                        if(unit.equals(""))
                            unit = "count";
                        Log.d("PARSER", "Ingredient Unit: " + unit);
                    }
                    if(!ingredientName.equals("") && amount != 0 && !unit.equals("")){}
                        //recipeIngredients.add(new Ingredient(0, ingredientName, ingredientCategory, 0, amount, unit, preparation1, ""));
                }
            }
        }
        Log.d("PARSER", "Total Number of Ingredients: " + recipeIngredients.size());
        return  recipeIngredients;
    }

    private double convertToDouble(String fraction){
        double amount = 0;
        String fractionRegex = "[0-9]+\\s*\\/\\s*[0-9]+";

        String wholeNumber = fraction.replaceFirst(fractionRegex, "").trim();
        if(!wholeNumber.equals(""))
            amount += Double.parseDouble(wholeNumber);

        Pattern fractionPattern = Pattern.compile("[0-9]+\\s*\\/\\s*[0-9]+");
        Matcher fractionMatch = fractionPattern.matcher(fraction);
        if(fractionMatch.find()){
            String fractionNumber[] = fractionMatch.group().split("\\/");
            int numerator = Integer.parseInt(fractionNumber[0]);
            int denominator = Integer.parseInt(fractionNumber[1]);
            amount += ((double)numerator / (double)denominator);
        }
        return amount;
    }
}


