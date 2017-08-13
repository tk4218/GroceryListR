package com.tk4218.grocerylistr;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;

import com.tk4218.grocerylistr.Model.QueryBuilder;

public class EditRecipeActivity extends AppCompatActivity {

    QueryBuilder mQb = new QueryBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_edit_recipe);
        setTitle("Add New Recipe");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            EditText recipeName = (EditText) findViewById(R.id.edit_recipe_name);
            Spinner mealType = (Spinner) findViewById(R.id.edit_meal_type);
            Spinner cuisineType = (Spinner) findViewById(R.id.edit_meal_style);

            mQb.insertRecipe(recipeName.getText().toString(), mealType.getSelectedItem().toString(), cuisineType.getSelectedItem().toString(), "");
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
