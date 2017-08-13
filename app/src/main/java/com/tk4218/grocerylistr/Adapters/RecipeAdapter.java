package com.tk4218.grocerylistr.Adapters;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tk4218.grocerylistr.Model.QueryBuilder;
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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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

        final ImageButton favorite = (ImageButton) convertView.findViewById(R.id.gridFavorite);
        favorite.setTag(mRecipes.get(position));
        if(mRecipes.get(position).getFavorite()){
            favorite.setImageResource(android.R.drawable.btn_star_big_on);
        }
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recipe recipe = (Recipe) favorite.getTag();
                recipe.setFavorite(!recipe.getFavorite());

                Log.d("UPDATE RECIPE", "Set Key " +recipe.getRecipeKey()+ " to " + recipe.getFavorite());
                QueryBuilder qb = new QueryBuilder();
                if(qb.updateRecipeFavorite(recipe.getRecipeKey(), recipe.getFavorite())){
                    if (recipe.getFavorite()){
                        favorite.setImageResource(android.R.drawable.btn_star_big_on);
                    } else {
                        favorite.setImageResource(android.R.drawable.btn_star);
                    }
                } else{
                    Toast.makeText(RecipeAdapter.this.mContext, "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return convertView;
    }
}
