package com.tk4218.grocerylistr.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Model.GroceryList;
import com.tk4218.grocerylistr.Model.GroceryListItem;
import com.tk4218.grocerylistr.R;

import java.util.List;

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
        }

        //Each expandable group is the ingredient type of the items within the group.
        TextView ingredientType = convertView.findViewById(R.id.header_ingredient_type);
        ingredientType.setText(getGroup(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandlist_grocerylist_item, null);
        }

        final CheckBox groceryListItem = convertView.findViewById(R.id.item_grocerylist_item);
        groceryListItem.setTag(getChild(groupPosition, childPosition));

        /*------------------------------------------------------
         * Set Grocery List Item Display
         *------------------------------------------------------*/
        groceryListItem.setChecked(((GroceryListItem)groceryListItem.getTag()).getAddedToCart());
        if(groceryListItem.isChecked()){
            groceryListItem.setTextColor(Color.LTGRAY);
            groceryListItem.setPaintFlags(groceryListItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            groceryListItem.setTextColor(Color.BLACK);
            groceryListItem.setPaintFlags(groceryListItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        GroceryListItem item = (GroceryListItem)groceryListItem.getTag();
        String groceryListItemText = item.getFormattedIngredientAmount() + " " + item.getIngredientUnit() + " " + item.getIngredient().getIngredientName();
        groceryListItem.setText(groceryListItemText);


        /*-----------------------------------------------------
         * Grocery List Item Event Handlers
         *-----------------------------------------------------*/
        groceryListItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                new UpdateAddedToCart().execute(((GroceryListItem)buttonView.getTag()).getGroceryListItemKey(), isChecked);
                ((GroceryListItem)buttonView.getTag()).setAddedToCart(isChecked);
                if(isChecked){
                    groceryListItem.setTextColor(Color.LTGRAY);
                    groceryListItem.setPaintFlags(groceryListItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }else {
                    groceryListItem.setTextColor(Color.BLACK);
                    groceryListItem.setPaintFlags(groceryListItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });

        groceryListItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Remove Item")
                        .setMessage("Remove " + ((GroceryListItem)v.getTag()).getIngredient().getIngredientName() + " from list?")
                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new RemoveGroceryListItem().execute(((GroceryListItem)v.getTag()).getGroceryListItemKey());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private class UpdateAddedToCart extends AsyncTask<Object, Void, Void> {
        private QueryBuilder mQb = new QueryBuilder();
        @Override
        protected Void doInBackground(Object... params) {

            if(mGroceryList.getGroceryListItemsRemaining() == 0) {
                mGroceryList.setGroceryListCompleted(true);
                mQb.updateGroceryListCompleted(mGroceryList.getGroceryListKey(), true);
            } else {
                if(mGroceryList.getGroceryListCompleted()) {
                    mGroceryList.setGroceryListCompleted(false);
                    mQb.updateGroceryListCompleted(mGroceryList.getGroceryListKey(), false);
                }
            }

            mQb.updateAddedToCart((int)params[0], (boolean)params[1]);

            return null;
        }
    }

    public void addGroceryListItem(int groceryListItemKey, int ingredientKey, double itemAmount, String itemUnit){
        mGroceryList.addGroceryListItem(groceryListItemKey, ingredientKey, itemAmount, itemUnit, true);
        mIngredientTypes = mGroceryList.getIngredientTypes();
    }

    private class RemoveGroceryListItem extends AsyncTask<Integer, Void, Void> {
        private QueryBuilder mQb = new QueryBuilder();

        @Override
        protected Void doInBackground(Integer... params) {
            boolean success = mQb.removeGroceryListItem(params[0]);
            if(success){
                mGroceryList.removeGroceryListItem(params[0]);
                mIngredientTypes = mGroceryList.getIngredientTypes();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            notifyDataSetChanged();
        }
    }

}
