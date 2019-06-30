package com.tk4218.grocerylistr;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tk4218.grocerylistr.adapters.GroceryListAdapter;
import com.tk4218.grocerylistr.adapters.IngredientDropdownAdapter;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.model.GroceryList;
import com.tk4218.grocerylistr.model.GroceryListItem;
import com.tk4218.grocerylistr.model.Ingredient;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class GroceryListActivity extends AppCompatActivity {

    private int mGroceryListKey;
    private GroceryList mGroceryList;

    private ExpandableListView mGroceryListView;
    private LinearLayout mAddItem;
    private EditText mAddItemAmount;
    private Spinner mAddItemMeasurement;
    private AutoCompleteTextView mAddItemText;
    private GroceryListAdapter mAdapter;
    private TextView mUndo;

    final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            mGroceryListKey = extras.getInt("groceryListKey");
            new GetGroceryList().execute();
        }

        mAddItem = findViewById(R.id.add_item_layout);
        mAddItemAmount = findViewById(R.id.add_item_amount);
        mAddItemMeasurement = findViewById(R.id.add_item_measurement);
        mAddItemText = findViewById(R.id.add_item_name);
        mUndo = findViewById(R.id.undo);

        mAddItem.setVisibility(View.INVISIBLE);
        mUndo.setVisibility(View.INVISIBLE);

        IngredientDropdownAdapter adapter = new IngredientDropdownAdapter(GroceryListActivity.this, R.layout.dropdown_ingredient);
        mAddItemText.setAdapter(adapter);

        mAddItemText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedIngredient = (String) parent.getAdapter().getItem(position);
                if(selectedIngredient.equals("+ New Ingredient")) {
                    mAddItemText.setText("");
                    showNewIngredientDialog();
                }
            }
        });

        final FloatingActionButton addItem = findViewById(R.id.add_item);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAddItem.getVisibility() == View.INVISIBLE){
                    mAddItem.setVisibility(View.VISIBLE);
                    mUndo.setVisibility(View.VISIBLE);
                    addItem.setImageResource(android.R.drawable.checkbox_on_background);

                    mAddItemAmount.setText("1");
                    String [] measurements = getResources().getStringArray(R.array.measurements);
                    for(int i = 0; i < measurements.length; i++){
                        if(measurements[i].toLowerCase().equals("count"))
                            mAddItemMeasurement.setSelection(i);
                    }
                    mAddItemText.setText("");
                } else{
                    String addItemName = mAddItemText.getText().toString();
                    double addItemAmount = Double.parseDouble(mAddItemAmount.getText().toString());
                    String addItemUnit = mAddItemMeasurement.getSelectedItem().toString();
                    new AddGroceryListItem().execute(addItemName, addItemAmount, addItemUnit);

                    mAddItem.setVisibility(View.INVISIBLE);
                    mUndo.setVisibility(View.INVISIBLE);
                    addItem.setImageResource(android.R.drawable.ic_input_add);
                }
            }
        });

        mUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUndo.setVisibility(View.INVISIBLE);
                mAddItem.setVisibility(View.INVISIBLE);
                addItem.setImageResource(android.R.drawable.ic_input_add);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mGroceryListView != null)
            mGroceryListView.setIndicatorBounds(mGroceryListView.getRight()- 150, mGroceryListView.getRight());
    }

    private void showNewIngredientDialog(){
        @SuppressLint("InflateParams")
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_ingredient, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Add New Ingredient")
                .setIcon(android.R.drawable.ic_input_add)
                .setView(dialogView);

        final EditText newIngredientName  = dialogView.findViewById(R.id.new_ingredient_name);
        final Spinner newIngredientType = dialogView.findViewById(R.id.new_ingredient_type);
        final EditText newIngredientExpAmount = dialogView.findViewById(R.id.new_ingredient_exp_amount);
        final Spinner newIngredientExpInterval = dialogView.findViewById(R.id.new_ingredient_exp_interval);

        newIngredientName.setText("");
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ingredientType = newIngredientType.getSelectedItem().toString();
                String interval = newIngredientExpInterval.getSelectedItem().toString();
                int expiration = Integer.parseInt(newIngredientExpAmount.getText().toString());
                if(interval.equals("Weeks")) expiration *= 7;
                if(interval.equals("Months")) expiration *= 30;
                new AddNewIngredient().execute(newIngredientName.getText().toString(), ingredientType, expiration);
            }
        })
                .setNegativeButton("Cancel", null)
                .show();
    }

     private class GetGroceryList extends AsyncTask<Void, Void, Void> {
        ProgressDialog mDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mDialog = new ProgressDialog(GroceryListActivity.this);
            mDialog.setMessage("Getting Grocery List...");
            mDialog.setIndeterminate(false);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mGroceryList = new GroceryList(mGroceryListKey);
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
                mGroceryListView = findViewById(R.id.list_grocerylist);
                mAdapter = new GroceryListAdapter(GroceryListActivity.this, mGroceryList.getIngredientTypes(), mGroceryList);
                mGroceryListView.setAdapter(mAdapter);
                if(mAdapter.getGroupCount() > 0)
                    mGroceryListView.expandGroup(0);

                setTitle(dateFormat.format(mGroceryList.getMealPlanDateStart()) + " To " + dateFormat.format(mGroceryList.getMealPlanDateEnd()));
                mDialog.dismiss();
        }
    }

    private class AddNewIngredient extends AsyncTask<Object, Void, String> {
        QueryBuilder mQb = new QueryBuilder();

        @Override
        protected String doInBackground(Object... params) {
            Ingredient ingredient = Ingredient.Companion.getIngredient((String)params[0]);
            if(ingredient.getIngredientKey() == null)
                mQb.insertIngredient((String)params[0], (String)params[1], (int)params[2]);
            return (String)params[0];
        }

        @Override
        protected void onPostExecute(String result) {
            mAddItemText.setText(result);
        }
    }

    private class AddGroceryListItem extends AsyncTask<Object, Void, Void> {
        private QueryBuilder mQb = new QueryBuilder();

        @Override
        protected Void doInBackground(Object... params) {
            String itemName = (String)params[0];
            double itemAmount = (double)params[1];
            String itemUnit = (String)params[2];

            int groceryListItemKey;
            String ingredientKey;

            GroceryListItem existingItem = mGroceryList.findIngredient(itemName, itemUnit);
            if(existingItem != null){
                groceryListItemKey = existingItem.getGroceryListItemKey();
                ingredientKey = existingItem.getIngredient().getIngredientKey();
                double newAmount = existingItem.getIngredientAmount() + itemAmount;
                mQb.updateGroceryListItemAmount(groceryListItemKey, newAmount);
            } else {
                //Ingredient ingredient = new Ingredient(itemName);
                //ingredientKey = ingredient.getIngredientKey();
                //if(ingredientKey == null){
                    //ingredientKey  = mQb.insertIngredient(itemName, "Uncategorized", 0);
                //}

                //groceryListItemKey =  mQb.insertGroceryListItem(mGroceryListKey, ingredientKey, itemAmount, itemUnit, false);
            }

            //mAdapter.addGroceryListItem(groceryListItemKey, ingredientKey, itemAmount, itemUnit);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
