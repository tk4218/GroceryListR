package com.tk4218.grocerylistr;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.tk4218.grocerylistr.Adapters.RecipeIngredientAdapter;
import com.tk4218.grocerylistr.CustomLayout.DatePickerFragment;

import com.tk4218.grocerylistr.Model.Recipe;

public class RecipeActivity extends AppCompatActivity {

    private int mRecipeKey;
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        /*************************************************
         * Populate recipe details
         *************************************************/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ListView recipeIngredientsList = (ListView) findViewById(R.id.recipe_ingredient_list);
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            mRecipeKey = extras.getInt("recipeKey");
            mRecipe = new Recipe(mRecipeKey);
            recipeIngredientsList.setAdapter(new RecipeIngredientAdapter(this, mRecipe.getIngredients()));
            toolbar.setTitle(mRecipe.getRecipeName());
        }

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                Bundle arguments = new Bundle();
                arguments.putInt("recipeKey", mRecipeKey);
                datePicker.setArguments(arguments);
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
