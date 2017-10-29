package com.tk4218.grocerylistr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import com.tk4218.grocerylistr.Adapters.GroceryListAdapter;
import com.tk4218.grocerylistr.Model.GroceryList;

public class GroceryListActivity extends AppCompatActivity {

    private int mGroceryListKey;
    private GroceryList mGroceryList;
    private ExpandableListView mGroceryListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            mGroceryListKey = extras.getInt("groceryListKey");
            mGroceryList = new GroceryList(mGroceryListKey);
        }

        mGroceryListView = (ExpandableListView) findViewById(R.id.list_grocerylist);
        mGroceryListView.setAdapter(new GroceryListAdapter(this, mGroceryList.getIngredientTypes(), mGroceryList));
        for(int i = 0; i < mGroceryList.getIngredientTypes().size(); i++){
            mGroceryListView.expandGroup(i);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mGroceryListView.setIndicatorBounds(mGroceryListView.getRight()- 150, mGroceryListView.getRight());

    }
}
