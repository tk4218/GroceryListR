package com.tk4218.grocerylistr.Model;

import com.tk4218.grocerylistr.Database.QueryBuilder;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Tk4218 on 10/14/2017.
 */

public class GroceryList {
    QueryBuilder mQb = new QueryBuilder();

    private int mGroceryListKey;
    private Date mMealPlanDateStart;
    private Date mMealPlanDateEnd;
    private boolean mGroceryListCompleted;
    private Date mCompletedDate;
    private ArrayList<GroceryListItem> mGroceryListItems;

    public GroceryList(int groceryListKey){

    }


}
