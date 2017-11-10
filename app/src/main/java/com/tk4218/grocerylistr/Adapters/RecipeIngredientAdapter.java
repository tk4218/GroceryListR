package com.tk4218.grocerylistr.Adapters;

import android.content.Context;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
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

public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Ingredient> mIngredients;

    public RecipeIngredientAdapter(Context context, ArrayList<Ingredient> ingredients){
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mIngredients = ingredients;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.listview_recipe_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.ingredient = mIngredients.get(position);
        holder.ingredientAmount.setText(mIngredients.get(position).getFormattedIngredientAmount() + " " + mIngredients.get(position).getIngredientUnit());
        holder.ingredientName.setText(mIngredients.get(position).getIngredientName());
    }

    @Override
    public int getItemCount() {
        return mIngredients.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        TextView ingredientAmount;
        TextView ingredientName;
        Ingredient ingredient;

        ViewHolder(View itemView) {
            super(itemView);
            ingredientAmount = (TextView) itemView.findViewById(R.id.recipe_ingredient_amount);
            ingredientName = (TextView) itemView.findViewById(R.id.recipe_ingredient_name);
        }

    }
}
