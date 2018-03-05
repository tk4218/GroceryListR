package com.tk4218.grocerylistr.Database;

import android.annotation.TargetApi;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Tk4218 on 8/12/2017.
 */

public class JSONResult {
    public static final int SORT_ASCENDING = 0;
    public static final int SORT_DESCENDING = 1;

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

    public boolean moveNext(){
        index++;
        return index < getCount();
    }

    public void moveToPosition(int position){
        index = position;
    }

    public int getPosition(){ return index; }

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

    public void putInt(String columnName, int value){
        try {
            result.getJSONObject(index).put(columnName, value);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void putInt(int position, String columnName, int value){
        try {
            result.getJSONObject(position).put(columnName, value);
        } catch (JSONException e) { e.printStackTrace(); }
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

    public void putDouble(String columnName, double value){
        try {
            result.getJSONObject(index).put(columnName, value);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void putDouble(int position, String columnName, double value){
        try {
            result.getJSONObject(position).put(columnName, value);
        } catch (JSONException e) { e.printStackTrace(); }
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

    public void putString(String columnName, String value){
        try {
            result.getJSONObject(index).put(columnName, value);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void putString(int position, String columnName, String value){
        try {
            result.getJSONObject(position).put(columnName, value);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public boolean getBoolean(String columnName){
        try{
            return result.getJSONObject(index).getInt(columnName) == 1;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean getBoolean(int position, String columnName){
        try{
            return result.getJSONObject(position).getInt(columnName) == 1;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void putBoolean(String columnName, boolean value){
        try {
            result.getJSONObject(index).put(columnName, value);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void putBoolean(int position, String columnName, boolean value){
        try {
            result.getJSONObject(position).put(columnName, value);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public Date getDate(String columnName){
        try{
            String date = result.getJSONObject(index).getString(columnName);
            if(date.equals("0000-00-00 00:00:00") || date.equals("null")) return new Date(0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(date);
        } catch (Exception e){
            e.printStackTrace();
        }
        return new Date();
    }

    public Date getDate(int position, String columnName){
        try{
            String date = result.getJSONObject(position).getString(columnName);
            if(date.equals("0000-00-00 00:00:00") || date.equals("null")) return new Date(0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(date);
        } catch (Exception e){
            e.printStackTrace();
        }
        return new Date();
    }

    public void putDate(String columnName, Date value){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            result.getJSONObject(index).put(columnName, dateFormat.format(value));
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void putDate(int position, String columnName, Date value){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            result.getJSONObject(position).put(columnName, dateFormat.format(value));
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public JSONObject getRow(int position) {
        try {
            return result.getJSONObject(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addRow(){
        try {
            result.put(getCount(), new JSONObject());
            index = getCount() - 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void deleteRow(int index){
        result.remove(index);
        this.index = 0;
    }

    public void addColumn(String columnName){
        try {
            for (int i = 0; i < getCount(); i++) {
                result.getJSONObject(i).put(columnName, null);
            }
        } catch (JSONException e){ e.printStackTrace(); }
    }

    public void addBooleanColumn(String columnName){
        addBooleanColumn(columnName, false);
    }

    public void addBooleanColumn(String columnName, boolean defaultValue){
        try {
            for (int i = 0; i < getCount(); i++) {
                result.getJSONObject(i).put(columnName, defaultValue);
            }
        } catch (JSONException e){ e.printStackTrace(); }
    }

    public void deleteColumn(String columnName){
        try {
            for (int i = 0; i < getCount(); i++) {
                result.getJSONObject(i).remove(columnName);
            }
        } catch (JSONException e){ e.printStackTrace(); }
    }

    public ArrayList<String> getStringColumnArray(String columnName){
        ArrayList<String> columnArray = new ArrayList<>();
        for(int i = 0; i < getCount(); i++){
            columnArray.add(getString(i, columnName));
        }

        return columnArray;
    }

    public JSONResult filter(String filterColumn, Object filterValue){
        JSONArray filter = new JSONArray();
        try{
            for(int i = 0; i < getCount(); i++){
                if(equalValues(result.getJSONObject(i).get(filterColumn), filterValue)){
                    filter.put(result.getJSONObject(i));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return new JSONResult(filter);
    }

    public boolean findFirst(String findColumn, Object findValue){
        try{
            for(index = 0; index < getCount(); index++){
                if(findValue instanceof Date){
                     if(equalValues(getDate(index, findColumn), findValue)) return true;
                } else {
                     if(equalValues(result.getJSONObject(index).get(findColumn), findValue)) return true;
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean findNext(String findColumn, Object findValue){
        try{
            for(index = index + 1; index < getCount(); index++){

                if(findValue instanceof Date){
                    if(equalValues(getDate(index, findColumn), findValue)) return true;

                } else {
                    if(equalValues(result.getJSONObject(index).get(findColumn), findValue)) return true;
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void sort(final String sortColumn, final int sortOrder){
        JSONArray sortedResult = new JSONArray();

        List<JSONObject> sortList = new ArrayList<JSONObject>();
        try{
            for(int i = 0; i < getCount(); i++){
                sortList.add(result.getJSONObject(i));
            }

            Collections.sort(sortList, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject lhs, JSONObject rhs) {
                    try{
                        Object a = lhs.get(sortColumn);
                        Object b = rhs.get(sortColumn);
                        return compareValues(a, b, sortOrder);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    return 0;
                }
            });

            for(int i = 0; i < getCount(); i++){
                sortedResult.put(sortList.get(i));
            }

            result = sortedResult;

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private boolean equalValues(Object value1, Object value2){
        if(value2.getClass().equals(Integer.class)) {
            if(value1.getClass().equals(String.class))return Integer.parseInt((String) value1) == (int) value2;
            return (int) value1 == (int) value2;
        }
        if(value2.getClass().equals(String.class))
            return ((String) value1).equals((String) value2);
        if(value2.getClass().equals(Boolean.class))
            return  (Boolean) value1 == (Boolean) value2;
        if(value2.getClass().equals(Date.class))
            return ((Date)value1).equals(value2);

        return false;
    }

    private int compareValues(Object value1, Object value2, int sortOrder){
        if(value1.getClass().equals(Integer.class) && value2.getClass().equals(Integer.class)){
            if((int) value1 == (int) value2) return 0;

            switch(sortOrder){
                case SORT_ASCENDING:
                    return (int) value1 > (int) value2 ? 1 : -1;
                case SORT_DESCENDING:
                    return (int) value1 < (int) value2 ? 1 : -1;
            }
        }
        if(value1.getClass().equals(String.class) && value2.getClass().equals(String.class)){
            switch(sortOrder){
                case SORT_ASCENDING:
                    return ((String) value1).compareTo(((String)value2));
                case SORT_DESCENDING:
                    return - ((String) value1).compareTo(((String)value2));
            }
        }

        return 0;
    }
}
