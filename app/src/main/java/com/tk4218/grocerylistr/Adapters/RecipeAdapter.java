package com.tk4218.grocerylistr.Adapters;

import android.content.Context;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tk4218.grocerylistr.Model.QueryBuilder;
import com.tk4218.grocerylistr.Model.Recipe;
import com.tk4218.grocerylistr.R;

import java.util.ArrayList;

/**
 * Created by Tk4218 on 8/11/2017.
 */

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
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.recipeName.setText(mRecipes.get(position).getRecipeName());

        holder.favorite.setTag(mRecipes.get(position));
        if(mRecipes.get(position).getFavorite()){
            holder.favorite.setImageResource(android.R.drawable.btn_star_big_on);
        }
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recipe recipe = (Recipe) holder.favorite.getTag();
                recipe.setFavorite(!recipe.getFavorite());

                Log.d("UPDATE RECIPE", "Set Key " +recipe.getRecipeKey()+ " to " + recipe.getFavorite());
                QueryBuilder qb = new QueryBuilder();
                if(qb.updateRecipeFavorite(recipe.getRecipeKey(), recipe.getFavorite())){
                    if (recipe.getFavorite()){
                        holder.favorite.setImageResource(android.R.drawable.btn_star_big_on);
                    } else {
                        holder.favorite.setImageResource(android.R.drawable.btn_star);
                    }
                } else{
                    Toast.makeText(RecipeAdapter.this.mContext, "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    public Recipe getItem(int position) {
        return mRecipes.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {

        public TextView recipeName;
        public ImageView recipeImage;
        public ImageButton favorite;

        public ViewHolder(View itemView) {
            super(itemView);
            recipeName = (TextView) itemView.findViewById(R.id.gridRecipeName);
            recipeImage = (ImageView) itemView.findViewById(R.id.gridRecipeImage);
            favorite = (ImageButton) itemView.findViewById(R.id.gridFavorite);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }
}
