package com.tk4218.grocerylistr.Database;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

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
        parameters.add(addParameter("return_cols", "RecipeKey,RecipeName,MealType,CuisineType,RecipeImage,Favorite,Rating,LastMade,LastEdited"));
        try{
            return new JSONResult(jsonParser.makeHttpRequest(database_url_retrieve, parameters).getJSONArray("data"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public int insertRecipe(String recipeName, String mealType, String cuisineType, String recipeImage){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "insert into tableRecipe (RecipeName,MealType,CuisineType,RecipeImage,Favorite,Rating,LastMade,LastEdited) values('" + recipeName + "','" + mealType + "','" + cuisineType + "','" + recipeImage + "',0,0,'0000-00-00', current_timestamp())"));
        try{
            JSONObject result = jsonParser.makeHttpRequest(database_url_insert, parameters);
            if(result.getInt("success") == 1){
                return result.getInt("insert_key");
            } else{
                Log.e("Insert Error", result.getString("message"));
                return -1;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return 0;
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

    public JSONResult getIngredientByName(String ingredientName){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select * from tableIngredient where IngredientName = '" + ingredientName + "'"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType"));
        try{
            return new JSONResult(jsonParser.makeHttpRequest(database_url_retrieve, parameters).getJSONArray("data"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public int insertIngredient(String ingredientName, String ingredientType){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "insert into tableIngredient (IngredientName,IngredientType) values('" + ingredientName + "','" + ingredientType + "')"));
        try{
            JSONObject result = jsonParser.makeHttpRequest(database_url_insert, parameters);
            if(result.getInt("success") == 1){
                return result.getInt("insert_key");
            } else{
                Log.e("Insert Error", result.getString("message"));
                return -1;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public boolean insertRecipeToIngredient(int recipeKey, int ingredientKey, double ingredientAmount, String ingredientUnit, String preparation1, String preparation2, boolean optional){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "insert into tableRecipeToIngredient (RecipeKey,IngredientKey,IngredientAmount,IngredientUnit,Preparation1,Preparation2,Optional)" +
                                                 "values(" + recipeKey + "," + ingredientKey + "," + ingredientAmount + ",'" + ingredientUnit + "','" + preparation1 + "','" + preparation2 + "'," + (optional ? 1 : 0) + ")"));
        try{
            JSONObject result = jsonParser.makeHttpRequest(database_url_insert, parameters);
            if(result.getInt("success") != 1){
                Log.e("Insert Error", result.getString("message"));
                return false;
            }
            return true;
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public JSONResult getRecipeIngredients(int recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select r.RecipeName, ri.IngredientAmount, ri.IngredientUnit, i.IngredientName from tableRecipeToIngredient ri, tableIngredient i, tableRecipe r where ri.RecipeKey = " +recipeKey+ " and i.IngredientKey = ri.ingredientKey and r.RecipeKey = ri.RecipeKey"));
        parameters.add(addParameter("return_cols", "RecipeName,IngredientAmount,IngredientUnit,IngredientName"));
        try{
            return new JSONResult(jsonParser.makeHttpRequest(database_url_retrieve, parameters).getJSONArray("data"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<String> addParameter(String paramName, String paramValue){
        ArrayList<String> param = new ArrayList<String>();
        param.add(paramName);
        param.add(paramValue);

        return param;
    }
}
