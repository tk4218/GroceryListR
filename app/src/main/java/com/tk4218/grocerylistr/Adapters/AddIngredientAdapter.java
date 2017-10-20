package com.tk4218.grocerylistr.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.StrictMode;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Model.Ingredient;
import com.tk4218.grocerylistr.R;

import java.util.ArrayList;

/**
 * Created by Tk4218 on 9/26/2017.
 */

public class AddIngredientAdapter extends BaseAdapter{
    private QueryBuilder mQb = new QueryBuilder();

    private Context mContext;
    private ArrayList<Ingredient> mIngredients;
    private JSONResult mAllIngredients;

    public AddIngredientAdapter(Context context, ArrayList<Ingredient> ingredients){
        mContext = context;
        mIngredients = ingredients;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mAllIngredients = mQb.getAllIngredients();
    }

    @Override
    public int getCount() {
        return mIngredients.size();
    }

    @Override
    public Ingredient getItem(int position) {
        return mIngredients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_add_ingredient, null);

            /***********************************************
             * Ingredient Amount
             ***********************************************/
            final EditText ingredientAmount = (EditText) convertView.findViewById(R.id.edit_ingredient_amount);

            if(mIngredients.get(position).getIngredientAmount() != 0){
                ingredientAmount.setText(mIngredients.get(position).getIngredientAmount() + "");
            }

            ingredientAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    if(!s.toString().equals("")){
                        try{
                            mIngredients.get(position).setIngredientAmount(Double.parseDouble(s.toString()));
                        } catch(Exception e){
                        }
                    }
                }
            });


            /***********************************************
             * Ingredient Measurement
             ***********************************************/
            Spinner ingredientMeasurement = (Spinner) convertView.findViewById(R.id.edit_ingredient_measurement);
            final String [] measurements = mContext.getResources().getStringArray(R.array.measurements);
            final int ingredientPosition = position;

            if(mIngredients.get(position).getIngredientUnit() != null){
                for(int i = 0; i < measurements.length; i++){
                    if(measurements[i].equals(mIngredients.get(position).getIngredientUnit())){
                        ingredientMeasurement.setSelection(i);
                        break;
                    }
                }
            }

            ingredientMeasurement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mIngredients.get(ingredientPosition).setIngredientUnit(measurements[position]);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mIngredients.get(ingredientPosition).setIngredientUnit("");
                }
            });

            /***********************************************
             * Ingredient Name
             ***********************************************/
            final AutoCompleteTextView ingredientName = (AutoCompleteTextView) convertView.findViewById(R.id.edit_ingredient_name);
            ingredientName.setAdapter(new IngredientDropdownAdapter(mContext, R.layout.dropdown_ingredient, mAllIngredients.getStringColumnArray("IngredientName")));

            if(mIngredients.get(position).getIngredientName() != null){
                ingredientName.setText(mIngredients.get(position).getIngredientName());
                ingredientName.setTag(mIngredients.get(position).getIngredientKey());
            }
            ingredientName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    mIngredients.get(position).setIngredientName(s.toString());
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    final double ingredientAmount = mIngredients.get(position).getIngredientAmount();
                    final String ingredientUnit = mIngredients.get(position).getIngredientUnit();
                    if(s.toString().equals("+ New Ingredient")){
                        ingredientName.setText(mIngredients.get(position).getIngredientName());
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Add New Ingredient");
                        builder.setIcon(android.R.drawable.ic_input_add);
                        View dialogView = inflater.inflate(R.layout.dialog_new_ingredient, null);
                        builder.setView(dialogView);
                        final EditText newIngredientName  = (EditText) dialogView.findViewById(R.id.new_ingredient_name);
                        newIngredientName.setText(mIngredients.get(position).getIngredientName());
                        final Spinner newIngredientType = (Spinner) dialogView.findViewById(R.id.new_ingredient_type);
                        final EditText newIngredientExpAmount = (EditText) dialogView.findViewById(R.id.new_ingredient_exp_amount);
                        final Spinner newIngredientExpInterval = (Spinner) dialogView.findViewById(R.id.new_ingredient_exp_interval);
                        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String ingredientType = newIngredientType.getSelectedItem().toString();
                                String interval = newIngredientExpInterval.getSelectedItem().toString();
                                int expiration = Integer.parseInt(newIngredientExpAmount.getText().toString());
                                if(interval.equals("Weeks")) expiration *= 7;
                                if(interval.equals("Months")) expiration *= 30;
                                int ingredientKey = mQb.insertIngredient(newIngredientName.getText().toString(), ingredientType, expiration);
                                mIngredients.set(position, new Ingredient(ingredientKey));
                                mIngredients.get(position).setIngredientAmount(ingredientAmount);
                                mIngredients.get(position).setIngredientUnit(ingredientUnit);
                                ingredientName.setTag(mIngredients.get(position).getIngredientKey());
                                mAllIngredients = mQb.getAllIngredients();
                                ingredientName.setAdapter(new IngredientDropdownAdapter(mContext, R.layout.dropdown_ingredient, mAllIngredients.getStringColumnArray("IngredientName")));
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        });
                        builder.show();
                    } else {
                        mIngredients.set(position, new Ingredient(s.toString()));
                        mIngredients.get(position).setIngredientAmount(ingredientAmount);
                        mIngredients.get(position).setIngredientUnit(ingredientUnit);
                        ingredientName.setTag(mIngredients.get(position).getIngredientKey());
                    }

                }
            });

            /***********************************************
             * Delete Button
             ***********************************************/
            ImageButton deleteIngredient = (ImageButton) convertView.findViewById(R.id.edit_ingredient_delete);
            deleteIngredient.setTag(position);
        }
        return convertView;
    }
}

