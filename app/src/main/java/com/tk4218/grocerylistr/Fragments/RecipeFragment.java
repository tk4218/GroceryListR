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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.tk4218.grocerylistr.Adapters.RecipeAdapter;
import com.tk4218.grocerylistr.EditRecipeActivity;
import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Model.ApplicationSettings;
import com.tk4218.grocerylistr.Recipe;
import com.tk4218.grocerylistr.Model.UpdatePinterestRecipes;
import com.tk4218.grocerylistr.R;

import java.util.ArrayList;
import java.util.Date;

public  class RecipeFragment extends Fragment{
    private ApplicationSettings mSettings;

    private boolean mShowUserRecipes;
    private boolean mShowFavorites;
    private String mRecipeSort;
    private ProgressBar mLoading;
    private SwipeRefreshLayout mRefreshRecipes;
    private GridLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private RecipeAdapter mAdapter;
    private ArrayList<Recipe> mRecipes;

    private boolean mLoadingRecipes;
    private boolean mAllRecipesLoaded;

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
        mRecipeSort = "";
        mRecipes = new ArrayList<>();
        mLoading = rootView.findViewById(R.id.recipe_loading);
        mRefreshRecipes = rootView.findViewById(R.id.refresh_recipes);
        mRecyclerView = rootView.findViewById(R.id.recipeGridView);
        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecipeAdapter(getContext(), mRecipes);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                int visibleThreshold = 6;

                boolean loadMore = (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold);

                if(loadMore && !mLoadingRecipes && !mAllRecipesLoaded){
                    mLoadingRecipes = true;
                    new RetrieveRecipes().execute(false);
                }
            }
        });

        mRefreshRecipes.setColorSchemeColors(Color.RED);
        mRefreshRecipes.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLoadingRecipes = true;
                new RetrieveRecipes().execute(true);
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

        mLoadingRecipes = true;
        new RetrieveRecipes().execute(true);
    }

    public void filterRecipes(String filterString){
        if(mAdapter != null)
            mAdapter.getFilter().filter(filterString);
    }

    public void toggleRecipeList(boolean showUserRecipes, String recipeSort, boolean favorites){
        if(favorites){
            mShowUserRecipes = true;
        } else {
            mShowUserRecipes = showUserRecipes;
        }
        mShowFavorites = favorites;
        mRecipeSort = recipeSort;
        mLoading.setVisibility(View.VISIBLE);
        mLoadingRecipes = true;
        new RetrieveRecipes().execute(true);
    }

    private class RetrieveRecipes extends AsyncTask<Boolean, String, String>{
        QueryBuilder mQb = new QueryBuilder();

        @Override
        protected String doInBackground(Boolean... params) {
            if(params[0]){
                mRecipes.clear();
                mAllRecipesLoaded = false;
            }
            JSONResult recipes;

            if(mShowUserRecipes){
                recipes = mQb.getUserRecipes(mSettings.getUser(), mRecipes.size(), mShowFavorites, mRecipeSort);
            }else{
                recipes = mQb.getAllRecipes(mSettings.getUser(), mRecipes.size(), mRecipeSort);
            }

            recipes.moveFirst();
            for(int i = 0; i < recipes.getCount(); i++){
                addRecipe(recipes.getString("RecipeKey"),
                          recipes.getString("PinterestId"),
                          recipes.getString("RecipeName"),
                          recipes.getString("MealType"),
                          recipes.getString("CuisineType"),
                          recipes.getString("RecipeImage"),
                          recipes.getBoolean("Favorite"),
                          recipes.getInt("Rating"),
                          recipes.getDate("LastEdited"),
                          recipes.getString("Username"));
                recipes.moveNext();
            }

            if(recipes.getCount() < 10) mAllRecipesLoaded = true;
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(isCancelled() || getActivity() == null) return;

            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                    mRefreshRecipes.setRefreshing(false);
                    mLoading.setVisibility(View.GONE);
                    mLoadingRecipes = false;
                }
            });
        }

        private void addRecipe(String recipeKey, String pinterestId, String recipeName, String mealType, String mealStyle, String recipeImage, boolean favorite, int rating, Date lastEdited, String username){
            boolean userRecipe = username.equals(mSettings.getUser());
            mRecipes.add(new Recipe(recipeKey, pinterestId, recipeName, mealType, mealStyle, recipeImage, favorite, rating, lastEdited, userRecipe));
        }
    }
}