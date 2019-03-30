package com.tk4218.grocerylistr;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.tk4218.grocerylistr.Adapters.IngredientDropdownAdapter;
import com.tk4218.grocerylistr.databinding.DialogNewIngredientBinding;
import com.tk4218.grocerylistr.databinding.ListviewAddIngredientBinding;

import java.util.ArrayList;

public class AddIngredientAdapter extends RecyclerView.Adapter<AddIngredientAdapter.ViewHolder>{

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Ingredient> mIngredients;

    AddIngredientAdapter(Context context, ArrayList<Ingredient> ingredients){
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mIngredients = ingredients;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        ListviewAddIngredientBinding binding = DataBindingUtil.inflate(mInflater, R.layout.listview_add_ingredient, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
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
        DialogNewIngredientBinding binding = DataBindingUtil.inflate(mInflater, R.layout.dialog_new_ingredient, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        binding.setIngredient(holder.mIngredient);

        builder.setTitle("Add New Ingredient")
               .setIcon(android.R.drawable.ic_input_add)
               .setView(binding.getRoot());

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                holder.mIngredient.save();
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
}

