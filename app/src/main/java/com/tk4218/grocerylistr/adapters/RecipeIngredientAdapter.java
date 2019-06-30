package com.tk4218.grocerylistr.adapters;

import android.content.Context;
import android.os.StrictMode;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tk4218.grocerylistr.model.Ingredient;
import com.tk4218.grocerylistr.R;

import java.util.ArrayList;

/*
 * Created by Tk4218 on 10/8/2017.
 */

public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList<Ingredient> mIngredients;

    public RecipeIngredientAdapter(Context context, ArrayList<Ingredient> ingredients){
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
        String ingredientAmountText = mIngredients.get(position).getFormattedIngredientAmount() + " " + mIngredients.get(position).getIngredientUnit();
        holder.ingredientAmount.setText(ingredientAmountText);
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
            ingredientAmount = itemView.findViewById(R.id.recipe_ingredient_amount);
            ingredientName = itemView.findViewById(R.id.recipe_ingredient_name);
        }

    }
}
