package com.tk4218.grocerylistr.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.tk4218.grocerylistr.Model.GroceryList;
import com.tk4218.grocerylistr.Model.GroceryListItem;
import com.tk4218.grocerylistr.R;

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
        if(convertView == null){
            final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandlist_grocerylist_header, null);

            //ImageView expandIcon = (ImageView)convertView.findViewById(R.id.imageView7);
            //if(isExpanded) expandIcon.setImageResource(R.drawable.collapse_icon);
            //if(!isExpanded) expandIcon.setImageResource(R.drawable.expand_icon);
        }

        TextView ingredientType = (TextView) convertView.findViewById(R.id.header_ingredient_type);
        ingredientType.setText(getGroup(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandlist_grocerylist_item, null);
        }

        CheckBox groceryListItem = (CheckBox) convertView.findViewById(R.id.item_grocerylist_item);
        GroceryListItem item = getChild(groupPosition, childPosition);
        groceryListItem.setText(item.getFormattedIngredientAmount() + " " + item.getIngredientUnit() + " " + item.getIngredient().getIngredientName());
        return convertView;    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
