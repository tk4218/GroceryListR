package com.tk4218.grocerylistr.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.tk4218.grocerylistr.R;

import java.util.ArrayList;


public class IngredientDropdownAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private ArrayList<String> mIngredients;
    private ArrayList<String> mAllIngredients;
    private ArrayList<String> mFilteredIngredients;

    public IngredientDropdownAdapter(Context context, int viewResouceId, ArrayList<String> ingredients){
        super(context, viewResouceId, ingredients);
        mContext = context;
        mIngredients = (ArrayList<String>)ingredients.clone();
        mAllIngredients = (ArrayList<String>) mIngredients.clone();
        mFilteredIngredients = new ArrayList<>();
    }

    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.dropdown_ingredient, null);
        }
        TextView ingredient = (TextView) convertView.findViewById(R.id.select_ingredient);
        ingredient.setText(getItem(position));
        return convertView;
    }

    @Override
    public Filter getFilter(){
        return mFilter;
    }

    private Filter mFilter = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if(constraint!= null){
                mFilteredIngredients.clear();
                for(String ingredient : mAllIngredients){
                    if(ingredient.toLowerCase().contains(constraint.toString().toLowerCase())){
                        mFilteredIngredients.add(ingredient);
                    }
                }
                results.values = mFilteredIngredients;
                results.count = mFilteredIngredients.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            ArrayList<String> filteredList = (ArrayList<String>) results.values;
            clear();
            if (results != null && results.count > 0) {
                for (String ingredient : filteredList) {
                    add(ingredient);
                }
            }

            if (getPosition("+ New Ingredient") != -1) {
                remove("+ New Ingredient");
            }
            add("+ New Ingredient");
            notifyDataSetChanged();
        }
    };
}
