package com.tk4218.grocerylistr.Model;

import java.util.ArrayList;

/**
 * Created by Tk4218 on 8/12/2017.
 */

public class QueryBuilder {

    private JSONParser jsonParser = new JSONParser();
    private static final String database_url_retrieve = "http://grocerylistr-env.br8sdfvknb.us-west-1.elasticbeanstalk.com/retrieve.php";
    private static final String database_url_insert = "http://grocerylistr-env.br8sdfvknb.us-west-1.elasticbeanstalk.com/insert.php";

    public QueryBuilder(){

    }

    public JSONResult getAllRecipes(){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select * from tableRecipe"));
        parameters.add(addParameter("return_cols", "RecipeKey,RecipeName,MealType,CuisineType,RecipeImage,Favorite,LastMade,LastEdited"));
        try{
            return new JSONResult(jsonParser.makeHttpRequest(database_url_retrieve, parameters).getJSONArray("data"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertRecipe(String recipeName, String mealType, String cuisineType, String recipeImage){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "insert into tableRecipe (RecipeName,MealType,CuisineType,RecipeImage,Favorite,LastMade,LastEdited) values('" + recipeName + "','" + mealType + "','" + cuisineType + "','" + recipeImage + "',0, '0000-00-00', current_timestamp())"));
        try{
            return jsonParser.makeHttpRequest(database_url_insert, parameters).getInt("success") == 1;
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateRecipeFavorite(int recipeKey, boolean favorite){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "update tableRecipe set Favorite = " + (favorite ? 1 : 0) + " where RecipeKey = " + recipeKey));
        try{
            return jsonParser.makeHttpRequest(database_url_insert, parameters).getInt("success") == 1;
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private ArrayList<String> addParameter(String paramName, String paramValue){
        ArrayList<String> param = new ArrayList<String>();
        param.add(paramName);
        param.add(paramValue);

        return param;
    }
}
