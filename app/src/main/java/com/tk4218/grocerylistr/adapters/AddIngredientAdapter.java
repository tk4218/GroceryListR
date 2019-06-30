package com.tk4218.grocerylistr.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.databinding.DataBindingUtil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tk4218.grocerylistr.R;
import com.tk4218.grocerylistr.model.Ingredient;
import com.tk4218.grocerylistr.databinding.DialogNewIngredientBinding;
import com.tk4218.grocerylistr.databinding.ListviewAddIngredientBinding;

import java.util.ArrayList;

public class AddIngredientAdapter extends RecyclerView.Adapter<AddIngredientAdapter.ViewHolder>{

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Ingredient> mIngredients;
    private boolean mShowDropDown;

    public AddIngredientAdapter(Context context, ArrayList<Ingredient> ingredients){
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
        holder.mIngredientName.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mShowDropDown && editable.toString().length() >= 3) {
                    Query ingredientList = FirebaseDatabase.getInstance()
                            .getReference().child("ingredient").orderByChild("ingredientName").startAt(editable.toString()).endAt(editable.toString() + "\uf8ff");

                    ingredientList.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<String> ingredients = new ArrayList<>();
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                Ingredient ingredient = item.getValue(Ingredient.class);
                                if (ingredient != null)
                                    ingredients.add(ingredient.getIngredientName());
                            }
                            ingredients.add("+ New Ingredient");
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_dropdown_item_1line, ingredients);
                            holder.mIngredientName.setAdapter(adapter);
                            holder.mIngredientName.showDropDown();
                        }

                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else {
                    holder.mIngredientName.dismissDropDown();
                }
                mShowDropDown = true;
            }
        });

        holder.mIngredientName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                mShowDropDown = false;
                String selectedIngredient = (String) parent.getAdapter().getItem(pos);
                if(selectedIngredient.equals("+ New Ingredient")){
                    holder.mIngredient.setIngredientName("");
                    showNewIngredientDialog(holder);
                }else {
                    holder.mIngredient.setIngredientName(selectedIngredient);
                }
                holder.mIngredientName.dismissDropDown();
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

        /*----------------------------------------------
         * More/Less Buttons
         *----------------------------------------------*/
        holder.mAmountMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mIngredient.setIngredientAmount(holder.mIngredient.getIngredientAmount() + 1);
            }
        });
        holder.mAmountLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.mIngredient.getIngredientAmount() >= 1){
                    holder.mIngredient.setIngredientAmount(holder.mIngredient.getIngredientAmount() - 1);
                } else {
                    holder.mIngredient.setIngredientAmount(0);
                }
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
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        }).show();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        final ListviewAddIngredientBinding binding;
        Spinner mIngredientUnit;
        AutoCompleteTextView mIngredientName;
        ImageButton mDeleteButton;
        Ingredient mIngredient;
        Button mAmountMore;
        Button mAmountLess;

        ViewHolder(final ListviewAddIngredientBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            mIngredientUnit = itemView.findViewById(R.id.edit_ingredient_measurement);
            mIngredientName = itemView.findViewById(R.id.edit_ingredient_name);
            mDeleteButton = itemView.findViewById(R.id.edit_ingredient_delete);
            mAmountMore = itemView.findViewById(R.id.edit_amount_more);
            mAmountLess = itemView.findViewById(R.id.edit_amount_less);
        }
    }
}

