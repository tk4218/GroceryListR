package com.tk4218.grocerylistr;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.tk4218.grocerylistr.adapters.GroceryListHistoryAdapter;
import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.model.GroceryList;
import com.tk4218.grocerylistr.model.User;

import java.util.ArrayList;

public class GroceryListHistoryActivity extends AppCompatActivity {
    RecyclerView mGroceryListHistory;
    GroceryListHistoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGroceryListHistory = (RecyclerView) findViewById(R.id.grocerylist_history_list);
        mGroceryListHistory.setLayoutManager(new LinearLayoutManager(this));

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
    protected void onResume() {
        super.onResume();
        new GetGroceryListHistory().execute();
    }

    private class GetGroceryListHistory extends AsyncTask<Void, Void, ArrayList<GroceryList>> {
        QueryBuilder mQb = new QueryBuilder();
        ProgressDialog mDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mDialog = new ProgressDialog(GroceryListHistoryActivity.this);
            mDialog.setMessage("Getting Grocerylist History...");
            mDialog.setIndeterminate(false);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.show();
        }

        @Override
        protected ArrayList<GroceryList> doInBackground(Void... voids) {
            JSONResult groceryListHistory = mQb.getGroceryListHistory(User.Companion.currentUsername(getApplicationContext()));
            ArrayList<GroceryList> groceryList = new ArrayList<>();

            if(groceryListHistory.getCount() != 0){
                for(int i = 0; i < groceryListHistory.getCount(); i++){
                    groceryList.add(new GroceryList(groceryListHistory.getInt(i, "GroceryListKey"),
                                                    groceryListHistory.getDate(i, "MealPlanDateStart"),
                                                    groceryListHistory.getDate(i, "MealPlanDateEnd"),
                                                    groceryListHistory.getBoolean(i, "GroceryListCompleted"),
                                                    groceryListHistory.getDate(i, "CompletedDate"),
                                                    null));
                }
            }

            return groceryList;
        }

        @Override
        protected void onPostExecute(ArrayList<GroceryList> groceryLists) {
            mAdapter = new GroceryListHistoryAdapter(GroceryListHistoryActivity.this, groceryLists);
            mGroceryListHistory.setAdapter(mAdapter);
            mDialog.dismiss();
        }
    }

}
