package com.tk4218.grocerylistr.Database;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Tk4218 on 8/12/2017.
 */

public class QueryBuilder {

    private JSONParser jsonParser = new JSONParser();
    private static final String database_url_retrieve = "http://grocerylistr-env.br8sdfvknb.us-west-1.elasticbeanstalk.com/retrieve.php";
    private static final String database_url_insert = "http://grocerylistr-env.br8sdfvknb.us-west-1.elasticbeanstalk.com/insert.php";

    public QueryBuilder(){

    }

    private JSONResult getResults(ArrayList<ArrayList<String>> parameters){
        try{
            JSONObject result = jsonParser.makeHttpRequest(database_url_retrieve, parameters);
            if(result.getInt("success") == 1)
                return new JSONResult(result.getJSONArray("data"));
            else
                Log.e("RETRIEVE ERROR", result.getString("message"));
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

    private String dateString(Date date){
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    /********************************************************
     * Queries
     ********************************************************/

    /**********************************
     * Recipe Queries
     **********************************/
    public JSONResult getAllRecipes(){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select * from tableRecipe"));
        parameters.add(addParameter("return_cols", "RecipeKey,RecipeName,MealType,CuisineType,RecipeImage,Favorite,Rating,LastMade,LastEdited"));
        return getResults(parameters);
    }

    public JSONResult getRecipe(int recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select * from tableRecipe where RecipeKey = " + recipeKey));
        parameters.add(addParameter("return_cols", "RecipeKey,RecipeName,MealType,CuisineType,RecipeImage,Favorite,Rating,LastMade,LastEdited"));
        return getResults(parameters);
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

    /**********************************
     * Ingredient Queries
     **********************************/
    public JSONResult getIngredientByName(String ingredientName){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select * from tableIngredient where IngredientName = '" + ingredientName + "'"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife"));
        return getResults(parameters);
    }

    public JSONResult getIngredient(int ingredientKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select * from tableIngredient where IngredientKey = '" + ingredientKey + "'"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife"));
        return getResults(parameters);
    }

    public JSONResult getAllIngredients(){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select * from tableIngredient"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife"));
        return getResults(parameters);
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

    /**********************************
     * RecipeToIngredient Queries
     **********************************/
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
        parameters.add(addParameter("sql_query", "select i.IngredientKey, i.IngredientName, i.IngredientType, i.ShelfLife, ri.IngredientAmount, ri.IngredientUnit from tableRecipeToIngredient ri, tableIngredient i where ri.RecipeKey = " +recipeKey+ " and i.IngredientKey = ri.ingredientKey"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife,IngredientAmount,IngredientUnit"));
        return getResults(parameters);
    }

    public JSONResult getRecipeIngredient(int recipeKey, String ingredientName){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select i.IngredientKey, i.IngredientName, i.IngredientType, i.ShelfLife, ri.IngredientAmount, ri.IngredientUnit from tableRecipeToIngredient ri, tableIngredient i where ri.RecipeKey = " +recipeKey+ " and i.IngredientKey = ri.ingredientKey and i.IngredientName = '" + ingredientName + "'"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife,IngredientAmount,IngredientUnit"));
        return getResults(parameters);
    }


    /**********************************
     * Meal Plan Queries
     **********************************/
    public boolean insertMealPlan(Date mealPlanDate, String mealType, int sequence, int recipeKey, int groceryListKey, boolean mealCompleted){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "insert into tableMealPlan (MealPlanDate,MealType,Sequence,RecipeKey,GroceryListKey,MealCompleted) values('" + dateString(mealPlanDate) + "','" + mealType + "'," + sequence + ","+ recipeKey+ ","+ groceryListKey + "," + mealCompleted + ")"));
        try{
            JSONObject result = jsonParser.makeHttpRequest(database_url_insert, parameters);
            if(result.getInt("success") == 1){
                return true;
            } else{
                Log.e("Insert Error", result.getString("message"));
                return false;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public JSONResult getMealPlan(Date mealPlanDate){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select MealPlanDate, MealType, Sequence, RecipeKey, MealCompleted from tableMealPlan where MealPlanDate = '"+ dateString(mealPlanDate) +"'"));
        parameters.add(addParameter("return_cols", "MealPlanDate,MealType,Sequence,RecipeKey,MealCompleted"));
        return getResults(parameters);
    }

    /**********************************
     * Grocery List Queries
     **********************************/
    public JSONResult getGroceryList(int groceryListKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select GroceryListKey,MealPlanDateStart,MealPlanDateEnd,GroceryListCompleted,CompletedDate from tableGroceryList where GroceryListKey = " + groceryListKey));
        parameters.add(addParameter("return_cols", "GroceryListKey,MealPlanDateStart,MealPlanDateEnd,GroceryListCompleted,CompletedDate"));
        return getResults(parameters);
    }

    /**********************************
     * Grocery List Item Queries
     **********************************/
    public JSONResult getGroceryListItems(int groceryListKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select GroceryListItemKey,IngredientKey,IngredientAmount,IngredientUnit,AddedToCart from tableGroceryListItem where GroceryListKey = " + groceryListKey));
        parameters.add(addParameter("return_cols", "GroceryListItemKey,IngredientKey,IngredientAmount,IngredientUnit,AddedToCart"));
        return getResults(parameters);
    }
}
