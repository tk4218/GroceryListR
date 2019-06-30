package com.tk4218.grocerylistr;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.tk4218.grocerylistr.adapters.IngredientListAdapter;
import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.model.Ingredient;

import java.util.ArrayList;

public class IngredientListActivity extends AppCompatActivity {

    private RecyclerView mIngredientList;
    private IngredientListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mIngredientList = findViewById(R.id.ingredient_list);
        mIngredientList.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.add_ingredient);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewIngredientDialog(getLayoutInflater());
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
                /*ingredientList.add(new Ingredient(ingredients.getString(i, "IngredientKey"),
                                                  ingredients.getString(i, "IngredientName"),
                                                  ingredients.getString(i, "IngredientType"),
                                                  ingredients.getInt(i, "ShelfLife"),
                                                  0.0, "", "", ""));*/
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

    private void showNewIngredientDialog(LayoutInflater inflater){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Ingredient");
        builder.setIcon(android.R.drawable.ic_input_add);
        View dialogView = inflater.inflate(R.layout.dialog_new_ingredient, null);
        builder.setView(dialogView);
        final EditText newIngredientName = dialogView.findViewById(R.id.new_ingredient_name);
        final Spinner newIngredientType = dialogView.findViewById(R.id.new_ingredient_type);
        final EditText newIngredientExpAmount = dialogView.findViewById(R.id.new_ingredient_exp_amount);
        final Spinner newIngredientExpInterval = dialogView.findViewById(R.id.new_ingredient_exp_interval);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ingredientType = newIngredientType.getSelectedItem().toString();
                String interval = newIngredientExpInterval.getSelectedItem().toString();
                int expiration = Integer.parseInt(newIngredientExpAmount.getText().toString());
                if(interval.equals("Weeks")) expiration *= 7;
                if(interval.equals("Months")) expiration *= 30;
                new AddIngredient().execute(newIngredientName.getText().toString(), ingredientType, expiration);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        builder.show();
    }

    private class AddIngredient extends AsyncTask<Object, Void, Integer> {
        private QueryBuilder mQb = new QueryBuilder();

        @Override
        protected Integer doInBackground(Object... params) {
            return mQb.insertIngredient((String)params[0], (String)params[1], (int)params[2]);
        }

        @Override
        protected void onPostExecute(Integer result){
            IngredientListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(IngredientListActivity.this, IngredientListActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

}
