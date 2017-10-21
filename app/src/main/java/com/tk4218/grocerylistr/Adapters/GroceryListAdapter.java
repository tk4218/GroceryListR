package com.tk4218.grocerylistr.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.tk4218.grocerylistr.Model.GroceryList;
import com.tk4218.grocerylistr.Model.GroceryListItem;

import java.util.List;

/**
 * Created by Tk4218 on 10/19/2017.
 */

public class GroceryListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> mIngredientTypes;
    private  GroceryList mGroceryList;

    public GroceryListAdapter(Context context, List<String> ingredientTypes, GroceryList groceryList){
        mContext = context;
        mIngredientTypes = ingredientTypes;
        mGroceryList = groceryList;
    }

    @Override
    public int getGroupCount() {
        return mIngredientTypes.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroceryList.getGroceryListItems(mIngredientTypes.get(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return mIngredientTypes.get(groupPosition);
    }

    @Override
    public GroceryListItem getChild(int groupPosition, int childPosition) {
        return mGroceryList.getGroceryListItems(mIngredientTypes.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
