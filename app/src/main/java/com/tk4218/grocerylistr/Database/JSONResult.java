package com.tk4218.grocerylistr.Database;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

    public JSONObject getRow(int position) {
        try {
            return result.getJSONObject(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
                if(equalValues(result.getJSONObject(index).get(findColumn), findValue)){
                    return true;
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean findNext(String findColumn, Object findValue){
        try{
            for(; index < getCount(); index++){
                if(equalValues(result.getJSONObject(index).get(findColumn), findValue)){
                    return true;
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
        if(value1.getClass().equals(Integer.class) && value2.getClass().equals(Integer.class))
            return (int) value1 == (int) value2;
        if(value1.getClass().equals(String.class) && value2.getClass().equals(String.class))
            return ((String) value1).equals((String) value2);
        if(value1.getClass().equals(Boolean.class) && value2.getClass().equals(Boolean.class))
            return  (Boolean) value1 == (Boolean) value2;

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
