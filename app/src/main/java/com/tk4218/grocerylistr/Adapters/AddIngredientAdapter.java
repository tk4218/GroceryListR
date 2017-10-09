package com.tk4218.grocerylistr.Adapters;

import android.content.Context;
import android.os.StrictMode;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.tk4218.grocerylistr.Model.Ingredient;
import com.tk4218.grocerylistr.R;

import java.util.ArrayList;

/**
 * Created by Tk4218 on 9/26/2017.
 */

public class AddIngredientAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<Ingredient> mIngredients;

    public AddIngredientAdapter(Context context, ArrayList<Ingredient> ingredients){
        mContext = context;
        mIngredients = ingredients;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_add_ingredient, null);

            /***********************************************
             * Ingredient Amount
             ***********************************************/
            EditText ingredientAmount = (EditText) convertView.findViewById(R.id.edit_ingredient_amount);

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

            if(mIngredients.get(position).getIngredientMeasurement() != null){
                for(int i = 0; i < measurements.length; i++){
                    if(measurements[i].equals(mIngredients.get(position).getIngredientMeasurement())){
                        ingredientMeasurement.setSelection(i);
                        break;
                    }
                }
            }

            ingredientMeasurement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mIngredients.get(ingredientPosition).setIngredientMeasurement(measurements[position]);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mIngredients.get(ingredientPosition).setIngredientMeasurement("");
                }
            });

            /***********************************************
             * Ingredient Name
             ***********************************************/
            EditText ingredientName = (EditText) convertView.findViewById(R.id.edit_ingredient_name);

            if(mIngredients.get(position).getIngredientName() != null){
                ingredientName.setText(mIngredients.get(position).getIngredientName());
            }
            ingredientName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    mIngredients.get(position).setIngredientName(s.toString());
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

