package com.tk4218.grocerylistr.model

import android.content.Context
import com.google.firebase.database.*
import com.tk4218.grocerylistr.fragments.CalendarLoadedCallback
import java.util.Date
import kotlin.collections.ArrayList

/*
 * Created by Tk4218 on 10/14/2017.
 */

class GroceryList() {
    var username: String = ""
    var groceryListKey: String = ""
    var mealPlanDateStart: Date = Date()
    var mealPlanDateEnd: Date = Date()
    var groceryListCompleted: Boolean = false
    var completedDate: Date = Date(0)
    var current: Boolean = false
    var groceryListItems: HashMap<String, GroceryListItem> = HashMap()

    constructor(username: String, mealPlanDateStart: Date, mealPlanDateEnd: Date): this() {
        this.username = username
        this.mealPlanDateStart = mealPlanDateStart
        this.mealPlanDateEnd = mealPlanDateEnd
    }

    val groceryListItemsRemaining: Int
        get() {
            var itemsRemaining = 0
            for (item in groceryListItems.values) {
                if (!item.addedToCart) {
                    itemsRemaining++
                }
            }
            return itemsRemaining
        }

    val ingredientTypes: ArrayList<String>
        get() {
            val ingredientTypes = ArrayList<String>()
            for (items in groceryListItems.values) {
                if(!ingredientTypes.contains(items.ingredient.ingredientType)){
                    ingredientTypes.add(items.ingredient.ingredientType!!)
                }
            }
            return ingredientTypes
        }

    val sortedGroceryList: ArrayList<GroceryListItem>
        get() {
            return sortGroceryListItems()
        }

    fun addGroceryListItem(ingredient: Ingredient, save: Boolean) {
        for (item in groceryListItems.values) {
            if (item.ingredient.ingredientKey == ingredient.ingredientKey) {
                if (item.addIngredientAmount(ingredient.ingredientAmount, ingredient.ingredientUnit)) {
                    if(save) item.save()
                    return
                }
            }
        }
        val groceryListItem = GroceryListItem(username, groceryListKey, ingredient)
        groceryListItems[groceryListItem.groceryListItemKey] = groceryListItem
        if(save) groceryListItem.save()
    }

    fun removeGroceryListItem(groceryListItemKey: String): Boolean {
        if(!groceryListItems.containsKey(groceryListItemKey)) return false
        groceryListItems.remove(groceryListItemKey)
        return true
    }

    fun getGroceryListItems(ingredientType: String): ArrayList<GroceryListItem> {
        val filteredGroceryListItems = ArrayList<GroceryListItem>()
        for (item in groceryListItems.values) {
            if (item.ingredient.ingredientType == ingredientType) {
                filteredGroceryListItems.add(item)
            }
        }
        return filteredGroceryListItems
    }

    fun findIngredient(ingredientName: String, ingredientUnit: String): GroceryListItem? {
        for (item in groceryListItems.values) {
            if (item.ingredient.ingredientName == ingredientName && item.ingredientUnit == ingredientUnit) return item
        }
        return null
    }

    private fun sortGroceryListItems(): ArrayList<GroceryListItem> {
        val sortedItems = ArrayList<GroceryListItem>()
        val ingredientTypes = ingredientTypes

        for (ingredientType in ingredientTypes) {
            val items = getGroceryListItems(ingredientType)

            items.sortWith(Comparator { lhs, rhs -> lhs.ingredient.ingredientName!!.compareTo(rhs.ingredient.ingredientName!!) })
            sortedItems.addAll(items)
        }

        return sortedItems
    }

    companion object {
        fun getGroceryList(context: Context, groceryListKey: String, callback: GroceryListLoadedCallback) {
            val username = User.currentUsername(context)
            val groceryListQuery: Query
            if(groceryListKey.isNotEmpty()){
                groceryListQuery = FirebaseDatabase.getInstance().getReference("$username/groceryList/$groceryListKey")
            } else {
                groceryListQuery = FirebaseDatabase.getInstance().getReference("$username/groceryList").orderByChild("current").equalTo(true)
            }
            groceryListQuery.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val groceryList: GroceryList? = dataSnapshot.getValue(GroceryList::class.java)
                    callback.onGroceryListLoaded(groceryList)
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
        }
        fun generateGroceryList(context: Context, mealPlanDateStart: Date, mealPlanDateEnd: Date, createEmptyList: Boolean, callback: GroceryListCreatedCallback) {
            val username = User.currentUsername(context)
            val newGroceryList = GroceryList(username, mealPlanDateStart, mealPlanDateEnd).apply { current = true }
            val groceryListRef = FirebaseDatabase.getInstance().getReference("$username/groceryList")

            if (createEmptyList) {
                replaceCurrentGroceryList(username)
                val newGroceryListRef = groceryListRef.push()
                newGroceryList.groceryListKey = newGroceryListRef.key ?: ""
                newGroceryListRef.setValue(newGroceryList)
                callback.onGroceryListCreated(newGroceryList.groceryListKey)

            } else {
                val calendarRecipeCallback = object:  CalendarLoadedCallback {
                    override fun onCalendarLoaded(calendarRecipes: ArrayList<CalendarRecipes>) {
                        if(calendarRecipes.count() == 0) {
                            callback.onGroceryListCreated("")
                            return
                        }
                        for(calendarRecipe in calendarRecipes){
                            for(recipe in calendarRecipe.recipes){
                                for(ingredient in recipe.ingredients){
                                    newGroceryList.addGroceryListItem(ingredient, false)
                                }
                            }
                        }
                        newGroceryList.sortGroceryListItems()
                        replaceCurrentGroceryList(username)

                        val newGroceryListRef = groceryListRef.push()
                        newGroceryList.groceryListKey = newGroceryListRef.key ?: ""
                        newGroceryListRef.setValue(newGroceryList)
                        callback.onGroceryListCreated(newGroceryList.groceryListKey)
                    }
                }

                CalendarRecipes.getCalendar(username, mealPlanDateStart, mealPlanDateEnd, calendarRecipeCallback)
            }
        }
        private fun replaceCurrentGroceryList(username: String) {
            val groceryListRef = FirebaseDatabase.getInstance().getReference("$username/groceryList")
            val queryCurrent = groceryListRef.orderByChild("current").equalTo(true)
            queryCurrent.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.value != null) {
                        dataSnapshot.child("current").ref.setValue(false)
                    }
                }
                override fun onCancelled(dataSnapshot: DatabaseError) { }
            })
        }
    }

    interface GroceryListCreatedCallback {
        fun onGroceryListCreated(groceryListKey: String)
    }

    interface GroceryListLoadedCallback {
        fun onGroceryListLoaded(groceryList: GroceryList?)
    }

}
