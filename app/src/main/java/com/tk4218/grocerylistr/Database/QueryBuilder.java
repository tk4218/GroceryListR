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
    //private static final String database_url_retrieve = "http://grocerylistr-env.br8sdfvknb.us-west-1.elasticbeanstalk.com/retrieve.php";
    private static final String database_url_retrieve = "https://grocerylistr.000webhostapp.com/retrieve.php";
    private static final String database_url_insert = "https://grocerylistr.000webhostapp.com/insert.php";
    //private static final String database_url_insert = "http://grocerylistr-env.br8sdfvknb.us-west-1.elasticbeanstalk.com/insert.php";

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
    public JSONResult getAllRecipes(String username, int startIndex, String sortString){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select r.*, coalesce(ur.Favorite, 0) as Favorite, coalesce(ur.Username, '') as Username from Recipes r left join UserRecipes ur on r.RecipeKey = ur.RecipeKey and ur.Username = '"+username+"' where r.PinterestId = '' "+sortString+" limit "+startIndex+", 10"));
        parameters.add(addParameter("return_cols", "RecipeKey,PinterestId,RecipeName,MealType,CuisineType,RecipeImage,Rating,LastEdited,Favorite,Username"));
        return getResults(parameters);
    }
    public JSONResult getRecipe(String recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select * from Recipes where RecipeKey = " + recipeKey));
        parameters.add(addParameter("return_cols", "RecipeKey,PinterestId,RecipeName,MealType,CuisineType,RecipeImage,Rating,RatingCount,LastEdited"));
        return getResults(parameters);
    }
    public JSONResult getPinterestRecipes(String username){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select r.* from UserRecipes u, Recipes r Where u.Username = '"+username+"' and r.RecipeKey = u.RecipeKey and r.PinterestId <> ''"));
        parameters.add(addParameter("return_cols", "RecipeKey,PinterestId,RecipeName,MealType,CuisineType,RecipeImage,Rating,RatingCount,LastEdited"));
        return getResults(parameters);
    }
    public int insertRecipe(String pinterestId, String recipeName, String mealType, String cuisineType, String recipeImage){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "insert into Recipes (PinterestId,RecipeName,MealType,CuisineType,RecipeImage,Rating,RatingCount,LastEdited) values('"+ pinterestId +"','" + recipeName.replace("'", "''") + "','" + mealType + "','" + cuisineType + "','" + recipeImage + "',0,0, current_timestamp())"));
        return insertReturnKey(parameters);
    }
    public boolean updateRecipeRating(String recipeKey, double rating, int ratingCount){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "update Recipes set Rating = " + rating + ", RatingCount = "+ratingCount+" where RecipeKey = " + recipeKey));
        return insert(parameters);
    }
    public boolean updateRecipeImage(String recipeImage, int recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "update Recipes set RecipeImage = '" + recipeImage + "' where RecipeKey = " + recipeKey));
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
    public boolean deleteUserEditRecipe(int recipeEditKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "delete from UserEditRecipes where RecipeEditKey = " + recipeEditKey));
        return insert(parameters);
    }

    /*---------------------------------
     * User Recipes Queries
     *---------------------------------*/
    public JSONResult getUserRecipes(String username, int startIndex, boolean favorites, String sortString){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select ur.RecipeKey, r.PinterestId, coalesce(e.RecipeName, r.RecipeName) as RecipeName, coalesce(e.MealType, r.MealType) as MealType, coalesce(e.CuisineType, r.CuisineType) as CuisineType, r.RecipeImage, ur.Favorite, r.Rating, coalesce(e.LastEdited, '0000-00-00 00:00:00') as LastEdited, ur.Username from UserRecipes ur left join UserEditRecipes e on ur.RecipeEditKey <> 0 and e.RecipeEditKey = ur.RecipeEditKey, Recipes r where ur.Username = '"+username+"' and r.RecipeKey = ur.RecipeKey"+(favorites ? " and ur.Favorite = 1 " : " ")+sortString+" limit "+startIndex+",10"));
        parameters.add(addParameter("return_cols", "RecipeKey,PinterestId,RecipeName,MealType,CuisineType,RecipeImage,Favorite,Rating,LastEdited,Username"));
        return getResults(parameters);
    }
    public JSONResult getUserRecipe(String username, String recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select * from UserRecipes where Username = '"+username+"' and RecipeKey = " + recipeKey));
        parameters.add(addParameter("return_cols", "Username,RecipeKey,RecipeEditKey,Favorite,Rating"));
        return getResults(parameters);
    }
    public boolean insertUserRecipe(String username, String recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "insert into UserRecipes (Username,RecipeKey,RecipeEditKey,Favorite,Rating) values('"+ username +"'," + recipeKey + ",0,0,0)"));
        return insert(parameters);
    }
    public boolean updateUserRecipeEditKey(String username, String recipeKey, int recipeEditKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "update UserRecipes set RecipeEditKey = "+recipeEditKey+" where Username = '"+username+"' and RecipeKey = " +recipeKey));
        return insert(parameters);
    }
    public boolean updateRecipeFavorite(String username, String recipeKey, boolean favorite){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "update UserRecipes set Favorite = " + (favorite ? 1 : 0) + " where Username = '"+username+"' and RecipeKey = " + recipeKey));
        return insert(parameters);
    }
    public boolean updateUserRecipeRating(String username, String recipeKey, double rating){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "update UserRecipes set Rating = " + rating + " where Username = '"+username+"' and RecipeKey = " + recipeKey));
        return insert(parameters);
    }
    public boolean deleteUserRecipe(String username, String recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "delete from UserRecipes where Username = '"+username+"' and RecipeKey = " + recipeKey));
        return insert(parameters);
    }

    /*-----------------------------------*
     * Ingredient Queries
     *-----------------------------------*/
    public JSONResult getIngredientByName(String ingredientName){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select * from Ingredients where IngredientName = '" + ingredientName + "'"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife"));
        return getResults(parameters);
    }
    public JSONResult getIngredient(int ingredientKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select * from Ingredients where IngredientKey = '" + ingredientKey + "'"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife"));
        return getResults(parameters);
    }
    public JSONResult getAllIngredients(){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select * from Ingredients"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife"));
        return getResults(parameters);
    }
    public JSONResult getIngredientsFilter(String filter){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select * from Ingredients where IngredientName like '%"+ filter +"%'"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife"));
        return getResults(parameters);
    }
    public int insertIngredient(String ingredientName, String ingredientType, int shelfLife){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "insert into Ingredients (IngredientName,IngredientType,ShelfLife) values('" + ingredientName.replace("'", "''") + "','" + ingredientType + "'," + shelfLife + ")"));
        return insertReturnKey(parameters);
    }
    public boolean editIngredient(String ingredientName, String ingredientType, int shelfLife, int ingredientKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "update Ingredients set IngredientName = '" + ingredientName.replace("'", "''") + "', IngredientType = '" + ingredientType + "', ShelfLife = " + shelfLife + " where IngredientKey = " + ingredientKey));
        return insert(parameters);
    }

    /*-----------------------------------*
     * RecipeToIngredient Queries
     *-----------------------------------*/
    public boolean insertRecipeToIngredient(int recipeKey, int ingredientKey, double ingredientAmount, String ingredientUnit, String preparation1, String preparation2, boolean optional){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "insert into RecipeToIngredient (RecipeKey,IngredientKey,IngredientAmount,IngredientUnit,Preparation1,Preparation2,Optional)" +
                                                 "values(" + recipeKey + "," + ingredientKey + "," + ingredientAmount + ",'" + ingredientUnit + "','" + preparation1 + "','" + preparation2 + "'," + (optional ? 1 : 0) + ")"));
        return insert(parameters);
    }
    public JSONResult getRecipeIngredients(int recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select i.IngredientKey, i.IngredientName, i.IngredientType, i.ShelfLife, ri.IngredientAmount, ri.IngredientUnit, ri.Preparation1, ri.Preparation2 from RecipeToIngredient ri, Ingredients i where ri.RecipeKey = " +recipeKey+ " and i.IngredientKey = ri.ingredientKey"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife,IngredientAmount,IngredientUnit,Preparation1,Preparation2"));
        return getResults(parameters);
    }
    public JSONResult getRecipeIngredient(int recipeKey, String ingredientName){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select i.IngredientKey, i.IngredientName, i.IngredientType, i.ShelfLife, ri.IngredientAmount, ri.IngredientUnit, ri.Preparation1, ri.Preparation2 from RecipeToIngredient ri, Ingredients i where ri.RecipeKey = " +recipeKey+ " and i.IngredientKey = ri.ingredientKey and i.IngredientName = '" + ingredientName + "'"));
        parameters.add(addParameter("return_cols", "IngredientKey,IngredientName,IngredientType,ShelfLife,IngredientAmount,IngredientUnit,Preparation1,Preparation2"));
        return getResults(parameters);
    }
    public boolean updateRecipeToIngredient(int recipeKey, int ingredientKey, double ingredientAmount, String ingredientUnit){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "update RecipeToIngredient set IngredientAmount = "+ ingredientAmount +", IngredientUnit = '"+ ingredientUnit +"' where RecipeKey = "+ recipeKey +" and IngredientKey = " +ingredientKey));
        return insert(parameters);
    }
    public boolean deleteRecipeToIngredient(int recipeKey, int ingredientKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "delete from RecipeToIngredient where RecipeKey = "+ recipeKey +" and IngredientKey = " +ingredientKey));
        return insert(parameters);
    }

    /*-----------------------------------
     * User Recipe to Ingredient Queries
     *-----------------------------------*/
    public JSONResult getUserRecipeIngredients(String username, int recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select i.IngredientKey, i.IngredientName, i.IngredientType, i.ShelfLife, ri.IngredientAmount, ri.IngredientUnit, ri.Preparation1, ri.Preparation2, ri.RemoveIngredient from UserRecipeToIngredient ri, Ingredients i where ri.Username = '"+username+"' and ri.RecipeKey = " +recipeKey+ " and i.IngredientKey = ri.ingredientKey"));
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
    public boolean deleteUserRecipeToIngredients(String username, String recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "delete from UserRecipeToIngredient where Username = '"+username+"' and RecipeKey = "+ recipeKey));
        return insert(parameters);
    }

    /*-----------------------------------
     * Calendar Recipes Queries
     *-----------------------------------*/
    public boolean insertCalendarRecipe(String username, Date calendarDate, String mealType, int sequence, String recipeKey, int groceryListKey, boolean mealCompleted){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "insert into UserCalendarRecipes (Username,CalendarDate,MealType,Sequence,RecipeKey,GroceryListKey,MealCompleted) values('"+username+"','" + dateString(calendarDate) + "','" + mealType + "'," + sequence + ","+ recipeKey+ ","+ groceryListKey + "," + mealCompleted + ")"));
        return insert(parameters);
    }
    public JSONResult getCalendarRecipes(String username, Date calendarDate){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select cr.CalendarDate, cr.MealType, cr.Sequence, cr.RecipeKey, coalesce(e.RecipeName, r.RecipeName) as RecipeName, cr.MealCompleted from UserCalendarRecipes cr, Recipes r left join UserRecipes u on u.Username = '"+username+"' and u.RecipeKey = r.RecipeKey and u.RecipeEditKey <> 0 left join UserEditRecipes e on e.RecipeEditKey = u.RecipeEditKey and e.RecipeKey = u.RecipeKey and e.Username = u.Username where cr.Username = '"+username+"' and cr.CalendarDate = '"+ dateString(calendarDate) +"' and r.RecipeKey = cr.RecipeKey"));
        parameters.add(addParameter("return_cols", "CalendarDate,MealType,Sequence,RecipeKey,RecipeName,MealCompleted"));
        return getResults(parameters);
    }
    public JSONResult getRecipeLastMade(String username, String recipeKey){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select MAX(CalendarDate) as LastMade from UserCalendarRecipes where Username = '"+username+"' and RecipeKey = "+ recipeKey));
        parameters.add(addParameter("return_cols", "LastMade"));
        return getResults(parameters);
    }
    public JSONResult getMonthCalendarRecipes(String username, Date beginDate, Date endDate){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select cr.CalendarDate, cr.MealType, cr.Sequence, cr.RecipeKey, coalesce(e.RecipeName, r.RecipeName) as RecipeName, cr.MealCompleted from UserCalendarRecipes cr, Recipes r left join UserRecipes u on u.Username = '"+username+"' and u.RecipeKey = r.RecipeKey and u.RecipeEditKey <> 0 left join UserEditRecipes e on e.RecipeEditKey = u.RecipeEditKey and e.RecipeKey = u.RecipeKey and e.Username = u.Username where cr.Username = '"+username+"' and cr.CalendarDate >= '"+ dateString(beginDate) +"' and cr.CalendarDate <= '"+ dateString(endDate) +"' and r.RecipeKey = cr.RecipeKey order by cr.CalendarDate,cr.Sequence"));
        parameters.add(addParameter("return_cols", "CalendarDate,MealType,Sequence,RecipeKey,RecipeName,MealCompleted"));
        return getResults(parameters);
    }
    public JSONResult getRecipeSchedule(String username, String recipeKey, Date fromDate){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select cr.CalendarDate, cr.MealType, cr.Sequence, cr.MealCompleted, r.RecipeKey, r.RecipeName from UserCalendarRecipes cr, Recipes r where cr.Username = '"+username+"' and cr.CalendarDate >= '"+ dateString(fromDate) +"'  and cr.RecipeKey = "+recipeKey+" and r.RecipeKey = cr.RecipeKey order by cr.CalendarDate,cr.Sequence"));
        parameters.add(addParameter("return_cols", "CalendarDate,MealType,Sequence,RecipeKey,MealCompleted"));
        return getResults(parameters);
    }
    public boolean deleteCalendarRecipe(String username, Date calendarDate, String recipeKey) {
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "delete from UserCalendarRecipes where Username = '"+username+"' and CalendarDate = '"+dateString(calendarDate)+"' and RecipeKey = " + recipeKey));
        return insert(parameters);
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

    public JSONResult getIngredientsForGroceryList(String username, Date calendarDateStart, Date calendarDateEnd){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query","Select i.IngredientType, coalesce(ur.IngredientAmount, ri.IngredientAmount) as IngredientAmount, coalesce(ur.IngredientUnit, ri.IngredientUnit) as IngredientUnit, i.IngredientName, i.IngredientKey, coalesce(ur.RemoveIngredient, 0) as RemoveIngredient from UserCalendarRecipes cr, RecipeToIngredient ri left join UserRecipeToIngredient ur on ur.Username = '"+username+"' and ur.RecipeKey = ri.RecipeKey and ur.IngredientKey = ri.IngredientKey, Ingredients i where cr.Username = '"+username+"' and cr.CalendarDate >= '"+dateString(calendarDateStart)+"' and cr.CalendarDate <= '"+dateString(calendarDateEnd)+"' and ri.RecipeKey = cr.RecipeKey and i.IngredientKey = ri.IngredientKey " +
                "union all Select i.IngredientType, ur.IngredientAmount, ur.IngredientUnit, i.IngredientName, i.IngredientKey, ur.RemoveIngredient from UserCalendarRecipes cr, UserRecipeToIngredient ur, Ingredients i where cr.Username = '"+username+"' and cr.CalendarDate >= '"+dateString(calendarDateStart)+"' and cr.CalendarDate <= '"+dateString(calendarDateEnd)+"' and ur.Username = cr.Username and ur.RecipeKey = cr.RecipeKey and ur.RemoveIngredient = 0 and i.IngredientKey = ur.IngredientKey and not exists(select 1 from RecipeToIngredient where RecipeKey = ur.RecipeKey and IngredientKey = ur.IngredientKey)"));
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
        parameters.add(addParameter("sql_query", "select Username,Email,FirstName,LastName from Users where Username = '"+ username +"'"));
        parameters.add(addParameter("return_cols", "Username,Email,FirstName,LastName"));
        return getResults(parameters);
    }
    public JSONResult getUserByEmail(String email){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select Username,Email,FirstName,LastName from Users where Email = '"+ email +"'"));
        parameters.add(addParameter("return_cols", "Username,Email,FirstName,LastName"));
        return getResults(parameters);
    }
    public JSONResult getUserPasswordByUsername(String username){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select Username,Email,Password,FirstName,LastName from Users where Username = '"+ username +"'"));
        parameters.add(addParameter("return_cols", "Username,Email,Password,FirstName,LastName"));
        return getResults(parameters);
    }
    public JSONResult getUserPasswordByEmail(String email){
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "select Username,Email,Password,FirstName,LastName from Users where Email = '"+ email +"'"));
        parameters.add(addParameter("return_cols", "Username,Email,Password,FirstName,LastName"));
        return getResults(parameters);
    }
    public boolean insertUser(String username, String email, String password, String firstName, String lastName) {
        ArrayList<ArrayList<String>> parameters = new ArrayList<>();
        parameters.add(addParameter("sql_query", "insert into Users (Username,Email,Password,FirstName,LastName) values('"+username+"','"+email+"','"+password+"','"+firstName+"','"+lastName+"')"));
        return insert(parameters);
    }
}
