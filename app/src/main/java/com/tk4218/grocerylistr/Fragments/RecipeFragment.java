package com.tk4218.grocerylistr.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.tk4218.grocerylistr.Adapters.RecipeAdapter;
import com.tk4218.grocerylistr.EditRecipeActivity;
import com.tk4218.grocerylistr.Model.JSONResult;
import com.tk4218.grocerylistr.Model.QueryBuilder;
import com.tk4218.grocerylistr.Model.Recipe;
import com.tk4218.grocerylistr.R;

import java.util.ArrayList;
import java.util.Date;


public  class RecipeFragment extends Fragment {

    private GridView mRecipeGridView;
    private  ArrayList<Recipe> mRecipes;
    QueryBuilder mQb = new QueryBuilder();

    public RecipeFragment() {

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RecipeFragment newInstance(int sectionNumber) {
        RecipeFragment fragment = new RecipeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);


        /*********************************
         *  Set recipes on grid view
         *********************************/
        mRecipes = new ArrayList<>();
        mRecipeGridView = (GridView) rootView.findViewById(R.id.recipeGridView);

        //new RetrieveRecipes().execute();

        /**********************************
        Set floating action button action
         **********************************/
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

        /*********************************************************************
         * Retrieving Recipes from the database. Doing it in onResume
         * guarantees the list will be updated upon returning to the fragment.
         *********************************************************************/
        new RetrieveRecipes().execute();
    }

    private class RetrieveRecipes extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {

            Log.d("DEBUG", "Attempting to retrieve recipes");
            JSONResult recipes = mQb.getAllRecipes();

            mRecipes.clear();

            for(int i = 0; i < recipes.getCount(); i++){
                addRecipe(recipes.getInt("RecipeKey"),
                          recipes.getString("RecipeName"),
                          recipes.getString("MealType"),
                          recipes.getString("CuisineType"),
                          recipes.getString("RecipeImage"),
                          (recipes.getInt("Favorite") ==1),
                          new Date(), new Date());
                recipes.moveNext();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    RecipeAdapter adapter = new RecipeAdapter(RecipeFragment.this.getContext(), mRecipes);
                    mRecipeGridView.setAdapter(adapter);
                }
            });
        }

        private void addRecipe(int recipeKey, String recipeName, String mealType, String mealStyle, String recipeImage, boolean favorite, Date lastMade, Date when){
            mRecipes.add(new Recipe(recipeKey, recipeName, mealType, mealStyle, recipeImage, favorite, lastMade, when, null));
        }
    }
}