package com.tk4218.grocerylistr.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tk4218.grocerylistr.Adapters.GroceryListAdapter;
import com.tk4218.grocerylistr.Adapters.IngredientDropdownAdapter;
import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Model.ApplicationSettings;
import com.tk4218.grocerylistr.Model.GroceryList;
import com.tk4218.grocerylistr.Model.GroceryListItem;
import com.tk4218.grocerylistr.Ingredient;
import com.tk4218.grocerylistr.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroceryListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroceryListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroceryListFragment extends Fragment {
    private ApplicationSettings mSettings;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GROCERY_LIST_KEY = "GroceryListKey";

    private int mGroceryListKey;
    private GroceryList mGroceryList;

    private ExpandableListView mGroceryListView;
    private LinearLayout mAddItem;
    private EditText mAddItemAmount;
    private Spinner mAddItemMeasurement;
    private AutoCompleteTextView mAddItemText;
    private GroceryListAdapter mAdapter;
    private TextView mUndo;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    //private OnFragmentInteractionListener mListener;

    public GroceryListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param groceryListKey Parameter 1.
     * @return A new instance of fragment GroceryListFragment.
     */
    public static GroceryListFragment newInstance(int groceryListKey) {
        GroceryListFragment fragment = new GroceryListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_GROCERY_LIST_KEY, groceryListKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = new ApplicationSettings(getActivity());

        if (getArguments() != null) {
            mGroceryListKey = getArguments().getInt(ARG_GROCERY_LIST_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_grocery_list, container, false);
        mGroceryListView = rootView.findViewById(R.id.list_grocerylist);
        new GetGroceryList().execute();

        mAddItem = rootView.findViewById(R.id.add_item_layout);
        mAddItemAmount = rootView.findViewById(R.id.add_item_amount);
        mAddItemMeasurement = rootView.findViewById(R.id.add_item_measurement);
        mAddItemText = rootView.findViewById(R.id.add_item_name);
        mUndo = rootView.findViewById(R.id.undo);

        mAddItem.setVisibility(View.INVISIBLE);
        mUndo.setVisibility(View.INVISIBLE);

        IngredientDropdownAdapter adapter = new IngredientDropdownAdapter(getContext(), R.layout.dropdown_ingredient);
        mAddItemText.setAdapter(adapter);

        mAddItemText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedIngredient = (String) parent.getAdapter().getItem(position);
                if(selectedIngredient.equals("+ New Ingredient")) {
                    mAddItemText.setText("");
                    showNewIngredientDialog();
                }
            }
        });

        final FloatingActionButton addItem = rootView.findViewById(R.id.add_item);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAddItem.getVisibility() == View.INVISIBLE){
                    mAddItem.setVisibility(View.VISIBLE);
                    mUndo.setVisibility(View.VISIBLE);
                    addItem.setImageResource(android.R.drawable.checkbox_on_background);

                    mAddItemAmount.setText("1");
                    String [] measurements = getResources().getStringArray(R.array.measurements);
                    for(int i = 0; i < measurements.length; i++){
                        if(measurements[i].toLowerCase().equals("count"))
                            mAddItemMeasurement.setSelection(i);
                    }
                    mAddItemText.setText("");
                } else{
                    String addItemName = mAddItemText.getText().toString();
                    double addItemAmount = Double.parseDouble(mAddItemAmount.getText().toString());
                    String addItemUnit = mAddItemMeasurement.getSelectedItem().toString();
                    new AddGroceryListItem().execute(addItemName, addItemAmount, addItemUnit);

                    mAddItem.setVisibility(View.INVISIBLE);
                    mUndo.setVisibility(View.INVISIBLE);
                    addItem.setImageResource(android.R.drawable.ic_input_add);
                }
            }
        });

        mUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUndo.setVisibility(View.INVISIBLE);
                mAddItem.setVisibility(View.INVISIBLE);
                addItem.setImageResource(android.R.drawable.ic_input_add);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void showNewIngredientDialog(){
        @SuppressLint("InflateParams")
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_ingredient, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Add New Ingredient")
                .setIcon(android.R.drawable.ic_input_add)
                .setView(dialogView);

        final EditText newIngredientName  = dialogView.findViewById(R.id.new_ingredient_name);
        final Spinner newIngredientType = dialogView.findViewById(R.id.new_ingredient_type);
        final EditText newIngredientExpAmount = dialogView.findViewById(R.id.new_ingredient_exp_amount);
        final Spinner newIngredientExpInterval = dialogView.findViewById(R.id.new_ingredient_exp_interval);

        newIngredientName.setText("");
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ingredientType = newIngredientType.getSelectedItem().toString();
                String interval = newIngredientExpInterval.getSelectedItem().toString();
                int expiration = Integer.parseInt(newIngredientExpAmount.getText().toString());
                if(interval.equals("Weeks")) expiration *= 7;
                if(interval.equals("Months")) expiration *= 30;
                new AddNewIngredient().execute(newIngredientName.getText().toString(), ingredientType, expiration);
            }
        })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private class GetGroceryList extends AsyncTask<Void, Void, Void> {
        ProgressDialog mDialog;
        private QueryBuilder mQb = new QueryBuilder();

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mDialog = new ProgressDialog(getContext());
            mDialog.setMessage("Getting Grocery List...");
            mDialog.setIndeterminate(false);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONResult currentGroceryList = mQb.getCurrentGroceryList(mSettings.getUser());
            if(currentGroceryList.getCount() != 0){
                mGroceryListKey = currentGroceryList.getInt(("GroceryListKey"));
                mGroceryList = new GroceryList(mGroceryListKey);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if(mGroceryListKey != 0){
                mAdapter = new GroceryListAdapter(getContext(), mGroceryList.getIngredientTypes(), mGroceryList);
                mGroceryListView.setAdapter(mAdapter);
                if(mAdapter.getGroupCount() > 0)
                    mGroceryListView.expandGroup(0);
            }

            //setTitle(dateFormat.format(mGroceryList.getMealPlanDateStart()) + " To " + dateFormat.format(mGroceryList.getMealPlanDateEnd()));
            mDialog.dismiss();
        }
    }

    private class AddNewIngredient extends AsyncTask<Object, Void, String> {
        QueryBuilder mQb = new QueryBuilder();

        @Override
        protected String doInBackground(Object... params) {
            Ingredient ingredient = new Ingredient((String)params[0]);
            if(ingredient.getIngredientKey() == null)
                mQb.insertIngredient((String)params[0], (String)params[1], (int)params[2]);
            return (String)params[0];
        }

        @Override
        protected void onPostExecute(String result) {
            mAddItemText.setText(result);
        }
    }

    private class AddGroceryListItem extends AsyncTask<Object, Void, Void> {
        private QueryBuilder mQb = new QueryBuilder();

        @Override
        protected Void doInBackground(Object... params) {
            String itemName = (String)params[0];
            double itemAmount = (double)params[1];
            String itemUnit = (String)params[2];

            int groceryListItemKey;
            String ingredientKey;

            GroceryListItem existingItem = mGroceryList.findIngredient(itemName, itemUnit);
            if(existingItem != null){
                groceryListItemKey = existingItem.getGroceryListItemKey();
                ingredientKey = existingItem.getIngredient().getIngredientKey();
                double newAmount = existingItem.getIngredientAmount() + itemAmount;
                mQb.updateGroceryListItemAmount(groceryListItemKey, newAmount);
            } else {
                Ingredient ingredient = new Ingredient(itemName);
                ingredientKey = ingredient.getIngredientKey();
                if(ingredientKey == null){
                    //ingredientKey  = mQb.insertIngredient(itemName, "Uncategorized", 0);
                }

                //groceryListItemKey =  mQb.insertGroceryListItem(mGroceryListKey, ingredientKey, itemAmount, itemUnit, false);
            }

            //mAdapter.addGroceryListItem(groceryListItemKey, ingredientKey, itemAmount, itemUnit);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
