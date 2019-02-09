package com.tk4218.grocerylistr.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Ingredient;
import com.tk4218.grocerylistr.R;

import java.util.ArrayList;

/*
 * Created by Tk4218 on 11/28/2017.
 */

public class IngredientListAdapter extends RecyclerView.Adapter<IngredientListAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Ingredient> mIngredients;

    public IngredientListAdapter(Context context, ArrayList<Ingredient> ingredients){
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mIngredients = ingredients;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.listview_ingredient_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.ingredient = mIngredients.get(position);
        holder.ingredientName.setText(mIngredients.get(position).getIngredientName());
        holder.ingredientType.setText(mIngredients.get(position).getIngredientType());

        holder.editIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditIngredientDialog(holder.ingredient, holder.getAdapterPosition());
            }
        });
    }

    private void showEditIngredientDialog(Ingredient ingredient, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Edit Ingredient");

        View dialogView = mInflater.inflate(R.layout.dialog_new_ingredient, null);
        builder.setView(dialogView);

        final EditText editIngredientName  = dialogView.findViewById(R.id.new_ingredient_name);
        editIngredientName.setText(ingredient.getIngredientName());

        final Spinner editIngredientType = dialogView.findViewById(R.id.new_ingredient_type);
        final String [] ingredientTypes = mContext.getResources().getStringArray(R.array.ingredient_type);
        for(int i = 0; i < ingredientTypes.length; i++){
            if(ingredientTypes[i].equals(ingredient.getIngredientType())){
                editIngredientType.setSelection(i);
                break;
            }
        }

        final EditText editIngredientExpAmount = dialogView.findViewById(R.id.new_ingredient_exp_amount);
        final Spinner editIngredientExpInterval =  dialogView.findViewById(R.id.new_ingredient_exp_interval);
        String shelfLifeText;
        if(ingredient.getShelfLife() % 30 == 0){
            shelfLifeText = ingredient.getShelfLife()/30 + "";
            editIngredientExpInterval.setSelection(2);
        } else if(ingredient.getShelfLife() % 7 == 0){
            shelfLifeText = ingredient.getShelfLife()/7 + "";
            editIngredientExpInterval.setSelection(1);
        } else {
            shelfLifeText = ingredient.getShelfLife() + "";
            editIngredientExpInterval.setSelection(0);
        }
        editIngredientExpAmount.setText(shelfLifeText);

        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ingredientType = editIngredientType.getSelectedItem().toString();
                String interval = editIngredientExpInterval.getSelectedItem().toString();
                int expiration = Integer.parseInt(editIngredientExpAmount.getText().toString());

                if(interval.equals("Weeks")) expiration *= 7;
                if(interval.equals("Months")) expiration *= 30;

                new EditIngredient().execute(position, editIngredientName.getText().toString(), ingredientType, expiration, mIngredients.get(position).getIngredientKey());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        builder.show();
    }

    @Override
    public int getItemCount() {
        return mIngredients.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView ingredientName;
        TextView ingredientType;
        ImageView ingredientTypeImage;
        ImageButton editIngredient;
        Ingredient ingredient;

        ViewHolder(View itemView){
            super(itemView);
            ingredientName = itemView.findViewById(R.id.list_ingredient_name);
            ingredientType = itemView.findViewById(R.id.list_ingredient_type);
            ingredientTypeImage = itemView.findViewById(R.id.list_ingredient_type_image);
            editIngredient = itemView.findViewById(R.id.list_edit_ingredient);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class EditIngredient extends AsyncTask<Object, Void, Void> {
        ProgressDialog mDialog;
        QueryBuilder mQb = new QueryBuilder();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("Editing Ingredient...");
            mDialog.setIndeterminate(false);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.show();
        }

        @Override
        protected Void doInBackground(Object... params) {
            int position = (int)params[0];
            String ingredientName = (String)params[1];
            String ingredientType = (String)params[2];
            int expiration = (int)params[3];
            int ingredientKey = (int)params[4];

            mQb.editIngredient(ingredientName, ingredientType, expiration, ingredientKey);
            mIngredients.get(position).setIngredientName(ingredientName);
            mIngredients.get(position).setIngredientType(ingredientType);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            notifyDataSetChanged();
            mDialog.dismiss();
        }
    }

}
