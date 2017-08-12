package com.tk4218.grocerylistr.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tk4218.grocerylistr.Model.Recipe;
import com.tk4218.grocerylistr.R;

import java.util.ArrayList;

/**
 * Created by Tk4218 on 8/11/2017.
 */

public class RecipeAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Recipe> mRecipes;

    public RecipeAdapter(Context context, ArrayList<Recipe> recipes){
        mContext = context;
        mRecipes = recipes;
    }

    @Override
    public int getCount() {
        return mRecipes.size();
    }

    @Override
    public Object getItem(int position) {
        return mRecipes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gridview_recipe_, null);
        }

        TextView recipeName = (TextView) convertView.findViewById(R.id.gridRecipeName);
        recipeName.setText(mRecipes.get(position).getRecipeName());
        return convertView;
    }
}
