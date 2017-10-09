package com.tk4218.grocerylistr;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.tk4218.grocerylistr.Adapters.RecipeIngredientAdapter;
import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Model.Ingredient;
import com.tk4218.grocerylistr.Model.NonScrollListView;

import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity {

    private int mRecipeKey;
    private QueryBuilder mQb = new QueryBuilder();

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
            JSONResult recipeIngredients = mQb.getRecipeIngredients(mRecipeKey);
            recipeIngredientsList.setAdapter(new RecipeIngredientAdapter(this, recipeIngredients));
            toolbar.setTitle(recipeIngredients.getString("RecipeName"));
        }

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
