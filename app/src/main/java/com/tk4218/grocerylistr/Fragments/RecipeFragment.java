package com.tk4218.grocerylistr.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKPin;
import com.pinterest.android.pdk.PDKResponse;
import com.tk4218.grocerylistr.Adapters.RecipeAdapter;
import com.tk4218.grocerylistr.EditRecipeActivity;
import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Model.Recipe;
import com.tk4218.grocerylistr.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public  class RecipeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private  ArrayList<Recipe> mRecipes;
    private PDKClient mPDKClient;
    private List<PDKPin> mPDKPinList;
    private AsyncTask<Void, Void, Void> mUpdatePinterestRecipes;
    private boolean mLoggedIn;
    private boolean mUpdateFinished;

    public RecipeFragment() {

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RecipeFragment newInstance() {
        return new RecipeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);


        /*--------------------------------
         *  Set recipes on grid view
         *--------------------------------*/
        mRecipes = new ArrayList<>();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recipeGridView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        //new RetrieveRecipes().execute();

        /*-----------------------------------
         *Set floating action button action
         *-----------------------------------*/
        FloatingActionButton fab = (FloatingActionButton)rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditRecipeActivity.class);
                getActivity().startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        /*-------------------------------------------------------------------
         * Retrieving Recipes from the database. Doing it in onResume
         * guarantees the list will be updated upon returning to the fragment.
         *-------------------------------------------------------------------*/

        mUpdateFinished = false;
        mUpdatePinterestRecipes = new UpdateFromPinterest();
        mUpdatePinterestRecipes.execute();

        new RetrieveRecipes().execute();
    }

    private class UpdateFromPinterest extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mUpdateFinished = false;
            mPDKClient = PDKClient.getInstance();
            mPDKClient.getMyPins("id", new PDKCallback(){
                @Override
                public void onSuccess(PDKResponse response) {
                    Log.d("DEBUG", "Pin Retrieval Success");
                    mPDKPinList = response.getPinList();
                    mLoggedIn = true;
                    mUpdateFinished = true;
                }
                @Override
                public void onFailure(PDKException exception) {
                    Log.e("ERROR", "Failed to Retrieve Pins");
                    mLoggedIn = false;
                    mUpdateFinished = true;
                }
            });
            return null;
        }
    }


    private class RetrieveRecipes extends AsyncTask<Boolean, String, String>{
        QueryBuilder mQb = new QueryBuilder();

        @Override
        protected String doInBackground(Boolean... params) {
            mRecipes.clear();

            boolean loggedIn = false;

            try{
                while(!mUpdateFinished){
                    if(mUpdatePinterestRecipes.getStatus() == Status.FINISHED){
                        Log.d("ASYNCTASK", "Status: Finished");
                    } else if(mUpdatePinterestRecipes.getStatus() == Status.PENDING){
                        Log.d("ASYNCTASK", "Status: Pending");
                    } else if(mUpdatePinterestRecipes.getStatus() == Status.RUNNING) {
                        Log.d("ASYNCTASK", "Status: Running");
                    } else {
                        Log.d("ASYNCTASK", "Status: Unknown");
                    }
                    Thread.sleep(100);
                }
                loggedIn = mLoggedIn;
            } catch (Exception e){
                e.printStackTrace();
            }

            Log.d("PINTEREST", "Logged In: " + (loggedIn ? "Yes" : "No"));

            if(loggedIn){
                for(PDKPin pin : mPDKPinList){
                    new Recipe(pin.getUid());
                }
            }

            Log.d("DEBUG", "Attempting to retrieve recipes");
            final JSONResult recipes = mQb.getAllRecipes();

            recipes.moveFirst();

            for(int i = 0; i < recipes.getCount(); i++){
                if(loggedIn || recipes.getString("PinterestId").equals("")){
                    addRecipe(recipes.getInt("RecipeKey"),
                            recipes.getString("PinterestId"),
                            recipes.getString("RecipeName"),
                            recipes.getString("MealType"),
                            recipes.getString("CuisineType"),
                            recipes.getString("RecipeImage"),
                            recipes.getBoolean("Favorite"),
                            recipes.getInt("Rating"),
                            recipes.getDate("LastEdited"));
                }

                recipes.moveNext();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(isCancelled() || getActivity() == null) return;

            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    RecipeAdapter adapter = new RecipeAdapter(RecipeFragment.this.getContext(), mRecipes);
                    mRecyclerView.setAdapter(adapter);
                }
            });
        }

        private void addRecipe(int recipeKey, String pinterestId, String recipeName, String mealType, String mealStyle, String recipeImage, boolean favorite, int rating, Date lastEdited){
            mRecipes.add(new Recipe(recipeKey, pinterestId, recipeName, mealType, mealStyle, recipeImage, favorite, rating, lastEdited, null));
        }
    }
}