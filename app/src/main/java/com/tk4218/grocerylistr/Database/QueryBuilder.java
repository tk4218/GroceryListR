package com.tk4218.grocerylistr.Database;

import android.util.Log;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/*
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
        ArrayList<String> param = new ArrayList<>();
        param.add(paramName);
        param.add(paramValue);

        return param;
    }

    private String dateString(Date date){
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date);
    }

    /*-------------------------------------------------------*
     * Queries
     *-------------------------------------------------------*/

    /*-----------------------------------*
     * Recipe Queries
     *-----------------------------------*/
    public JSONResult getAllRecipes(){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select * from tableRecipe where PinterestId = ''"));
        parameters.add(addParameter("return_cols", "RecipeKey,PinterestId,RecipeName,MealType,CuisineType,RecipeImage,Favorite,Rating,LastEdited"));
        return getResults(parameters);
    }

    public JSONResult getRecipe(int recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select * from tableRecipe where RecipeKey = " + recipeKey));
        parameters.add(addParameter("return_cols", "RecipeKey,PinterestId,RecipeName,MealType,CuisineType,RecipeImage,Favorite,Rating,LastEdited"));
        return getResults(parameters);
    }

    public JSONResult getPinterestRecipe(String pinterestId){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select * from tableRecipe where PinterestId = '"+ pinterestId +"'"));
        parameters.add(addParameter("return_cols", "RecipeKey,PinterestId,RecipeName,MealType,CuisineType,RecipeImage,Favorite,Rating,LastEdited"));
        return getResults(parameters);
    }

    public JSONResult getPinterestRecipes(String username){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select r.* from UserRecipes u, tableRecipe r Where u.Username = '"+username+"' and r.RecipeKey = u.RecipeKey and r.PinterestId <> ''"));
        parameters.add(addParameter("return_cols", "RecipeKey,PinterestId,RecipeName,MealType,CuisineType,RecipeImage,Favorite,Rating,LastEdited"));
        return getResults(parameters);
    }

    public int insertRecipe(String pinterestId, String recipeName, String mealType, String cuisineType, String recipeImage){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "insert into tableRecipe (PinterestId,RecipeName,MealType,CuisineType,RecipeImage,Favorite,Rating,LastEdited) values('"+ pinterestId +"','" + recipeName.replace("'", "''") + "','" + mealType + "','" + cuisineType + "','" + recipeImage + "',0,0, current_timestamp())"));
        return insertReturnKey(parameters);
    }

    public boolean updateRecipeFavorite(int recipeKey, boolean favorite){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "update tableRecipe set Favorite = " + (favorite ? 1 : 0) + " where RecipeKey = " + recipeKey));
        return insert(parameters);
    }

    public boolean updateRecipeInfo(int recipeKey, String recipeName, String mealType, String cuisineType, String recipeImage){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "update tableRecipe set RecipeName = '"+ recipeName.replace("'", "''") +"', MealType = '"+ mealType +"', CuisineType = '"+ cuisineType +"', RecipeImage = '"+ recipeImage +"' where RecipeKey = " + recipeKey));
        return insert(parameters);
    }

    /*---------------------------------
     * User Edit Recipe Queries
     *---------------------------------*/
    public JSONResult getUserEditRecipe(int recipeEditKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select * from UserEditRecipes where RecipeEditKey = " + recipeEditKey));
        parameters.add(addParameter("return_cols", "RecipeEditKey,Username,RecipeKey,RecipeName,MealType,CuisineType,LastEdited"));
        return getResults(parameters);
    }

    public int insertUserEditRecipe(String username, int recipeKey, String recipeName, String mealType, String cuisineType){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "insert into UserEditRecipes (Username,RecipeKey,RecipeName,MealType,CuisineType,LastEdited) values('"+username+"',"+recipeKey+",'" + recipeName.replace("'", "''") + "','" + mealType + "','" + cuisineType + "',current_timestamp())"));
        return insertReturnKey(parameters);
    }

    public boolean updateUserEditRecipe(String recipeName, String mealType, String cuisineType, String username, int recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "update UserEditRecipes set RecipeName = '"+recipeName+"', MealType = '"+cuisineType+"', CuisineType = '"+cuisineType+"' where Username = '"+username+"' and RecipeKey = " + recipeKey));
        return insert(parameters);
    }

    /*---------------------------------
     * User Recipes Queries
     *---------------------------------*/
    public JSONResult getUserRecipes(String username){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select ur.RecipeKey, r.PinterestId, coalesce(e.RecipeName, r.RecipeName) as RecipeName, coalesce(e.MealType, r.MealType) as MealType, coalesce(e.CuisineType, r.CuisineType) as CuisineType, r.RecipeImage, ur.Favorite, r.Rating, coalesce(e.LastEdited, '0000-00-00 00:00:00') as LastEdited from UserRecipes ur left join UserEditRecipes e on ur.RecipeEditKey <> 0 and e.RecipeEditKey = ur.RecipeEditKey, tableRecipe r where ur.Username = '"+username+"' and r.RecipeKey = ur.RecipeKey"));
        parameters.add(addParameter("return_cols", "RecipeKey,PinterestId,RecipeName,MealType,CuisineType,RecipeImage,Favorite,Rating,LastEdited"));
        return getResults(parameters);
    }
    public JSONResult getUserRecipe(String username, int recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select * from UserRecipes where Username = '"+username+"' and RecipeKey = " + recipeKey));
        parameters.add(addParameter("return_cols", "Username,RecipeKey,RecipeEditKey,Favorite"));
        return getResults(parameters);
    }

    public boolean insertUserRecipe(String username, int recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "insert into UserRecipes (Username,RecipeKey,RecipeEditKey,Favorite) values('"+ username +"'," + recipeKey + ",0,0)"));
        return insert(parameters);
    }

    public boolean updateUserRecipeEditKey(String username, int recipeKey, int recipeEditKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "update UserRecipes set RecipeEditKey = "+recipeEditKey+" where Username = '"+username+"' and RecipeKey = " +recipeKey));
        return insert(parameters);
    }

    /*-----------------------------------*
     * Ingredient Queries
     *-----------------------------------*/
    public JSONResult getIngredientByName(String ingredientName){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select * from tableIngredient where IngredientName = '" + ingredientName + "'"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife"));
        return getResults(parameters);
    }

    public JSONResult getIngredient(int ingredientKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select * from tableIngredient where IngredientKey = '" + ingredientKey + "'"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife"));
        return getResults(parameters);
    }

    public JSONResult getAllIngredients(){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select * from tableIngredient"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife"));
        return getResults(parameters);
    }

    public JSONResult getIngredientsFilter(String filter){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select * from tableIngredient where IngredientName like '%"+ filter +"%'"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife"));
        return getResults(parameters);
    }

    public int insertIngredient(String ingredientName, String ingredientType, int shelfLife){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "insert into tableIngredient (IngredientName,IngredientType,ShelfLife) values('" + ingredientName.replace("'", "''") + "','" + ingredientType + "'," + shelfLife + ")"));
        return insertReturnKey(parameters);
    }

    public boolean editIngredient(String ingredientName, String ingredientType, int shelfLife, int ingredientKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "update tableIngredient set IngredientName = '" + ingredientName.replace("'", "''") + "', IngredientType = '" + ingredientType + "', ShelfLife = " + shelfLife + " where IngredientKey = " + ingredientKey));
        return insert(parameters);
    }

    /*-----------------------------------*
     * RecipeToIngredient Queries
     *-----------------------------------*/
    public boolean insertRecipeToIngredient(int recipeKey, int ingredientKey, double ingredientAmount, String ingredientUnit, String preparation1, String preparation2, boolean optional){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "insert into tableRecipeToIngredient (RecipeKey,IngredientKey,IngredientAmount,IngredientUnit,Preparation1,Preparation2,Optional)" +
                                                 "values(" + recipeKey + "," + ingredientKey + "," + ingredientAmount + ",'" + ingredientUnit + "','" + preparation1 + "','" + preparation2 + "'," + (optional ? 1 : 0) + ")"));
        return insert(parameters);
    }

    public JSONResult getRecipeIngredients(int recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select i.IngredientKey, i.IngredientName, i.IngredientType, i.ShelfLife, ri.IngredientAmount, ri.IngredientUnit, ri.Preparation1, ri.Preparation2 from tableRecipeToIngredient ri, tableIngredient i where ri.RecipeKey = " +recipeKey+ " and i.IngredientKey = ri.ingredientKey"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife,IngredientAmount,IngredientUnit,Preparation1,Preparation2"));
        return getResults(parameters);
    }

    public JSONResult getRecipeIngredient(int recipeKey, String ingredientName){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select i.IngredientKey, i.IngredientName, i.IngredientType, i.ShelfLife, ri.IngredientAmount, ri.IngredientUnit, ri.Preparation1, ri.Preparation2 from tableRecipeToIngredient ri, tableIngredient i where ri.RecipeKey = " +recipeKey+ " and i.IngredientKey = ri.ingredientKey and i.IngredientName = '" + ingredientName + "'"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife,IngredientAmount,IngredientUnit,Preparation1,Preparation2"));
        return getResults(parameters);
    }

    public boolean updateRecipeToIngredient(int recipeKey, int ingredientKey, double ingredientAmount, String ingredientUnit){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "update tableRecipeToIngredient set IngredientAmount = "+ ingredientAmount +", IngredientUnit = '"+ ingredientUnit +"' where RecipeKey = "+ recipeKey +" and IngredientKey = " +ingredientKey));
        return insert(parameters);
    }

    public boolean deleteRecipeToIngredient(int recipeKey, int ingredientKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "delete from tableRecipeToIngredient where RecipeKey = "+ recipeKey +" and IngredientKey = " +ingredientKey));
        return insert(parameters);
    }

    /*-----------------------------------
     * User Recipe to Ingredient Queries
     *-----------------------------------*/
    public JSONResult getUserRecipeIngredients(String username, int recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select i.IngredientKey, i.IngredientName, i.IngredientType, i.ShelfLife, ri.IngredientAmount, ri.IngredientUnit, ri.Preparation1, ri.Preparation2, ri.RemoveIngredient from UserRecipeToIngredient ri, tableIngredient i where ri.Username = '"+username+"' and ri.RecipeKey = " +recipeKey+ " and i.IngredientKey = ri.ingredientKey"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife,IngredientAmount,IngredientUnit,Preparation1,Preparation2,RemoveIngredient"));
        return getResults(parameters);
    }

    public boolean insertUserRecipeIngredient(String username, int recipeKey, int ingredientKey, double ingredientAmount, String ingredientUnit, String preparation1, String preparation2, boolean optional, boolean remove){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "insert into UserRecipeToIngredient (Username,RecipeKey,IngredientKey,IngredientAmount,IngredientUnit,Preparation1,Preparation2,Optional,RemoveIngredient) values('"+username+"',"+recipeKey+","+ingredientKey+","+ingredientAmount+",'"+ingredientUnit+"','"+preparation1+"','"+preparation2+"',"+(optional ? 1 : 0)+","+(remove ? 1: 0)+")"));
        return insert(parameters);
    }

    public boolean updateUserRecipeToIngredient(String username, int recipeKey, int ingredientKey, double ingredientAmount, String ingredientUnit, boolean remove){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "update UserRecipeToIngredient set IngredientAmount = "+ ingredientAmount +", IngredientUnit = '"+ ingredientUnit +"', RemoveIngredient = "+(remove ? 1 : 0)+" where Username = '"+username+"' and RecipeKey = "+ recipeKey +" and IngredientKey = " +ingredientKey));
        return insert(parameters);
    }

    public boolean updateUserRecipeToIngredientRemove(String username, int recipeKey, int ingredientKey, boolean remove){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "update UserRecipeToIngredient set RemoveIngredient = "+ (remove ? 1 : 0) +" where Username = '"+username+"' and RecipeKey = "+ recipeKey +" and IngredientKey = " +ingredientKey));
        return insert(parameters);
    }

    public boolean deleteUserRecipeToIngredient(String username, int recipeKey, int ingredientKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "delete from UserRecipeToIngredient where Username = '"+username+"' and RecipeKey = "+ recipeKey +" and IngredientKey = " +ingredientKey));
        return insert(parameters);
    }
    /*-----------------------------------
     * Meal Plan Queries
     *-----------------------------------*/
    public boolean insertMealPlan(String username, Date mealPlanDate, String mealType, int sequence, int recipeKey, int groceryListKey, boolean mealCompleted){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "insert into UserMealPlans (Username,MealPlanDate,MealType,Sequence,RecipeKey,GroceryListKey,MealCompleted) values('"+username+"','" + dateString(mealPlanDate) + "','" + mealType + "'," + sequence + ","+ recipeKey+ ","+ groceryListKey + "," + mealCompleted + ")"));
        return insert(parameters);
    }

    public JSONResult getMealPlan(String username, Date mealPlanDate){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select MealPlanDate, MealType, Sequence, RecipeKey, MealCompleted from UserMealPlans where Username = '"+username+"' and MealPlanDate = '"+ dateString(mealPlanDate) +"'"));
        parameters.add(addParameter("return_cols", "MealPlanDate,MealType,Sequence,RecipeKey,MealCompleted"));
        return getResults(parameters);
    }

    public JSONResult getRecipeLastMade(String username, int recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select MAX(MealPlanDate) as LastMade from UserMealPlans where Username = '"+username+"' and RecipeKey = "+ recipeKey));
        parameters.add(addParameter("return_cols", "LastMade"));
        return getResults(parameters);
    }

    public JSONResult getMonthMealPlans(String username, Date beginDate, Date endDate){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select MealPlanDate, MealType, Sequence, RecipeKey, MealCompleted from UserMealPlans where Username = '"+username+"' and MealPlanDate >= '"+ dateString(beginDate) +"' and MealPlanDate <= '"+ dateString(endDate) +"' order by MealPlanDate,Sequence"));
        parameters.add(addParameter("return_cols", "MealPlanDate,MealType,Sequence,RecipeKey,MealCompleted"));
        return getResults(parameters);
    }

    /*-----------------------------------*
     * Grocery List Queries
     *-----------------------------------*/
    public JSONResult getGroceryList(int groceryListKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select GroceryListKey,Username,MealPlanDateStart,MealPlanDateEnd,Current,GroceryListCompleted,CompletedDate from GroceryLists where GroceryListKey = " + groceryListKey));
        parameters.add(addParameter("return_cols", "GroceryListKey,Username,MealPlanDateStart,MealPlanDateEnd,Current,GroceryListCompleted,CompletedDate"));
        return getResults(parameters);
    }

    public JSONResult getCurrentGroceryList(String username){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select GroceryListKey from GroceryLists where Username = '"+username+"' and Current = 1"));
        parameters.add(addParameter("return_cols", "GroceryListKey"));
        return getResults(parameters);
    }

    public JSONResult getGroceryListHistory(String username){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select GroceryListKey, MealPlanDateStart, MealPlanDateEnd, Current, GroceryListCompleted, CompletedDate from GroceryLists where Username = '"+username+"'order by MealPlanDateStart desc"));
        parameters.add(addParameter("return_cols", "GroceryListKey,MealPlanDateStart,MealPlanDateEnd,Current,GroceryListCompleted,CompletedDate"));
        return getResults(parameters);
    }

    public boolean setGroceryListCurrent(boolean current, int groceryListKey) {
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "update GroceryLists set Current = " + (current ? 1 : 0) + " where GroceryListKey = " + groceryListKey));
        return insert(parameters);
    }

    public int insertGroceryList(String username, Date mealPlanDateStart, Date mealPlanDateEnd, boolean current, boolean groceryListCompleted, Date completedDate){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "insert into GroceryLists (Username,MealPlanDateStart,MealPlanDateEnd,Current,GroceryListCompleted,CompletedDate) values('"+username+"','"+ dateString(mealPlanDateStart) +"','"+ dateString(mealPlanDateEnd) +"',"+ (current ? 1 : 0) +","+ (groceryListCompleted ? 1 : 0) +",'"+ dateString(completedDate) +"')"));
        return insertReturnKey(parameters);
    }

    public JSONResult getIngredientsForGroceryList(String username, Date mealPlanDateStart, Date mealPlanDateEnd){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query","Select i.IngredientType, coalesce(ur.IngredientAmount, ri.IngredientAmount) as IngredientAmount, coalesce(ur.IngredientUnit, ri.IngredientUnit) as IngredientUnit, i.IngredientName, i.IngredientKey, coalesce(ur.RemoveIngredient, 0) as RemoveIngredient from UserMealPlans mp, tableRecipeToIngredient ri left join UserRecipeToIngredient ur on ur.Username = '"+username+"' and ur.RecipeKey = ri.RecipeKey and ur.IngredientKey = ri.IngredientKey, tableIngredient i where mp.Username = '"+username+"' and mp.MealPlanDate >= '"+dateString(mealPlanDateStart)+"' and mp.MealPlanDate <= '"+dateString(mealPlanDateEnd)+"' and ri.RecipeKey = mp.RecipeKey and i.IngredientKey = ri.IngredientKey " +
                "union all Select i.IngredientType, ur.IngredientAmount, ur.IngredientUnit, i.IngredientName, i.IngredientKey, ur.RemoveIngredient from UserMealPlans mp, UserRecipeToIngredient ur, tableIngredient i where mp.Username = '"+username+"' and mp.MealPlanDate >= '"+dateString(mealPlanDateStart)+"' and mp.MealPlanDate <= '"+dateString(mealPlanDateEnd)+"' and ur.Username = mp.Username and ur.RecipeKey = mp.RecipeKey and ur.RemoveIngredient = 0 and i.IngredientKey = ur.IngredientKey and not exists(select 1 from tableRecipeToIngredient where RecipeKey = ur.RecipeKey and IngredientKey = ur.IngredientKey)"));
        parameters.add(addParameter("return_cols", "IngredientType,IngredientAmount,IngredientUnit,IngredientName,IngredientKey,RemoveIngredient"));
        return getResults(parameters);
    }

    public boolean updateGroceryListCompleted(int groceryListKey, boolean completed) {
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        String completedDate = (completed ? "current_timestamp()" : "'" + dateString(new Date(0)) + "'");
        parameters.add(addParameter("sql_query", "update GroceryLists set GroceryListCompleted = " + (completed ? 1 : 0) + ", CompletedDate = " + completedDate + " where GroceryListKey = " + groceryListKey));
        return insert(parameters);
    }

    /*-----------------------------------*
     * Grocery List Item Queries
     *-----------------------------------*/
    public JSONResult getGroceryListItems(int groceryListKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select GroceryListItemKey,IngredientKey,IngredientAmount,IngredientUnit,AddedToCart from GroceryListItems where GroceryListKey = " + groceryListKey));
        parameters.add(addParameter("return_cols", "GroceryListItemKey,IngredientKey,IngredientAmount,IngredientUnit,AddedToCart"));
        return getResults(parameters);
    }

    public int insertGroceryListItem(int groceryListKey, int ingredientKey, double ingredientAmount, String ingredientUnit, boolean addedToCart){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "insert into GroceryListItems (GroceryListKey,IngredientKey,IngredientAmount,IngredientUnit,AddedToCart) values("+ groceryListKey +","+ ingredientKey +","+ ingredientAmount +",'"+ ingredientUnit +"',"+ (addedToCart ? 1 : 0) +")"));
        return insertReturnKey(parameters);
    }

    public JSONResult getGroceryListItem(int groceryListItemKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select GroceryListItemKey,GroceryListKey,IngredientKey,IngredientAmount,IngredientUnit,AddedToCart from GroceryListItems where GroceryListItemKey = " + groceryListItemKey));
        parameters.add(addParameter("return_cols", "GroceryListItemKey,GroceryListKey,IngredientKey,IngredientAmount,IngredientUnit,AddedToCart"));
        return getResults(parameters);
    }

    public boolean updateGroceryListItemAmount(int groceryListItemKey, double amount){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "update GroceryListItems set IngredientAmount = " + amount + " where GroceryListItemKey = " + groceryListItemKey));
        return insert(parameters);
    }

    public boolean updateAddedToCart(int groceryListItemKey, boolean addedToCart){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "update GroceryListItems set AddedToCart = " +(addedToCart ? 1 : 0)+ " where GroceryListItemKey = " + groceryListItemKey));
        return insert(parameters);
    }

    public boolean removeGroceryListItem(int groceryListItemKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "delete from GroceryListItems where GroceryListItemKey = "+ groceryListItemKey));
        return insert(parameters);
    }

    /*-----------------------------------*
     * User Queries
     *-----------------------------------*/
    public JSONResult getUserByUsername(String username){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select Username,Email from Users where Username = '"+ username +"'"));
        parameters.add(addParameter("return_cols", "Username,Email"));
        return getResults(parameters);
    }
    public JSONResult getUserByEmail(String email){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select Username,Email from Users where Email = '"+ email +"'"));
        parameters.add(addParameter("return_cols", "Username,Email"));
        return getResults(parameters);
    }
    public JSONResult getUserPasswordByUsername(String username){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select Username,Email,Password from Users where Username = '"+ username +"'"));
        parameters.add(addParameter("return_cols", "Username,Email,Password"));
        return getResults(parameters);
    }
    public JSONResult getUserPasswordByEmail(String email){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select Username,Email,Password from Users where Email = '"+ email +"'"));
        parameters.add(addParameter("return_cols", "Username,Email,Password"));
        return getResults(parameters);
    }
    public boolean insertUser(String username, String email, String password, String firstName, String lastName) {
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "insert into Users (Username,Email,Password,FirstName,LastName) values('"+username+"','"+email+"','"+password+"','"+firstName+"','"+lastName+"')"));
        return insert(parameters);
    }
}
