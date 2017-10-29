package com.tk4218.grocerylistr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tk4218.grocerylistr.Adapters.MainViewPagerAdapter;
import com.tk4218.grocerylistr.Model.GroceryList;
import com.tk4218.grocerylistr.Model.GroceryListItem;
import com.tk4218.grocerylistr.Model.Ingredient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private MainViewPagerAdapter mMainViewPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private boolean mFromDateSelected;
    private boolean mToDateSelected;
    private Date mMealPlanDateStart;
    private Date mMealPlanDateEnd;
    final SimpleDateFormat weekdayFormat = new SimpleDateFormat("EEEE");
    final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd");
    final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /****************************************************
         * Set up Navigation Drawer
         ****************************************************/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);
        navView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mMainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mMainViewPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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

                Intent intent = new Intent(this, GroceryListActivity.class);
                intent.putExtra("groceryListKey", 1);
                startActivity(intent);
                break;
            case R.id.nav_add_grocerylist:
                createNewGroceryList();
                break;
            case R.id.nav_pantry:
                Toast.makeText(this, "Feature Not Available Yet", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_ingredients:
                Toast.makeText(this, "Feature Not Available Yet", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_grocerylist_history:
                Toast.makeText(this, "Feature Not Available Yet", Toast.LENGTH_SHORT).show();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createNewGroceryList(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_grocerylist_date_picker, null);
        builder.setView(dialogView);

        mFromDateSelected  = true;
        mMealPlanDateStart = new Date();
        LinearLayout fromLayout = (LinearLayout) dialogView.findViewById(R.id.from_date_picker);
        final TextView fromDayOfWeek = (TextView) dialogView.findViewById(R.id.from_dayofweek);
        final TextView fromDate = (TextView) dialogView.findViewById(R.id.from_date);
        final TextView fromYear = (TextView) dialogView.findViewById(R.id.from_year);
        fromDayOfWeek.setText(weekdayFormat.format(mMealPlanDateStart));
        fromDate.setText(dateFormat.format(mMealPlanDateStart));
        fromYear.setText(yearFormat.format(mMealPlanDateStart));

        mToDateSelected = false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 14);
        mMealPlanDateEnd = calendar.getTime();
        LinearLayout toLayout = (LinearLayout) dialogView.findViewById(R.id.to_date_picker);
        final TextView toDayOfWeek = (TextView) dialogView.findViewById(R.id.to_dayofweek);
        final TextView toDate = (TextView) dialogView.findViewById(R.id.to_date);
        final TextView toYear = (TextView) dialogView.findViewById(R.id.to_year);
        toDayOfWeek.setText(weekdayFormat.format(mMealPlanDateEnd));
        toDate.setText(dateFormat.format(mMealPlanDateEnd));
        toYear.setText(yearFormat.format(mMealPlanDateEnd));

        final CalendarView calendarView = (CalendarView) dialogView.findViewById(R.id.calendarView2);


        fromLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFromDateSelected = true;
                mToDateSelected = false;
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
                mToDateSelected = true;
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
                GroceryList newGroceryList = new GroceryList();
                int groceryListKey = newGroceryList.generateGroceryList(mMealPlanDateStart, mMealPlanDateEnd);

                if (groceryListKey == 0){
                    Toast.makeText(getApplicationContext(), "No Meals Planned Between These Dates", Toast.LENGTH_SHORT).show();
                } else{
                    Intent intent = new Intent(getApplicationContext(), GroceryListActivity.class);
                    intent.putExtra("groceryListKey", groceryListKey);
                    startActivity(intent);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        builder.show();
    }
}
