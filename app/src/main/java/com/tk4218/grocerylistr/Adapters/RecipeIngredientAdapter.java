package com.tk4218.grocerylistr.Adapters;

import android.content.Context;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Model.Ingredient;
import com.tk4218.grocerylistr.R;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Tk4218 on 10/8/2017.
 */

public class RecipeIngredientAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Ingredient> mIngredients;

    public RecipeIngredientAdapter(Context context, ArrayList<Ingredient> ingredients){
        mContext = context;
        mIngredients = ingredients;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public int getCount() {
        return mIngredients.size();
    }

    @Override
    public Ingredient getItem(int position) {
        return mIngredients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_recipe_ingredient, null);

            TextView ingredientAmount = (TextView) convertView.findViewById(R.id.recipe_ingredient_amount);
            TextView ingredientName = (TextView) convertView.findViewById(R.id.recipe_ingredient_name);

            ingredientAmount.setText(mIngredients.get(position).getIngredientAmount() + " " + mIngredients.get(position).getIngredientUnit());
            ingredientName.setText(mIngredients.get(position).getIngredientName());
        }

        return convertView;
    }
}
