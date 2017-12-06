package com.tk4218.grocerylistr;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tk4218.grocerylistr.Adapters.IngredientListAdapter;
import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Model.Ingredient;

import java.util.ArrayList;

public class IngredientListActivity extends AppCompatActivity {

    private RecyclerView mIngredientList;
    private IngredientListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mIngredientList = (RecyclerView) findViewById(R.id.ingredient_list);
        mIngredientList.setLayoutManager(new LinearLayoutManager(this));

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

    @Override
    protected void onResume(){
        super.onResume();
        new GetIngredientList().execute();
    }

    private class GetIngredientList extends AsyncTask<Void,Void,ArrayList<Ingredient>> {
        QueryBuilder mQb = new QueryBuilder();
        ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(IngredientListActivity.this);
            mDialog.setMessage("Getting Ingredients...");
            mDialog.setIndeterminate(false);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.show();
        }

        @Override
        protected ArrayList<Ingredient> doInBackground(Void... params) {
            JSONResult ingredients = mQb.getAllIngredients();
            ingredients.sort("IngredientName", JSONResult.SORT_ASCENDING);

            ArrayList<Ingredient> ingredientList = new ArrayList<>();
            for(int i = 0; i < ingredients.getCount(); i++){
                ingredientList.add(new Ingredient(ingredients.getInt(i, "IngredientKey"),
                                                  ingredients.getString(i, "IngredientName"),
                                                  ingredients.getString(i, "IngredientType"),
                                                  ingredients.getInt(i, "ShelfLife"),
                                                  0.0, "", "", ""));
            }
            return ingredientList;
        }

        @Override
        protected void onPostExecute(ArrayList<Ingredient>  result) {
            mAdapter = new IngredientListAdapter(IngredientListActivity.this, result);
            mIngredientList.setAdapter(mAdapter);
            mDialog.dismiss();
        }
    }

}
