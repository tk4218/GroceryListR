package com.tk4218.grocerylistr;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.StrictMode;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.tk4218.grocerylistr.Adapters.IngredientDropdownAdapter;
import com.tk4218.grocerylistr.databinding.ListviewAddIngredientBinding;

import java.util.ArrayList;

public class AddIngredientAdapter extends RecyclerView.Adapter<AddIngredientAdapter.ViewHolder>{

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Ingredient> mIngredients;

    public AddIngredientAdapter(Context context, ArrayList<Ingredient> ingredients){
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mIngredients = ingredients;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        ListviewAddIngredientBinding binding = DataBindingUtil.inflate(mInflater, R.layout.listview_add_ingredient, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.binding.setIngredient(mIngredients.get(position));
        holder.mIngredient = mIngredients.get(position);

        /*----------------------------------------------
         * Ingredient Name
         *----------------------------------------------*/
        IngredientDropdownAdapter adapter = new IngredientDropdownAdapter(mContext, R.layout.dropdown_ingredient);
        holder.mIngredientName.setAdapter(adapter);

        holder.mIngredientName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                String selectedIngredient = (String) parent.getAdapter().getItem(pos);

                if(selectedIngredient.equals("+ New Ingredient")){
                    showNewIngredientDialog(holder);
                }else {
                    holder.mIngredient.setIngredientName(selectedIngredient);
                    //new AddIngredient().execute(holder, false, selectedIngredient);
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
                    //new AddIngredient().execute(holder, false, s.toString());
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
                //new AddIngredient().execute(holder, true, newIngredientName.getText().toString(), ingredientType, expiration);
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        })
                .show();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        final ListviewAddIngredientBinding binding;
        Spinner mIngredientUnit;
        AutoCompleteTextView mIngredientName;
        ImageButton mDeleteButton;
        Ingredient mIngredient;

        ViewHolder(final ListviewAddIngredientBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            mIngredientUnit = itemView.findViewById(R.id.edit_ingredient_measurement);
            mIngredientName = itemView.findViewById(R.id.edit_ingredient_name);
            mDeleteButton = itemView.findViewById(R.id.edit_ingredient_delete);
        }
    }

    public class EventHandlers {
        public void onIngredientDeleted(View view, Ingredient ingredient){
            mIngredients.remove(ingredient);
            notifyDataSetChanged();
        }
    }
}

