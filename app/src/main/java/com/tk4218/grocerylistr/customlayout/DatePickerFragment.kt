package com.tk4218.grocerylistr.customlayout

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.widget.DatePicker

import com.tk4218.grocerylistr.model.CalendarRecipes
import com.tk4218.grocerylistr.model.User

import java.util.Calendar

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private var mRecipeKey: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val arguments = arguments
        if (arguments != null)
            mRecipeKey = arguments.getString("recipeKey")

        // Create a new instance of DatePickerDialog and return it
        val c = Calendar.getInstance()
        return DatePickerDialog(activity!!, this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, monthOfYear, dayOfMonth)
        CalendarRecipes.addRecipe(User.currentUsername(context!!), calendar.time, mRecipeKey!!)
    }
}