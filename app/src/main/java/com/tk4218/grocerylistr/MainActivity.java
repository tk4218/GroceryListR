package com.tk4218.grocerylistr;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.amazonaws.mobile.client.AWSMobileClient;

import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.fragments.CalendarFragment;
import com.tk4218.grocerylistr.fragments.GroceryListFragment;
import com.tk4218.grocerylistr.fragments.RecipeFragment;
import com.tk4218.grocerylistr.model.ApplicationSettings;
import com.tk4218.grocerylistr.model.GroceryList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.tk4218.grocerylistr.model.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GroceryList.GroceryListCreatedCallback {

    private ApplicationSettings mSettings;

    private boolean mShowUserRecipes;
    private boolean mShowFavorites;

    private TextView mNavUsername;
    private FloatingActionButton mFab;

    private SearchView mSearchView;

    private boolean mFromDateSelected;
    private Date mMealPlanDateStart;
    private Date mMealPlanDateEnd;

    final SimpleDateFormat weekdayFormat = new SimpleDateFormat("EEEE" , Locale.getDefault());
    final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd", Locale.getDefault());
    final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

    private static String PINTEREST_APP_ID = "4932556460807699958";
    private PDKClient mPDKClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShowUserRecipes = true;
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        final AppBarLayout appBar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        AWSMobileClient.getInstance().initialize(this).execute();

        mPDKClient = PDKClient.configureInstance(this, PINTEREST_APP_ID);
        mPDKClient.onConnect(this);

        mSettings = new ApplicationSettings(this);

        /*---------------------------------------------------
         * Set up Navigation Drawer
         *---------------------------------------------------*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);
        navView.setNavigationItemSelectedListener(this);
        final Menu navigationMenu = navView.getMenu();
        navigationMenu.findItem(R.id.nav_pantry).setVisible(false);
        mPDKClient.getMe("id,image,counts,created_at,first_name,last_name,bio,username", new PDKCallback(){
            @Override
            public void onSuccess(PDKResponse response) {
                navigationMenu.findItem(R.id.nav_pinterest_login).setVisible(false);
                mSettings.pinterestLogin();
            }
            @Override
            public void onFailure(PDKException exception) {
                navigationMenu.findItem(R.id.nav_pinterest_login).setVisible(true);
                mSettings.pinterestLogout();
            }
        });

        User user = User.Companion.currentUser(this);
        View header = navView.getHeaderView(0);
        mNavUsername = header.findViewById(R.id.navigation_username);
        mNavUsername.setText(user.getFirstName() + " " + user.getLastName());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        replaceFragment(CalendarFragment.Companion.newInstance());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_calendar:
                        mFab.hide();
                        replaceFragment(CalendarFragment.Companion.newInstance());
                        break;
                    case R.id.action_recipes:
                        mFab.show();
                        replaceFragment(RecipeFragment.Companion.newInstance());
                        break;
                    case R.id.action_grocerylist:
                        mFab.hide();
                        replaceFragment(GroceryListFragment.Companion.newInstance(""));
                        break;
                }
                appBar.setExpanded(true, true);
                return true;
            }
        });


        /*-----------------------------------
         *Set floating action button action
         *-----------------------------------*/
        mFab= findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditRecipeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mSearchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
             //   if(mViewPager.getCurrentItem() != 1){
              //      mViewPager.setCurrentItem(1);
               // }

               // RecipeFragment viewPagerFragment = (RecipeFragment) mViewPager.getAdapter().instantiateItem(mViewPager, 1);
               // viewPagerFragment.filterRecipes(newText);
                return false;
            }
        });

        MenuItem recipeToggle = menu.findItem(R.id.action_toggle_recipes);
        if(!mShowUserRecipes){
            recipeToggle.setTitle("My Recipes");
        } else{
            recipeToggle.setTitle("Explore Recipes");
        }

        MenuItem favoriteToggle = menu.findItem(R.id.action_my_favorites);
        if(mShowFavorites){
            favoriteToggle.setTitle("My Recipes");
        } else{
            favoriteToggle.setTitle("My Favorites");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //RecipeFragment viewPagerFragment = (RecipeFragment) mViewPager.getAdapter().instantiateItem(mViewPager, 1);

        //if(mViewPager.getCurrentItem() != 1){
         //   mViewPager.setCurrentItem(1);
        //}

        switch (id){
            case R.id.action_toggle_recipes:
                mShowFavorites = false;
                mShowUserRecipes = !mShowUserRecipes;
                //viewPagerFragment.toggleRecipeList(mShowUserRecipes, "", false);
                invalidateOptionsMenu();
                return true;
            case R.id.app_bar_search:
                return true;
            case R.id.action_my_favorites:
                mShowUserRecipes = true;
                mShowFavorites = !mShowFavorites;
                //viewPagerFragment.toggleRecipeList(mShowUserRecipes, "", mShowFavorites);
                invalidateOptionsMenu();
                return true;
            case R.id.action_sort_rating:
                //viewPagerFragment.toggleRecipeList(mShowUserRecipes, "order by Rating desc", mShowFavorites);
                return true;
            case R.id.action_sort_name_asc:
                //viewPagerFragment.toggleRecipeList(mShowUserRecipes, "order by RecipeName", mShowFavorites);
                return true;
            case R.id.action_sort_name_desc:
               // viewPagerFragment.toggleRecipeList(mShowUserRecipes, "order by RecipeName desc", mShowFavorites);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id){
            case R.id.nav_grocerylist:
                replaceFragment(GroceryListFragment.Companion.newInstance(""));
                break;
            case R.id.nav_add_grocerylist:
                createNewGroceryList();
                break;
            case R.id.nav_pantry:
                Toast.makeText(this, "Feature Not Available Yet", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_ingredients:
                Intent ingredientListIntent = new Intent(this, IngredientListActivity.class);
                startActivity(ingredientListIntent);
                break;
            case R.id.nav_grocerylist_history:
                Intent groceryListHistoryIntent = new Intent(this, GroceryListHistoryActivity.class);
                startActivity(groceryListHistoryIntent);
                break;
            case R.id.nav_pinterest_login:
                pinterestLogin();
                break;
            case R.id.nav_logout:
                if(mSettings.isPinterestLoggedIn()){
                    mPDKClient.logout();
                }
                User.Companion.logout(this);
                AccessToken.setCurrentAccessToken(null);
                Intent intent = new Intent(this, LoginActivity.class);
                finish();
                startActivity(intent);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);

        transaction.commit();
    }

    private void createNewGroceryList(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_grocerylist_date_picker, null);
        builder.setView(dialogView);

        mFromDateSelected  = true;
        mMealPlanDateStart = new Date();
        LinearLayout fromLayout = dialogView.findViewById(R.id.from_date_picker);
        final TextView fromDayOfWeek = dialogView.findViewById(R.id.from_dayofweek);
        final TextView fromDate = dialogView.findViewById(R.id.from_date);
        final TextView fromYear = dialogView.findViewById(R.id.from_year);
        fromDayOfWeek.setText(weekdayFormat.format(mMealPlanDateStart));
        fromDate.setText(dateFormat.format(mMealPlanDateStart));
        fromYear.setText(yearFormat.format(mMealPlanDateStart));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 14);
        mMealPlanDateEnd = calendar.getTime();
        LinearLayout toLayout = dialogView.findViewById(R.id.to_date_picker);
        final TextView toDayOfWeek = dialogView.findViewById(R.id.to_dayofweek);
        final TextView toDate = dialogView.findViewById(R.id.to_date);
        final TextView toYear = dialogView.findViewById(R.id.to_year);
        toDayOfWeek.setText(weekdayFormat.format(mMealPlanDateEnd));
        toDate.setText(dateFormat.format(mMealPlanDateEnd));
        toYear.setText(yearFormat.format(mMealPlanDateEnd));

        final CalendarView calendarView = dialogView.findViewById(R.id.calendarView2);


        fromLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFromDateSelected = true;
                fromDayOfWeek.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.recipeGridBackground));
                fromDate.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.recipeGridBackground));
                fromYear.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.recipeGridBackground));
                toDayOfWeek.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
                toDate.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
                toYear.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
                calendarView.setDate(mMealPlanDateStart.getTime());
                calendarView.setMinDate(0);
            }
        });


        toLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFromDateSelected = false;
                fromDayOfWeek.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
                fromDate.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
                fromYear.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
                toDayOfWeek.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.recipeGridBackground));
                toDate.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.recipeGridBackground));
                toYear.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.recipeGridBackground));
                calendarView.setDate(mMealPlanDateEnd.getTime());
                calendarView.setMinDate(mMealPlanDateStart.getTime());
            }
        });


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);

                if(mFromDateSelected){
                    mMealPlanDateStart = calendar.getTime();
                    fromDayOfWeek.setText(weekdayFormat.format(mMealPlanDateStart));
                    fromDate.setText(dateFormat.format(mMealPlanDateStart));
                    fromYear.setText(yearFormat.format(mMealPlanDateStart));
                }else{
                    mMealPlanDateEnd = calendar.getTime();
                    toDayOfWeek.setText(weekdayFormat.format(mMealPlanDateEnd));
                    toDate.setText(dateFormat.format(mMealPlanDateEnd));
                    toYear.setText(yearFormat.format(mMealPlanDateEnd));
                }
            }
        });

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GroceryList.Companion.generateGroceryList(getApplicationContext(), mMealPlanDateStart, mMealPlanDateEnd, false, MainActivity.this);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.setNeutralButton("Create Empty List", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GroceryList.Companion.generateGroceryList(getApplicationContext(), mMealPlanDateStart, mMealPlanDateEnd, true, MainActivity.this);
            }
        });
        builder.show();
    }

    /*------------------------------
     * Pinterest Login
     *------------------------------*/
    private void pinterestLogin(){
        ArrayList<String> scopes = new ArrayList<>();
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);

        mPDKClient.login(this, scopes, new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {
                Log.d(getClass().getName(), response.getData().toString());
                //user logged in, use response.getUser() to get PDKUser object
            }

            @Override
            public void onFailure(PDKException exception) {
                Log.e(getClass().getName(), exception.getDetailMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPDKClient.onOauthResponse(requestCode, resultCode, data);
        mPDKClient.getMe("id,image,counts,created_at,first_name,last_name,bio,username", new PDKCallback(){
            @Override
            public void onSuccess(PDKResponse response) {
                Toast.makeText(MainActivity.this, "Welcome, " + response.getUser().getUsername(), Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
            }

            @Override
            public void onFailure(PDKException exception) {
                exception.printStackTrace();
                Toast.makeText(MainActivity.this, "Failed to Login!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void onGroceryListCreated(String groceryListKey) {
        replaceFragment(GroceryListFragment.Companion.newInstance(groceryListKey));
    }
}
