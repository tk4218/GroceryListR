package com.tk4218.grocerylistr.Database;

import org.json.JSONArray;
import org.json.JSONObject;

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

    public int getInt(String columnName){
        try{
            return result.getJSONObject(index).getInt(columnName);
        } catch(Exception e){
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

}
