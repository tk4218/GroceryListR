package com.tk4218.grocerylistr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tk4218.grocerylistr.Adapters.RecipeIngredientAdapter;
import com.tk4218.grocerylistr.CustomLayout.DatePickerFragment;

import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Model.Recipe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecipeActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapseToolbar;
    private RecyclerView mRecipeIngredientList;
    private TextView mRecipeLastMade;
    private ImageView mRecipeImage;
    private TextView mRecipeName;

    private int mRecipeKey;
    private Recipe mRecipe;
    private Date mLastMade;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        /*------------------------------------------------
         * Populate recipe details
         *------------------------------------------------*/
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mCollapseToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mRecipeIngredientList = (RecyclerView) findViewById(R.id.recipe_ingredient_list);
        mRecipeIngredientList.setLayoutManager(new LinearLayoutManager(this));
        mRecipeLastMade = (TextView) findViewById(R.id.recipe_last_made);
        mRecipeImage = (ImageView) findViewById(R.id.recipe_image);
        mRecipeName = (TextView) findViewById(R.id.recipe_name_title);
        Bundle extras = getIntent().getExtras();
        setSupportActionBar(mToolbar);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(final AppBarLayout appBarLayout, int verticalOffset) {
                //Initialize the size of the scroll
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                //Check if the view is collapsed
                if (scrollRange + verticalOffset == 0) {
                    mToolbar.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                    if(mRecipe != null){
                        mCollapseToolbar.setTitle(mRecipe.getRecipeName());
                    }
                }else{
                    mToolbar.setBackgroundColor(ContextCompat.getColor(getBaseContext(), android.R.color.transparent));
                    mCollapseToolbar.setTitle("");
                }
            }
        });
        if(extras != null){
            mRecipeKey = extras.getInt("recipeKey");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                Bundle arguments = new Bundle();
                arguments.putInt("recipeKey", mRecipeKey);
                datePicker.setArguments(arguments);
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetRecipe().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            Intent intent = new Intent(this, EditRecipeActivity.class);
            intent.putExtra("recipeKey", mRecipeKey);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*--------------------------------------
     * Async Tasks
     *--------------------------------------*/
    private class GetRecipe extends AsyncTask<Void, Void, Void> {
        QueryBuilder mQb = new QueryBuilder();
        ProgressDialog mDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mDialog = new ProgressDialog(RecipeActivity.this);
            mDialog.setMessage("Getting Recipe...");
            mDialog.setIndeterminate(false);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mRecipe = new Recipe(mRecipeKey);
            JSONResult recipeLastMade = mQb.getRecipeLastMade(mRecipeKey);
            mLastMade = recipeLastMade.getDate("LastMade");

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRecipeName.setText(mRecipe.getRecipeName());
                    mRecipeIngredientList.setAdapter(new RecipeIngredientAdapter(RecipeActivity.this, mRecipe.getIngredients()));


                    if(mLastMade.getTime() == 0){
                        mRecipeLastMade.setText("You have not made this recipe yet");
                    }else{
                        if(mLastMade.getTime() < new Date().getTime()){
                            mRecipeLastMade.setText("Last made on " + dateFormat.format(mLastMade));
                        } else{
                            mRecipeLastMade.setText("Scheduled on " + dateFormat.format(mLastMade));
                        }
                    }
                    if(!mRecipe.getRecipeImage().equals("")){
                        Picasso.with(RecipeActivity.this)
                                .load(mRecipe.getRecipeImage())
                                .fit()
                                .centerCrop()
                                .into(mRecipeImage);
                    }

                }
            });
            mDialog.dismiss();
        }
    }
}
