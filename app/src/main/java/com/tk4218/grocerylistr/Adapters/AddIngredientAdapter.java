package com.tk4218.grocerylistr.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.StrictMode;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.EditRecipeActivity;
import com.tk4218.grocerylistr.Model.Ingredient;
import com.tk4218.grocerylistr.R;

import java.util.ArrayList;

public class AddIngredientAdapter extends RecyclerView.Adapter<AddIngredientAdapter.ViewHolder>{

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Ingredient> mIngredients;

    public AddIngredientAdapter(Context context, ArrayList<Ingredient> ingredients){
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mIngredients = ingredients;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.listview_add_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mIngredient = mIngredients.get(position);

        /*----------------------------------------------
         * Ingredient Amount
         *----------------------------------------------*/
        if(holder.mIngredient.getIngredientAmount() != 0){
            holder.mIngredientAmount.setText(String.valueOf(holder.mIngredient.getIngredientAmount()));
        }else {
            holder.mIngredientAmount.setText("");
        }

        holder.mIngredientAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")){
                    try{
                        holder.mIngredient.setIngredientAmount(Double.parseDouble(s.toString()));
                    } catch(Exception e){
                        Log.e("ERROR", "Error Setting Ingredient Amount.");
                    }
                }
            }
        });

        /*----------------------------------------------
         * Ingredient Unit
         *----------------------------------------------*/
        final String [] measurements = mContext.getResources().getStringArray(R.array.measurements);
        if(mIngredients.get(position).getIngredientUnit() != null){
            for(int i = 0; i < measurements.length; i++){
                if(measurements[i].equals(mIngredients.get(position).getIngredientUnit())){
                    holder.mIngredientUnit.setSelection(i);
                    break;
                }
            }
        } else{
            holder.mIngredientUnit.setSelection(0);
        }
        holder.mIngredientUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                holder.mIngredient.setIngredientUnit(measurements[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                holder.mIngredient.setIngredientUnit("");
            }
        });

        /*----------------------------------------------
         * Ingredient Name
         *----------------------------------------------*/
        IngredientDropdownAdapter adapter = new IngredientDropdownAdapter(mContext, R.layout.dropdown_ingredient);
        holder.mIngredientName.setAdapter(adapter);

        if(holder.mIngredient.getIngredientName() != null){
            holder.mIngredientName.setText(holder.mIngredient.getIngredientName());
            holder.mIngredientName.setTag(holder.mIngredient.getIngredientKey());
        } else {
            holder.mIngredientName.setText("");
            holder.mIngredientName.setTag(0);
        }

        holder.mIngredientName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                String selectedIngredient = (String) parent.getAdapter().getItem(pos);

                if(selectedIngredient.equals("+ New Ingredient")){
                    showNewIngredientDialog(holder);
                }else {
                    holder.mIngredientName.setText(selectedIngredient);
                    new AddIngredient().execute(holder, false, selectedIngredient);
                }
            }
        });

        holder.mIngredientName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(!s.toString().equals("+ New Ingredient")) {
                    holder.mIngredient.setIngredientName(s.toString());
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("+ New Ingredient")){
                    holder.mIngredientName.setText(holder.mIngredient.getIngredientName());
                } else {
                    holder.mIngredient.setIngredientName(s.toString());
                    new AddIngredient().execute(holder, false, s.toString());
                }

            }
        });

        /*----------------------------------------------
         * Delete Button
         *----------------------------------------------*/
        holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIngredients.remove(holder.mIngredient);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mIngredients.size();
    }

    private void showNewIngredientDialog(final ViewHolder holder){
        @SuppressLint("InflateParams")
        View dialogView = mInflater.inflate(R.layout.dialog_new_ingredient, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("Add New Ingredient")
               .setIcon(android.R.drawable.ic_input_add)
               .setView(dialogView);

        final EditText newIngredientName  = dialogView.findViewById(R.id.new_ingredient_name);
        final Spinner newIngredientType = dialogView.findViewById(R.id.new_ingredient_type);
        final EditText newIngredientExpAmount = dialogView.findViewById(R.id.new_ingredient_exp_amount);
        final Spinner newIngredientExpInterval = dialogView.findViewById(R.id.new_ingredient_exp_interval);

        newIngredientName.setText(holder.mIngredient.getIngredientName());

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ingredientType = newIngredientType.getSelectedItem().toString();
                String interval = newIngredientExpInterval.getSelectedItem().toString();
                int expiration = Integer.parseInt(newIngredientExpAmount.getText().toString());
                if(interval.equals("Weeks")) expiration *= 7;
                if(interval.equals("Months")) expiration *= 30;
                new AddIngredient().execute(holder, true, newIngredientName.getText().toString(), ingredientType, expiration);
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        })
                .show();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        EditText mIngredientAmount;
        Spinner mIngredientUnit;
        AutoCompleteTextView mIngredientName;
        ImageButton mDeleteButton;
        Ingredient mIngredient;

        ViewHolder(View itemView) {
            super(itemView);

            mIngredientAmount = itemView.findViewById(R.id.edit_ingredient_amount);
            mIngredientUnit = itemView.findViewById(R.id.edit_ingredient_measurement);
            mIngredientName = itemView.findViewById(R.id.edit_ingredient_name);
            mDeleteButton = itemView.findViewById(R.id.edit_ingredient_delete);
        }
    }

    private class AddIngredient extends AsyncTask<Object, Void, ViewHolder> {
        private QueryBuilder mQb = new QueryBuilder();

        @Override
        protected ViewHolder doInBackground(Object... params) {
            ViewHolder holder = (ViewHolder)params[0];
            if((boolean)params[1]) {
                int ingredientKey = mQb.insertIngredient((String)params[2], (String)params[3], (int)params[4]);
                holder.mIngredient.setIngredientKey(ingredientKey);
            }else{
                Ingredient ingredient = new Ingredient((String)params[2]);
                holder.mIngredient.setIngredientKey(ingredient.getIngredientKey());
            }

            return holder;
        }

        @Override
        protected void onPostExecute(final ViewHolder result){
            ((EditRecipeActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    result.mIngredientName.setTag(result.mIngredient.getIngredientKey());
                }
            });
        }
    }
}

