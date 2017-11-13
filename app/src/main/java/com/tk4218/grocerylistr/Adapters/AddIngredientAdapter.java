package com.tk4218.grocerylistr.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.StrictMode;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.EditRecipeActivity;
import com.tk4218.grocerylistr.Model.Ingredient;
import com.tk4218.grocerylistr.R;

import java.util.ArrayList;

public class AddIngredientAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<Ingredient> mIngredients;
    private AutoCompleteTextView mIngredientName;

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
            final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_add_ingredient, null);

            /*----------------------------------------------
             * Ingredient Amount
             *----------------------------------------------*/
            final EditText ingredientAmount = (EditText) convertView.findViewById(R.id.edit_ingredient_amount);

            if(mIngredients.get(position).getIngredientAmount() != 0){
                ingredientAmount.setText(String.valueOf(mIngredients.get(position).getIngredientAmount()));
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
                            Log.e("ERROR", "Error Setting Ingredient Amount.");
                        }
                    }
                }
            });


            /*----------------------------------------------
             * Ingredient Measurement
             *----------------------------------------------*/
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

            /*----------------------------------------------
             * Ingredient Name
             *----------------------------------------------*/
            mIngredientName = (AutoCompleteTextView) convertView.findViewById(R.id.edit_ingredient_name);

            if(mIngredients.get(position).getIngredientName() != null){
                mIngredientName.setText(mIngredients.get(position).getIngredientName());
                mIngredientName.setTag(mIngredients.get(position).getIngredientKey());
            }
            mIngredientName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    mIngredients.get(position).setIngredientName(s.toString());
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ArrayList<String> filterIngredients = new ArrayList<String>();
                    if(s.length() == 3 || (s.length() > 3 && mIngredientName.getAdapter() == null)){
                            new SetIngredientFilter().execute(s.toString());
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                    final double ingredientAmount = mIngredients.get(position).getIngredientAmount();
                    final String ingredientUnit = mIngredients.get(position).getIngredientUnit();
                    if(s.toString().equals("+ New Ingredient")){
                        mIngredientName.setText(mIngredients.get(position).getIngredientName());
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
                                new AddIngredient().execute(position, true, newIngredientName.getText().toString(), ingredientType, expiration, ingredientAmount, ingredientUnit);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        });
                        builder.show();
                    } else {
                        new AddIngredient().execute(position, false, s.toString(), ingredientAmount, ingredientUnit);
                    }

                }
            });

            /*----------------------------------------------
             * Delete Button
             *----------------------------------------------*/
            ImageButton deleteIngredient = (ImageButton) convertView.findViewById(R.id.edit_ingredient_delete);
            deleteIngredient.setTag(position);
        }
        return convertView;
    }

    private class SetIngredientFilter extends AsyncTask<String, Void, ArrayList<String>> {
        private QueryBuilder mQb = new QueryBuilder();

        @Override
        protected ArrayList<String> doInBackground(String... params) {
                return mQb.getIngredientsFilter(params[0]).getStringColumnArray("IngredientName");
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            mIngredientName.setAdapter(new IngredientDropdownAdapter(mContext, R.layout.dropdown_ingredient, result));
            mIngredientName.showDropDown();
        }
    }

    private class AddIngredient extends AsyncTask<Object, Void, Integer> {
        private QueryBuilder mQb = new QueryBuilder();

        @Override
        protected Integer doInBackground(Object... params) {

            if((boolean)params[1]) {
                int ingredientKey = mQb.insertIngredient((String)params[2], (String)params[3], (int)params[4]);
                mIngredients.set((int)params[0], new Ingredient(ingredientKey));
                mIngredients.get((int)params[0]).setIngredientAmount((double)params[5]);
                mIngredients.get((int)params[0]).setIngredientUnit((String)params[6]);
            }else{
                mIngredients.set((int)params[0], new Ingredient((String)params[2]));
                if(mIngredients.get((int)params[0]).getIngredientKey() == 0)
                    mIngredients.get((int)params[0]).setIngredientName((String)params[2]);

                mIngredients.get((int)params[0]).setIngredientAmount((double)params[3]);
                mIngredients.get((int)params[0]).setIngredientUnit((String)params[4]);
            }

            return (int)params[0];
        }

        @Override
        protected void onPostExecute(Integer result){
            final int position  = result;
            ((EditRecipeActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mIngredientName.setTag(mIngredients.get(position).getIngredientKey());

                }
            });
        }
    }
}

