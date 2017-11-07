package com.tk4218.grocerylistr;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import com.tk4218.grocerylistr.Adapters.GroceryListAdapter;
import com.tk4218.grocerylistr.Model.GroceryList;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class GroceryListActivity extends AppCompatActivity {

    private int mGroceryListKey;
    private GroceryList mGroceryList;
    private ExpandableListView mGroceryListView;

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
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
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
                mGroceryListView = (ExpandableListView) findViewById(R.id.list_grocerylist);
                mGroceryListView.setAdapter(new GroceryListAdapter(GroceryListActivity.this, mGroceryList.getIngredientTypes(), mGroceryList));
                mGroceryListView.expandGroup(0);

                setTitle(dateFormat.format(mGroceryList.getMealPlanDateStart()) + " To " + dateFormat.format(mGroceryList.getMealPlanDateEnd()));
                mDialog.dismiss();
        }
    }
}
