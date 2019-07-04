package com.tk4218.grocerylistr.customlayout

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.tk4218.grocerylistr.R
import com.tk4218.grocerylistr.databinding.DialogNewIngredientBinding
import com.tk4218.grocerylistr.model.Ingredient

class NewIngredientDialog(context: Context, inflater: LayoutInflater, ingredientName: String): AlertDialog.Builder(context) {
    private var mListener: IngredientAddedListener? = null
    private val mIngredient = Ingredient()

    init {
        val binding = DataBindingUtil.inflate<DialogNewIngredientBinding>(inflater, R.layout.dialog_new_ingredient, null, false)
        mIngredient.ingredientName = ingredientName
        binding.ingredient = mIngredient

        setTitle("Add New Ingredient")
        setIcon(android.R.drawable.ic_input_add)
        setView(binding.root)

        setPositiveButton("Add") { _, _ ->
            mIngredient.save()
            mListener?.onIngredientAdded(mIngredient)
        }
        setNegativeButton("Cancel", null)
    }

    fun setOnIngredientAddedListener(listener: IngredientAddedListener) {
        mListener = listener
    }

    interface IngredientAddedListener {
        fun onIngredientAdded(ingredient: Ingredient)
    }
}