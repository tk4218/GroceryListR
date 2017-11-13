package com.tk4218.grocerylistr.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pinterest.android.pdk.PDKClient;
import com.squareup.picasso.Picasso;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.CustomLayout.DatePickerFragment;
import com.tk4218.grocerylistr.EditRecipeActivity;
import com.tk4218.grocerylistr.Model.Recipe;
import com.tk4218.grocerylistr.R;
import com.tk4218.grocerylistr.RecipeActivity;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Recipe> mRecipes;

    public RecipeAdapter(Context context, ArrayList<Recipe> recipes){
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mRecipes = recipes;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.gridview_recipe_, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.recipe = mRecipes.get(position);
        holder.recipeName.setText(mRecipes.get(position).getRecipeName());

        if(mRecipes.get(position).getFavorite()){
            holder.favorite.setImageResource(android.R.drawable.btn_star_big_on);
        }
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.recipe.setFavorite(!holder.recipe.getFavorite());

                Log.d("UPDATE RECIPE", "Set Key " +holder.recipe.getRecipeKey()+ " to " + holder.recipe.getFavorite());
                QueryBuilder qb = new QueryBuilder();
                if(qb.updateRecipeFavorite(holder.recipe.getRecipeKey(), holder.recipe.getFavorite())){
                    if (holder.recipe.getFavorite()){
                        holder.favorite.setImageResource(android.R.drawable.btn_star_big_on);
                    } else {
                        holder.favorite.setImageResource(android.R.drawable.btn_star);
                    }
                } else{
                    Toast.makeText(RecipeAdapter.this.mContext, "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.scheduleRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                Bundle arguments = new Bundle();
                arguments.putInt("recipeKey", holder.recipe.getRecipeKey());
                datePicker.setArguments(arguments);
                datePicker.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "datePicker");
            }
        });

        holder.editRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditRecipeActivity.class);
                intent.putExtra("recipeKey", holder.recipe.getRecipeKey());
                mContext.startActivity(intent);
            }
        });

        if(!holder.recipe.getPinterestId().equals("")){
            holder.pinterestIcon.setImageResource(R.drawable.pinterest_icon_red);
        } else {
            holder.pinterestIcon.setImageResource(android.R.color.transparent);
        }

        if(!holder.recipe.getRecipeImage().equals("")){
            Picasso.with(mContext)
                    .load(holder.recipe.getRecipeImage())
                    .fit()
                    .centerCrop()
                    .into(holder.recipeImage);
        } else {
            holder.recipeImage.setImageResource(R.drawable.recipe_default);
        }
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {

        TextView recipeName;
        ImageView recipeImage;
        ImageButton favorite;
        ImageButton scheduleRecipe;
        ImageButton editRecipe;
        ImageView pinterestIcon;
        public Recipe recipe;

        ViewHolder(View itemView) {
            super(itemView);
            recipeName = (TextView) itemView.findViewById(R.id.gridRecipeName);
            recipeImage = (ImageView) itemView.findViewById(R.id.gridRecipeImage);
            favorite = (ImageButton) itemView.findViewById(R.id.gridFavorite);
            scheduleRecipe = (ImageButton) itemView.findViewById(R.id.gridSchedule);
            editRecipe = (ImageButton) itemView.findViewById(R.id.gridEdit);
            pinterestIcon = (ImageView) itemView.findViewById(R.id.gridPinterest);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent recipeIntent = new Intent(v.getContext(), RecipeActivity.class);
            recipeIntent.putExtra("recipeKey", recipe.getRecipeKey());
            v.getContext().startActivity(recipeIntent);
        }
    }
}
