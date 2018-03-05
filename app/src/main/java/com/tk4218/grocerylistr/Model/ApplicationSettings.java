package com.tk4218.grocerylistr.Model;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by taylo on 3/2/2018.
 */

public class ApplicationSettings {
    private SharedPreferences mSp;

    public ApplicationSettings(Context context){
        if(context != null) {
            mSp = context.getSharedPreferences("application_settings", MODE_PRIVATE);
        }
    }

    public boolean isLoggedIn() {return mSp.getBoolean("LoggedIn", false); }
    public String getUser(){ return mSp.getString("Username", ""); }
    public String getUserFirstName(){ return mSp.getString("UserFirstName", ""); }
    public String getUserLastName() { return mSp.getString("UserLastName", ""); }
    public boolean isPinterestLoggedIn() { return mSp.getBoolean("PinterestLoggedIn", false); }

    public void logout(){
        mSp.edit().putBoolean("LoggedIn", false).apply();
        mSp.edit().putString("Username", "").apply();
        mSp.edit().putString("UserFirstName", "").apply();
        mSp.edit().putString("UserLastName", "").apply();
        pinterestLogout();
    }

    public void login(String username){
        mSp.edit().putBoolean("LoggedIn", true).apply();
        mSp.edit().putString("Username", username).apply();
    }

    public void pinterestLogin(){
        mSp.edit().putBoolean("PinterestLoggedIn", true).apply();
    }

    public void pinterestLogout(){
        mSp.edit().putBoolean("PinterestLoggedIn", false).apply();
    }
}
