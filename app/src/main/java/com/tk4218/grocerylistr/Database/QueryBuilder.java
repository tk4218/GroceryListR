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

    private boolean insert(ArrayList<ArrayList<String>> parameters){
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

    private int insertReturnKey(ArrayList<ArrayList<String>> parameters){
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
        parameters.add(addParameter("return_cols", "RecipeKey,PinterestId,RecipeName,MealType,CuisineType,RecipeImage,Favorite,Rating,LastEdited"));
        return getResults(parameters);
    }

    public JSONResult getRecipe(int recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select * from tableRecipe where RecipeKey = " + recipeKey));
        parameters.add(addParameter("return_cols", "RecipeKey,PinterestId,RecipeName,MealType,CuisineType,RecipeImage,Favorite,Rating,LastEdited"));
        return getResults(parameters);
    }

    public JSONResult getPinterestRecipe(String pinterestId){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select * from tableRecipe where PinterestId = '"+ pinterestId +"'"));
        parameters.add(addParameter("return_cols", "RecipeKey,PinterestId,RecipeName,MealType,CuisineType,RecipeImage,Favorite,Rating,LastEdited"));
        return getResults(parameters);
    }

    public JSONResult getPinterestRecipes(){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select * from tableRecipe where PinterestId <> ''"));
        parameters.add(addParameter("return_cols", "RecipeKey,PinterestId,RecipeName,MealType,CuisineType,RecipeImage,Favorite,Rating,LastEdited"));
        return getResults(parameters);
    }

    public int insertRecipe(String pinterestId, String recipeName, String mealType, String cuisineType, String recipeImage){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "insert into tableRecipe (PinterestId,RecipeName,MealType,CuisineType,RecipeImage,Favorite,Rating,LastEdited) values('"+ pinterestId +"','" + recipeName + "','" + mealType + "','" + cuisineType + "','" + recipeImage + "',0,0, current_timestamp())"));
        return insertReturnKey(parameters);
    }

    public boolean updateRecipeFavorite(int recipeKey, boolean favorite){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "update tableRecipe set Favorite = " + (favorite ? 1 : 0) + " where RecipeKey = " + recipeKey));
        return insert(parameters);
    }

    public boolean updateRecipeInfo(int recipeKey, String recipeName, String mealType, String cuisineType, String recipeImage){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "update tableRecipe set RecipeName = '"+ recipeName +"', MealType = '"+ mealType +"', CuisineType = '"+ cuisineType +"', RecipeImage = '"+ recipeImage +"' where RecipeKey = " + recipeKey));
        return insert(parameters);
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

    public JSONResult getIngredientsFilter(String filter){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select * from tableIngredient where IngredientName like '%"+ filter +"%'"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife"));
        return getResults(parameters);
    }

    public int insertIngredient(String ingredientName, String ingredientType, int shelfLife){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "insert into tableIngredient (IngredientName,IngredientType,ShelfLife) values('" + ingredientName + "','" + ingredientType + "'," + shelfLife + ")"));
        return insertReturnKey(parameters);
    }

    /**********************************
     * RecipeToIngredient Queries
     **********************************/
    public boolean insertRecipeToIngredient(int recipeKey, int ingredientKey, double ingredientAmount, String ingredientUnit, String preparation1, String preparation2, boolean optional){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "insert into tableRecipeToIngredient (RecipeKey,IngredientKey,IngredientAmount,IngredientUnit,Preparation1,Preparation2,Optional)" +
                                                 "values(" + recipeKey + "," + ingredientKey + "," + ingredientAmount + ",'" + ingredientUnit + "','" + preparation1 + "','" + preparation2 + "'," + (optional ? 1 : 0) + ")"));
        return insert(parameters);
    }

    public JSONResult getRecipeIngredients(int recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select i.IngredientKey, i.IngredientName, i.IngredientType, i.ShelfLife, ri.IngredientAmount, ri.IngredientUnit, ri.Preparation1, ri.Preparation2 from tableRecipeToIngredient ri, tableIngredient i where ri.RecipeKey = " +recipeKey+ " and i.IngredientKey = ri.ingredientKey"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife,IngredientAmount,IngredientUnit,Preparation1,Preparation2"));
        return getResults(parameters);
    }

    public JSONResult getRecipeIngredient(int recipeKey, String ingredientName){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select i.IngredientKey, i.IngredientName, i.IngredientType, i.ShelfLife, ri.IngredientAmount, ri.IngredientUnit, ri.Preparation1, ri.Preparation2 from tableRecipeToIngredient ri, tableIngredient i where ri.RecipeKey = " +recipeKey+ " and i.IngredientKey = ri.ingredientKey and i.IngredientName = '" + ingredientName + "'"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife,IngredientAmount,IngredientUnit,Preparation1,Preparation2"));
        return getResults(parameters);
    }

    public boolean updateRecipeToIngredient(int recipeKey, int ingredientKey, double ingredientAmount, String ingredientUnit){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "update tableRecipeToIngredient set IngredientAmount = "+ ingredientAmount +", IngredientUnit = '"+ ingredientUnit +"' where RecipeKey = "+ recipeKey +" and IngredientKey = " +ingredientKey));
        return insert(parameters);
    }

    public boolean deleteRecipeToIngredient(int recipeKey, int ingredientKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "delete from tableRecipeToIngredient where RecipeKey = "+ recipeKey +" and IngredientKey = " +ingredientKey));
        return insert(parameters);
    }

    /**********************************
     * Meal Plan Queries
     **********************************/
    public boolean insertMealPlan(Date mealPlanDate, String mealType, int sequence, int recipeKey, int groceryListKey, boolean mealCompleted){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "insert into tableMealPlan (MealPlanDate,MealType,Sequence,RecipeKey,GroceryListKey,MealCompleted) values('" + dateString(mealPlanDate) + "','" + mealType + "'," + sequence + ","+ recipeKey+ ","+ groceryListKey + "," + mealCompleted + ")"));
        return insert(parameters);
    }

    public JSONResult getMealPlan(Date mealPlanDate){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select MealPlanDate, MealType, Sequence, RecipeKey, MealCompleted from tableMealPlan where MealPlanDate = '"+ dateString(mealPlanDate) +"'"));
        parameters.add(addParameter("return_cols", "MealPlanDate,MealType,Sequence,RecipeKey,MealCompleted"));
        return getResults(parameters);
    }

    public JSONResult getRecipeLastMade(int recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select MAX(MealPlanDate) as LastMade from tableMealPlan where RecipeKey = "+ recipeKey));
        parameters.add(addParameter("return_cols", "LastMade"));
        return getResults(parameters);
    }

    /**********************************
     * Grocery List Queries
     **********************************/
    public JSONResult getGroceryList(int groceryListKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select GroceryListKey,MealPlanDateStart,MealPlanDateEnd,Current,GroceryListCompleted,CompletedDate from tableGroceryList where GroceryListKey = " + groceryListKey));
        parameters.add(addParameter("return_cols", "GroceryListKey,MealPlanDateStart,MealPlanDateEnd,Current,GroceryListCompleted,CompletedDate"));
        return getResults(parameters);
    }

    public JSONResult getCurrentGroceryList(){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select GroceryListKey from tableGroceryList where Current = 1"));
        parameters.add(addParameter("return_cols", "GroceryListKey"));
        return getResults(parameters);
    }

    public JSONResult getGroceryListHistory(){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select GroceryListKey, MealPlanDateStart, MealPlanDateEnd, Current, GroceryListCompleted, CompletedDate from tableGroceryList order by MealPlanDateStart desc"));
        parameters.add(addParameter("return_cols", "GroceryListKey,MealPlanDateStart,MealPlanDateEnd,Current,GroceryListCompleted,CompletedDate"));
        return getResults(parameters);
    }

    public boolean setGroceryListCurrent(boolean current, int groceryListKey) {
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "update tableGroceryList set Current = " + (current ? 1 : 0) + " where GroceryListKey = " + groceryListKey));
        return insert(parameters);
    }

    public int insertGroceryList(Date mealPlanDateStart, Date mealPlanDateEnd, boolean current, boolean groceryListCompleted, Date completedDate){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "insert into tableGroceryList (MealPlanDateStart,MealPlanDateEnd,Current,GroceryListCompleted,CompletedDate) values('"+ dateString(mealPlanDateStart) +"','"+ dateString(mealPlanDateEnd) +"',"+ (current ? 1 : 0) +","+ (groceryListCompleted ? 1 : 0) +",'"+ dateString(completedDate) +"')"));
        return insertReturnKey(parameters);
    }

    public JSONResult getIngredientsForGroceryList(Date mealPlanDateStart, Date mealPlanDateEnd){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "Select i.IngredientType, ri.IngredientAmount, ri.IngredientUnit, i.IngredientName , i.IngredientKey from tableMealPlan mp, tableRecipeToIngredient ri, tableIngredient i where mp.MealPlanDate >= '"+ dateString(mealPlanDateStart) +"' and mp.MealPlanDate <= '"+ dateString(mealPlanDateEnd) +"' and ri.RecipeKey = mp.RecipeKey and i.IngredientKey = ri.IngredientKey"));
        parameters.add(addParameter("return_cols", "IngredientType,IngredientAmount,IngredientUnit,IngredientName,IngredientKey"));
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

    public int insertGroceryListItem(int groceryListKey, int ingredientKey, double ingredientAmount, String ingredientUnit, boolean addedToCart){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "insert into tableGroceryListItem (GroceryListKey,IngredientKey,IngredientAmount,IngredientUnit,AddedToCart) values("+ groceryListKey +","+ ingredientKey +","+ ingredientAmount +",'"+ ingredientUnit +"',"+ (addedToCart ? 1 : 0) +")"));
        return insertReturnKey(parameters);
    }

    public JSONResult getGroceryListItem(int groceryListItemKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "select GroceryListItemKey,GroceryListKey,IngredientKey,IngredientAmount,IngredientUnit,AddedToCart from tableGroceryListItem where GroceryListItemKey = " + groceryListItemKey));
        parameters.add(addParameter("return_cols", "GroceryListItemKey,GroceryListKey,IngredientKey,IngredientAmount,IngredientUnit,AddedToCart"));
        return getResults(parameters);
    }

    public boolean updateGroceryListItemAmount(int groceryListItemKey, double amount){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "update tableGroceryListItem set IngredientAmount = " + amount + " where GroceryListItemKey = " + groceryListItemKey));
        return insert(parameters);
    }

    public boolean updateAddedToCart(int groceryListItemKey, boolean addedToCart){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "update tableGroceryListItem set AddedToCart = " +(addedToCart ? 1 : 0)+ " where GroceryListItemKey = " + groceryListItemKey));
        return insert(parameters);
    }

    public boolean removeGroceryListItem(int groceryListItemKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
        parameters.add(addParameter("sql_query", "delete from tableGroceryListItem where GroceryListItemKey = "+ groceryListItemKey));
        return insert(parameters);
    }
}
