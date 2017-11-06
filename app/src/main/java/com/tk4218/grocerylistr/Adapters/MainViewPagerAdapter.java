package com.tk4218.grocerylistr.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.tk4218.grocerylistr.Fragments.CalendarFragment;
import com.tk4218.grocerylistr.Fragments.RecipeFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class MainViewPagerAdapter extends FragmentPagerAdapter {

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Log.d("Tab Number", position+"");
        switch(position){
            case 0:
                return CalendarFragment.newInstance();
            case 1:
                return RecipeFragment.newInstance();
        }
        return RecipeFragment.newInstance();
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Calendar";
            case 1:
                return "Recipes";
        }
        return null;
    }
}