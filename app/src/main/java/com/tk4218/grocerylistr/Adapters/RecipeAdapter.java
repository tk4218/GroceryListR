package com.tk4218.grocerylistr.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.CustomLayout.DatePickerFragment;
import com.tk4218.grocerylistr.EditRecipeActivity;
import com.tk4218.grocerylistr.Model.ApplicationSettings;
import com.tk4218.grocerylistr.Model.Recipe;
import com.tk4218.grocerylistr.R;
import com.tk4218.grocerylistr.RecipeActivity;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> implements Filterable{
    private ApplicationSettings mSettings;
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Recipe> mRecipes;
    private ArrayList<Recipe> mFilteredRecipes;

    public RecipeAdapter(Context context, ArrayList<Recipe> recipes){
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mRecipes = recipes;
        mFilteredRecipes = recipes;

        mSettings = new ApplicationSettings(mContext);
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
        holder.recipe = mFilteredRecipes.get(position);
        holder.recipeName.setText(mFilteredRecipes.get(position).getRecipeName());

        if(holder.recipe.isUserRecipe()){
            if(mFilteredRecipes.get(position).getFavorite()){
                holder.saveFavorite.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                holder.saveFavorite.setImageResource(android.R.drawable.btn_star);
            }
            holder.saveFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.recipe.setFavorite(!holder.recipe.getFavorite());

                    QueryBuilder qb = new QueryBuilder();
                    if(qb.updateRecipeFavorite(mSettings.getUser(), holder.recipe.getRecipeKey(), holder.recipe.getFavorite())){
                        if (holder.recipe.getFavorite()){
                            holder.saveFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                        } else {
                            holder.saveFavorite.setImageResource(android.R.drawable.btn_star);
                        }
                    } else{
                        Toast.makeText(RecipeAdapter.this.mContext, "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else{
            holder.saveFavorite.setImageResource(android.R.drawable.ic_input_add);
            holder.saveFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveRecipe(holder.recipe.getRecipeKey(), holder.recipe.getRecipeName(), holder);
                }
            });
        }


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

        holder.recipeOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu recipeMenu = new PopupMenu(mContext, v);
                recipeMenu.inflate(R.menu.menu_recipe_options);
                if(holder.recipe.isUserRecipe()){
                    recipeMenu.getMenu().findItem(R.id.option_save_recipe).setVisible(false);
                    if(holder.recipe.getFavorite()){
                        recipeMenu.getMenu().findItem(R.id.option_favorite).setTitle("Remove Favorite");
                    }else {
                        recipeMenu.getMenu().findItem(R.id.option_favorite).setTitle("Make Favorite");
                    }
                }else {
                    recipeMenu.getMenu().findItem(R.id.option_edit_recipe).setVisible(false);
                    recipeMenu.getMenu().findItem(R.id.option_favorite).setVisible(false);
                    recipeMenu.getMenu().findItem(R.id.option_delete_recipe).setVisible(false);
                }
                recipeMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.option_view_recipe:
                                Intent recipeIntent = new Intent(mContext, RecipeActivity.class);
                                recipeIntent.putExtra("recipeKey", holder.recipe.getRecipeKey());
                                mContext.startActivity(recipeIntent);
                                break;
                            case R.id.option_edit_recipe:
                                Intent editIntent = new Intent(mContext, EditRecipeActivity.class);
                                editIntent.putExtra("recipeKey", holder.recipe.getRecipeKey());
                                mContext.startActivity(editIntent);
                            case R.id.option_favorite:
                                holder.recipe.setFavorite(!holder.recipe.getFavorite());

                                QueryBuilder qb = new QueryBuilder();
                                if(qb.updateRecipeFavorite(mSettings.getUser(), holder.recipe.getRecipeKey(), holder.recipe.getFavorite())){
                                    if (holder.recipe.getFavorite()){
                                        holder.saveFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                                    } else {
                                        holder.saveFavorite.setImageResource(android.R.drawable.btn_star);
                                    }
                                } else{
                                    Toast.makeText(RecipeAdapter.this.mContext, "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
                                }
                            case R.id.option_save_recipe:
                                saveRecipe(holder.recipe.getRecipeKey(), holder.recipe.getRecipeName(), holder);
                            case R.id.option_delete_recipe:
                        }
                        return false;
                    }
                });
                recipeMenu.show();
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
        return mFilteredRecipes.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchString = constraint.toString();
                if (searchString.isEmpty()) {
                    mFilteredRecipes = mRecipes;
                } else {
                    ArrayList<Recipe> filteredList = new ArrayList<>();
                    for (Recipe row : mRecipes) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getRecipeName().toLowerCase().contains(searchString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    mFilteredRecipes = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredRecipes;
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (results != null && results.count > 0) {
                    mFilteredRecipes = (ArrayList<Recipe>) results.values;
                }
                notifyDataSetChanged();
            }
        };
    }

    public void saveRecipe(final int recipeKey, String recipeName, final ViewHolder holder){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Save Recipe")
                .setMessage("Save " + recipeName + " to your recipes?")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new SaveUserRecipe().execute(recipeKey);
                        holder.recipe.setUserRecipe(true);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }).create().show();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {

        TextView recipeName;
        ImageView recipeImage;
        ImageButton recipeOptions;
        ImageButton saveFavorite;
        ImageButton scheduleRecipe;

        ImageView pinterestIcon;
        public Recipe recipe;

        ViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.gridRecipeName);
            recipeImage = itemView.findViewById(R.id.gridRecipeImage);
            recipeOptions = itemView.findViewById(R.id.action_recipe_options);
            saveFavorite = itemView.findViewById(R.id.action_save_favorite);
            scheduleRecipe = itemView.findViewById(R.id.action_schedule_recipe);
            pinterestIcon = itemView.findViewById(R.id.gridPinterest);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent recipeIntent = new Intent(v.getContext(), RecipeActivity.class);
            recipeIntent.putExtra("recipeKey", recipe.getRecipeKey());
            v.getContext().startActivity(recipeIntent);
        }
    }

    private class SaveUserRecipe extends AsyncTask<Integer, Void, Void> {
        private QueryBuilder mQb = new QueryBuilder();

        @Override
        protected Void doInBackground(Integer... params) {
            mQb.insertUserRecipe(mSettings.getUser(), params[0]);
            return null;
        }
    }
}
