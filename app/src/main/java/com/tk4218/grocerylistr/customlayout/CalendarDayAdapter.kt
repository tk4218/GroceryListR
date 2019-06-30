package com.tk4218.grocerylistr.customlayout

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.tk4218.grocerylistr.model.Recipe
import com.tk4218.grocerylistr.R
import com.tk4218.grocerylistr.RecipeActivity
import com.tk4218.grocerylistr.model.CalendarRecipes
import com.tk4218.grocerylistr.model.User

import java.util.ArrayList
import java.util.Date

class CalendarDayAdapter internal constructor(private val mContext: Context?, private val mRecipes: ArrayList<Recipe>, private val mCalendarDate: Date) : BaseAdapter() {
    override fun getCount(): Int {
        return mRecipes.size
    }

    override fun getItem(position: Int): Recipe {
        return mRecipes[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.listview_day_meals, parent, false)
        }

        val recipeName = view!!.findViewById<TextView>(R.id.list_day_meal)
        recipeName.text = mRecipes[position].recipeName
        recipeName.setOnClickListener {
            val intent = Intent(mContext, RecipeActivity::class.java)
            intent.putExtra("recipeKey", mRecipes[position].recipeKey)
            mContext?.startActivity(intent)
        }

        recipeName.setOnLongClickListener {
            deleteMealPlan(mRecipes[position], position)
            true
        }

        return view
    }

    private fun deleteMealPlan(recipe: Recipe, position: Int) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("Un-schedule Recipe from Calendar")
                .setMessage("Are you sure you want to remove " + recipe.recipeName + " from your calendar?")
                .setPositiveButton("Remove") { _, _ ->
                    CalendarRecipes.removeRecipe(User.currentUsername(mContext!!), mCalendarDate, recipe.recipeKey)
                    mRecipes.removeAt(position)
                    notifyDataSetChanged()
                }
                .setNegativeButton("Cancel", null)
                .show()
    }
}
