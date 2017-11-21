package com.tk4218.grocerylistr.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.R;

import java.util.ArrayList;


public class IngredientDropdownAdapter extends ArrayAdapter<String> implements Filterable{
    private Context mContext;
    private ArrayList<String> mIngredients;

    public IngredientDropdownAdapter(Context context, int viewResourceId){
        super(context, viewResourceId);
        mContext = context;
        mIngredients = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mIngredients.size();
    }

    @Override
    public String getItem(int position) {
        return mIngredients.get(position);
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

    @Nullable
    @Override
    public Filter getFilter() {

        Filter mFilter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint != null) {
                    String ingredientFilter = constraint.toString();

                    if(ingredientFilter.length() >= 3 ){
                        Log.d("FILTER", "Filter ingredients by " + ingredientFilter);
                        try{
                            mIngredients = new SetIngredientFilter().execute(ingredientFilter).get();
                            mIngredients.add("+ New Ingredient");
                        } catch(Exception e){
                            Log.e("ERROR", "Error Retrieving Filtered Recipes.");
                        }


                    }
                }

                results.values = mIngredients;
                results.count = mIngredients.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                if (results != null && results.count > 0) {
                    for(String ingredient : mIngredients){
                        add(ingredient);
                    }
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
                notifyDataSetChanged();
            }
        };

        return mFilter;
    }

    private class SetIngredientFilter extends AsyncTask<String, Void, ArrayList<String>> {
        private QueryBuilder mQb = new QueryBuilder();

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            return mQb.getIngredientsFilter(params[0]).getStringColumnArray("IngredientName");
        }
    }
}
