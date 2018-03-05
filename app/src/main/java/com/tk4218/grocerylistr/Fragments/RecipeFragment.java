package com.tk4218.grocerylistr.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.tk4218.grocerylistr.Adapters.RecipeAdapter;
import com.tk4218.grocerylistr.EditRecipeActivity;
import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Model.ApplicationSettings;
import com.tk4218.grocerylistr.Model.Recipe;
import com.tk4218.grocerylistr.Model.UpdatePinterestRecipes;
import com.tk4218.grocerylistr.R;

import java.util.ArrayList;
import java.util.Date;

public  class RecipeFragment extends Fragment{
    private ApplicationSettings mSettings;

    private boolean mShowUserRecipes;
    private ProgressBar mLoading;
    private SwipeRefreshLayout mRefreshRecipes;
    private RecyclerView mRecyclerView;
    private RecipeAdapter mAdapter;
    private  ArrayList<Recipe> mRecipes;

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
        mSettings = new ApplicationSettings(getActivity());

        /*--------------------------------
         *  Set recipes on grid view
         *--------------------------------*/
        mShowUserRecipes = true;
        mRecipes = new ArrayList<>();
        mLoading = rootView.findViewById(R.id.recipe_loading);
        mRefreshRecipes = rootView.findViewById(R.id.refresh_recipes);
        mRecyclerView = rootView.findViewById(R.id.recipeGridView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        mRefreshRecipes.setColorSchemeColors(Color.RED);
        mRefreshRecipes.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new RetrieveRecipes().execute();
            }
        });

        /*-----------------------------------
         *Set floating action button action
         *-----------------------------------*/
        FloatingActionButton fab = rootView.findViewById(R.id.fab);
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
        if(mSettings.isPinterestLoggedIn()){
            UpdatePinterestRecipes updatePinterestRecipes = new UpdatePinterestRecipes();
            updatePinterestRecipes.execute(mSettings.getUser());
        }

        new RetrieveRecipes().execute();
    }

    public void filterRecipes(String filterString){
        Log.d("SEARCH", "SEARCHING RECIPES");
        if(mAdapter != null)
            mAdapter.getFilter().filter(filterString);
    }

    public void toggleRecipeList(boolean showUserRecipes){
        mShowUserRecipes = showUserRecipes;
        mLoading.setVisibility(View.VISIBLE);
        new RetrieveRecipes().execute();
    }

    private class RetrieveRecipes extends AsyncTask<Boolean, String, String>{
        QueryBuilder mQb = new QueryBuilder();

        @Override
        protected String doInBackground(Boolean... params) {
            mRecipes.clear();
            JSONResult recipes;

            if(mShowUserRecipes){
                recipes = mQb.getUserRecipes(mSettings.getUser());
            }else{
                recipes = mQb.getAllRecipes();
                recipes.addBooleanColumn("Favorite", false);
            }

            recipes.moveFirst();
            for(int i = 0; i < recipes.getCount(); i++){
                addRecipe(recipes.getInt("RecipeKey"),
                        recipes.getString("PinterestId"),
                        recipes.getString("RecipeName"),
                        recipes.getString("MealType"),
                        recipes.getString("CuisineType"),
                        recipes.getString("RecipeImage"),
                        recipes.getBoolean("Favorite"),
                        recipes.getInt("Rating"),
                        recipes.getDate("LastEdited"));
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
                    mAdapter = new RecipeAdapter(RecipeFragment.this.getContext(), mRecipes);
                    mRecyclerView.setAdapter(mAdapter);
                    mRefreshRecipes.setRefreshing(false);
                    mLoading.setVisibility(View.GONE);
                }
            });
        }

        private void addRecipe(int recipeKey, String pinterestId, String recipeName, String mealType, String mealStyle, String recipeImage, boolean favorite, int rating, Date lastEdited){
            mRecipes.add(new Recipe(recipeKey, pinterestId, recipeName, mealType, mealStyle, recipeImage, favorite, rating, lastEdited, null));
        }
    }
}