package com.tk4218.grocerylistr;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import com.tk4218.grocerylistr.Adapters.AddIngredientAdapter;
import com.tk4218.grocerylistr.Adapters.GroceryListAdapter;
import com.tk4218.grocerylistr.Adapters.IngredientDropdownAdapter;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Model.GroceryList;
import com.tk4218.grocerylistr.Model.GroceryListItem;
import com.tk4218.grocerylistr.Model.Ingredient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class GroceryListActivity extends AppCompatActivity {

    private int mGroceryListKey;
    private GroceryList mGroceryList;
    private ExpandableListView mGroceryListView;
    private GroceryListAdapter mAdapter;
    private AutoCompleteTextView mAddItemName;
    private IngredientDropdownAdapter mIngredientAdapter;
    private Spinner mAddItemType;
    private String mNewItemName;

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

        FloatingActionButton addItem = (FloatingActionButton) findViewById(R.id.add_item);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroceryListItem();
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mGroceryListView.setIndicatorBounds(mGroceryListView.getRight()- 150, mGroceryListView.getRight());

    }

    private void addGroceryListItem(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Grocery List Item");
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_grocerylist_item, null);
        builder.setView(dialogView);

        mAddItemName = (AutoCompleteTextView) dialogView.findViewById(R.id.add_item_name);
        mIngredientAdapter = new IngredientDropdownAdapter(this, R.layout.dropdown_ingredient);
        mAddItemName.setAdapter(mIngredientAdapter);
        final EditText itemAmount = (EditText) dialogView.findViewById(R.id.add_item_amount);
        final Spinner itemMeasurement = (Spinner) dialogView.findViewById(R.id.add_item_measurement);
        mAddItemType = (Spinner) dialogView.findViewById(R.id.add_item_type);

        itemAmount.setText("1.0");

        final String [] measurements = getResources().getStringArray(R.array.measurements);
        for(int i = 0; i < measurements.length; i++){
            if(measurements[i].equals("count")){
                itemMeasurement.setSelection(i);
            }
        }

        mAddItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mNewItemName = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("+ New Ingredient")){
                    mAddItemName.setText(mNewItemName);
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroceryListActivity.this);
                    builder.setTitle("Add New Ingredient");
                    builder.setIcon(android.R.drawable.ic_input_add);
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_ingredient, null);
                    builder.setView(dialogView);
                    final EditText newIngredientName  = (EditText) dialogView.findViewById(R.id.new_ingredient_name);
                    newIngredientName.setText(mAddItemName.getText());
                    final Spinner newIngredientType = (Spinner) dialogView.findViewById(R.id.new_ingredient_type);
                    final EditText newIngredientExpAmount = (EditText) dialogView.findViewById(R.id.new_ingredient_exp_amount);
                    final Spinner newIngredientExpInterval = (Spinner) dialogView.findViewById(R.id.new_ingredient_exp_interval);
                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String ingredientType = newIngredientType.getSelectedItem().toString();
                            String interval = newIngredientExpInterval.getSelectedItem().toString();
                            int expiration = Integer.parseInt(newIngredientExpAmount.getText().toString());
                            if(interval.equals("Weeks")) expiration *= 7;
                            if(interval.equals("Months")) expiration *= 30;
                            new AddNewIngredient().execute(newIngredientName.getText().toString(), ingredientType, expiration);
                            final String[] itemTypes = getResources().getStringArray(R.array.ingredient_type);
                            for(int i = 0; i < itemTypes.length; i ++){
                                if(itemTypes[i].equals(ingredientType)){
                                    mAddItemType.setSelection(i);
                                }
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    builder.show();
                }
            }
        });
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String addItemName = mAddItemName.getText().toString();
                double addItemAmount = Double.parseDouble(itemAmount.getText().toString());
                String addItemUnit = itemMeasurement.getSelectedItem().toString();
                String addItemType = mAddItemType.getSelectedItem().toString();

                new AddGroceryListItem().execute(addItemName, addItemAmount, addItemUnit, addItemType);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
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
                mGroceryListView = (ExpandableListView) findViewById(R.id.list_grocerylist);
                mAdapter = new GroceryListAdapter(GroceryListActivity.this, mGroceryList.getIngredientTypes(), mGroceryList);
                mGroceryListView.setAdapter(mAdapter);
                mGroceryListView.expandGroup(0);

                setTitle(dateFormat.format(mGroceryList.getMealPlanDateStart()) + " To " + dateFormat.format(mGroceryList.getMealPlanDateEnd()));
                mDialog.dismiss();
        }
    }

    private class AddNewIngredient extends AsyncTask<Object, Void, String> {
        QueryBuilder mQb = new QueryBuilder();

        @Override
        protected String doInBackground(Object... params) {
            mQb.insertIngredient((String)params[0], (String)params[1], (int)params[2]);
            return (String)params[0];
        }

        @Override
        protected void onPostExecute(String result) {
            mAddItemName.setText(result);
        }
    }

    private class AddGroceryListItem extends AsyncTask<Object, Void, Void> {
        private QueryBuilder mQb = new QueryBuilder();

        @Override
        protected Void doInBackground(Object... params) {
            String itemName = (String)params[0];
            double itemAmount = (double)params[1];
            String itemUnit = (String)params[2];
            String itemType = (String)params[3];

            int groceryListItemKey;
            int ingredientKey;

            GroceryListItem existingItem = mGroceryList.findIngredient(itemName, itemUnit);
            if(existingItem != null){
                groceryListItemKey = existingItem.getGroceryListItemKey();
                ingredientKey = existingItem.getIngredient().getIngredientKey();
                double newAmount = existingItem.getIngredientAmount() + itemAmount;
                mQb.updateGroceryListItemAmount(groceryListItemKey, newAmount);
            } else {
                Ingredient ingredient = new Ingredient(itemName);
                ingredientKey = ingredient.getIngredientKey();
                if(ingredientKey == 0){
                    ingredientKey  = mQb.insertIngredient(itemName, itemType, 0);
                }

                groceryListItemKey =  mQb.insertGroceryListItem(mGroceryListKey, ingredientKey, itemAmount, itemUnit, false);
            }

            mAdapter.addGroceryListItem(groceryListItemKey, ingredientKey, itemAmount, itemUnit);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
