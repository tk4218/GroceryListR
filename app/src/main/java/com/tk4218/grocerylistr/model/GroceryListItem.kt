package com.tk4218.grocerylistr.model

import com.google.firebase.database.FirebaseDatabase
import java.util.*

class GroceryListItem() {
    var username: String = ""
    var groceryListItemKey: String = ""
    var groceryListKey: String = ""
    var ingredient: Ingredient = Ingredient()
    private val mIngredientAmount = MeasurementConverter()
    var addedToCart: Boolean = false

    var ingredientAmount: Double
        get() = mIngredientAmount.measurementAmount
        set(amount) = mIngredientAmount.setmMeasurementAmount(amount)

    val formattedIngredientAmount: String
        get() = toFraction(ingredientAmount, 10)

    var ingredientUnit: String
        get() = mIngredientAmount.measurementUnit
        set(ingredientUnit) = mIngredientAmount.setMesurementUnit(ingredientUnit)

    constructor(username: String, groceryListKey: String, ingredient: Ingredient): this(){
        this.username = username
        this.groceryListKey = groceryListKey
        this.ingredient = ingredient
        this.ingredientAmount = ingredient.ingredientAmount
        this.ingredientUnit = ingredient.ingredientUnit
        groceryListItemKey = UUID.randomUUID().toString()
    }

    fun addIngredientAmount(amount: Double, unit: String): Boolean {
        return mIngredientAmount.add(amount, unit)
    }

    fun save() {
        val groceryListItemRef = FirebaseDatabase.getInstance().getReference("$username/groceryList/$groceryListKey/$groceryListItemKey")
        groceryListItemRef.setValue(this)
    }

    private fun toFraction(d: Double, factor: Int): String {
        var d = d
        val sb = StringBuilder()
        if (d < 0) {
            sb.append('-')
            d = -d
        }
        val l = d.toLong()
        if (l != 0L) sb.append(l)
        d -= l.toDouble()
        var error = Math.abs(d)
        var bestDenominator = 1
        for (i in 2..factor) {
            val error2 = Math.abs(d - Math.round(d * i).toDouble() / i)
            if (error2 < error) {
                error = error2
                bestDenominator = i
            }
        }
        if (bestDenominator > 1) {
            if (l != 0L) sb.append(' ')
            sb.append(Math.round(d * bestDenominator)).append('/').append(bestDenominator)
        }
        return sb.toString()
    }

}
