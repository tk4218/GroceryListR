package com.tk4218.grocerylistr;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tk4218.grocerylistr.Adapters.GroceryListAdapter;
import com.tk4218.grocerylistr.Adapters.IngredientDropdownAdapter;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Model.GroceryList;
import com.tk4218.grocerylistr.Model.GroceryListItem;
import com.tk4218.grocerylistr.Model.Ingredient;

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
                    ingredientKey  = mQb.insertIngredient(itemName, "Uncategorized", 0);
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
