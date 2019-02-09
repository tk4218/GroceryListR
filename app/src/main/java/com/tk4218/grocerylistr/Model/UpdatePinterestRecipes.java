package com.tk4218.grocerylistr.Model;

import android.os.AsyncTask;
import android.util.Log;

import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKPin;
import com.pinterest.android.pdk.PDKResponse;
import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class UpdatePinterestRecipes extends AsyncTask<String, Void, Void> {

    private PDKClient mPDKClient;
    private QueryBuilder mQb = new QueryBuilder();
    private String mUsername;
    private boolean mUpdateFinished;

    @Override
    protected Void doInBackground(final String... params) {
        mUsername = params[0];
        mPDKClient = PDKClient.getInstance();
        mPDKClient.getMyPins("id,metadata,image", new PDKCallback(){

            @Override
            public void onSuccess(PDKResponse response) {
                Log.d("PINTEREST", "Pins Retrieval Successful");

                JSONResult pinterestRecipes = mQb.getPinterestRecipes(mUsername);

                List<PDKPin> pinList = response.getPinList();
                for (PDKPin pin : pinList){

                    String pinterestId = pin.getUid();
                    String recipeMetadata = pin.getMetadata();
                    RecipePinParser parser = new RecipePinParser(recipeMetadata);
                    Log.d("IMAGEURL", pin.getImageUrl());

                    if(parser.isValidRecipe()){
                        if(!pinterestRecipes.findFirst("PinterestId", pinterestId)){

                            String recipeName = parser.getRecipeName();
                            String recipeImage = pin.getImageUrl();
                            ArrayList<Ingredient> recipeIngredients = parser.getRecipeIngredients();

                            int recipeKey = mQb.insertRecipe(pinterestId, recipeName, "", "", recipeImage);
                            mQb.insertUserRecipe(mUsername, recipeKey + "");

                            for(Ingredient ingredient : recipeIngredients){
                                int ingredientKey = 0;
                                Ingredient existingIngredient = new Ingredient(ingredient.getIngredientName());
                                if(existingIngredient.getIngredientKey() == null){
                                    ingredientKey = mQb.insertIngredient(ingredient.getIngredientName(), ingredient.getIngredientType(), 0);
                                } else {
                                    //ingredientKey = existingIngredient.getIngredientKey();
                                }
                                mQb.insertRecipeToIngredient(recipeKey, ingredientKey, ingredient.getIngredientAmount(), ingredient.getIngredientUnit(), ingredient.getPreparation1(), "", false);
                            }
                        }
                    }
                }
                mUpdateFinished = true;
            }

            @Override
            public void onFailure(PDKException exception) {
                Log.d("PINTEREST", "Could Not Retrieve Pins");
                mUpdateFinished = true;
            }
        });
        return null;
    }

    public boolean isUpdateFinished(){
        return mUpdateFinished;
    }
}
