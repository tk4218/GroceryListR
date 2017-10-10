package com.tk4218.grocerylistr.Database;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tk4218 on 8/12/2017.
 */

public class JSONResult {

    private JSONArray result;
    private int index = 0;

    public JSONResult(JSONArray array){
        result = array;
        index = 0;
    }

    public int getCount(){
        return result.length();
    }

    public void moveFirst(){
        index = 0;
    }

    public void moveNext(){
        index++;
    }

    public void moveToPosition(int position){
        index = position;
    }

    public int getInt(String columnName){
        try{
            return result.getJSONObject(index).getInt(columnName);
        } catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public int getInt(int position, String columnName) {
        try {
            return result.getJSONObject(position).getInt(columnName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getDouble(String columnName){
        try{
            return result.getJSONObject(index).getDouble(columnName);
        } catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public double getDouble(int position, String columnName){
        try{
            return result.getJSONObject(position).getDouble(columnName);
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public String getString(String columnName){
        try{
            return result.getJSONObject(index).getString(columnName);
        } catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public String getString(int position, String columnName){
        try{
            return result.getJSONObject(position).getString(columnName);
        } catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public boolean getBoolean(String columnName){
        try{
            return result.getJSONObject(index).getBoolean(columnName);
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean getBoolean(int position, String columnName){
        try{
            return result.getJSONObject(position).getBoolean(columnName);
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public Date getDate(String columnName){
        try{
            String date = result.getJSONObject(index).getString(columnName);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.parse(date);
        } catch (Exception e){
            e.printStackTrace();
        }
        return new Date();
    }

    public Date getDate(int position, String columnName){
        try{
            String date = result.getJSONObject(position).getString(columnName);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.parse(date);
        } catch (Exception e){
            e.printStackTrace();
        }
        return new Date();
    }

    public JSONObject getRow(int position) {
        try {
            return result.getJSONObject(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
