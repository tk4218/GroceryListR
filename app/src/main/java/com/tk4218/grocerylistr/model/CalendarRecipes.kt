package com.tk4218.grocerylistr.model

import android.util.Log
import com.google.firebase.database.*
import com.tk4218.grocerylistr.fragments.CalendarLoadedCallback
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/*
 * Created by Tk4218 on 10/11/2017.
 */

data class CalendarRecipes(private var mUsername: String, var calendarDate: Date?, var recipes: ArrayList<Recipe>) {
    companion object {
        private var dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        fun addRecipe(userName: String, calendarDate: Date, recipeKey: String) {
            val database = FirebaseDatabase.getInstance()
            val recipeRef = FirebaseDatabase.getInstance().getReference("recipe/$recipeKey")

            recipeRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.value == null) return

                    val recipe = dataSnapshot.getValue(Recipe::class.java)
                    val calendarRef = database.getReference("$userName/calendar/${dateFormat.format(calendarDate)}")

                    calendarRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val type = object: GenericTypeIndicator<HashMap<String, Recipe>>() {}
                            var recipes = dataSnapshot.getValue(type)
                            if(recipes == null) recipes = HashMap()
                            recipes[recipe!!.recipeKey] = recipe
                            calendarRef.setValue(recipes)
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e("CalendarRecipes", "Unable to Retrieve Calendar Recipes for $userName, ${dateFormat.format(calendarDate)}")
                        }
                    })
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("CalendarRecipes", "Unable to Retrieve Calendar Recipes for $userName, ${dateFormat.format(calendarDate)}")
                }
            })
        }

        fun removeRecipe(userName: String, calendarDate: Date, recipeKey: String) {
            val calendarRef = FirebaseDatabase.getInstance().getReference(userName).child("calendar").child(dateFormat.format(calendarDate)).child(recipeKey)
            calendarRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.value != null)
                        dataSnapshot.ref.removeValue()
                }
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }

        fun getCalendar(userName: String, beginDate: Date, endDate: Date, calendarLoadedCallback: CalendarLoadedCallback){
            val startDate = clearDateTime(beginDate)
            val stopDate = clearDateTime(endDate)
            val calendarMap: HashMap<Date, CalendarRecipes> = HashMap()
            val date = Calendar.getInstance()
            date.time = startDate
            while(date.time < stopDate) {
                calendarMap[date.time] = CalendarRecipes(userName, date.time, ArrayList())
                date.add(Calendar.DAY_OF_MONTH, 1)
            }

            val calendarRef = FirebaseDatabase.getInstance().getReference("$userName/calendar")
            val query = calendarRef.orderByKey()
                            .startAt(dateFormat.format(startDate))
                            .endAt(dateFormat.format(stopDate))
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.value != null){
                        val type = object: GenericTypeIndicator<HashMap<String, Recipe>>() {}
                        for(child in dataSnapshot.children)
                            calendarMap[dateFormat.parse(child.key)]!!.recipes = ArrayList(child.getValue(type)!!.values)
                    }
                    val calendarRecipes: ArrayList<CalendarRecipes> = ArrayList()
                    for(item in calendarMap.toSortedMap().values)
                        calendarRecipes.add(item)
                    calendarLoadedCallback.onCalendarLoaded(calendarRecipes)
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
        }
        private fun clearDateTime(date: Date): Date {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.time
        }
    }
}


