package com.tk4218.grocerylistr.Fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.tk4218.grocerylistr.Adapters.RecipeAdapter;
import com.tk4218.grocerylistr.Model.Ingredient;
import com.tk4218.grocerylistr.Model.Recipe;
import com.tk4218.grocerylistr.R;

import java.util.ArrayList;
import java.util.Date;


public  class RecipeFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private GridView mRecipeGridView;

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
        ArrayList<Recipe> recipes = setRecipes();

        RecipeAdapter adapter = new RecipeAdapter(getContext(), recipes);
        mRecipeGridView = (GridView) rootView.findViewById(R.id.recipeGridView);
        mRecipeGridView.setAdapter(adapter);


        /**********************************
        Set floating action button action
         **********************************/
        FloatingActionButton fab = (FloatingActionButton)rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        return rootView;
    }

    /*
     * This is used to populate test data into gridview. this can be removes when connection to
     * database is finished.
     */
    private ArrayList<Recipe> setRecipes(){
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        recipes.add(new Recipe(1, "Spaghetti", "", "", false, new Date(), new Date(), new ArrayList<Ingredient>()));
        recipes.add(new Recipe(2, "Burgers", "", "", false, new Date(), new Date(), new ArrayList<Ingredient>()));
        recipes.add(new Recipe(3, "Fried Chicken", "", "", false, new Date(), new Date(), new ArrayList<Ingredient>()));
        recipes.add(new Recipe(4, "Chicken Puff Pastries", "", "", false, new Date(), new Date(), new ArrayList<Ingredient>()));
        recipes.add(new Recipe(5, "Steak and Asparagus", "", "", false, new Date(), new Date(), new ArrayList<Ingredient>()));
        recipes.add(new Recipe(6, "Orange Chicken", "", "", false, new Date(), new Date(), new ArrayList<Ingredient>()));
        recipes.add(new Recipe(7, "That One Recipe That We All Really Like", "", "", false, new Date(), new Date(), new ArrayList<Ingredient>()));
        recipes.add(new Recipe(8, "More Food", "", "", false, new Date(), new Date(), new ArrayList<Ingredient>()));
        return  recipes;
    }
}