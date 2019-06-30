package com.tk4218.grocerylistr.model

import android.content.Context
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.firebase.database.*

import com.tk4218.grocerylistr.BR
import com.tk4218.grocerylistr.R
import kotlin.math.abs
import kotlin.math.roundToInt

class Ingredient() : BaseObservable() {
    var ingredientKey: String? = null
    private var mIngredientUnits = arrayOf<String>()
    private var mIngredientTypes = arrayOf<String>()

    constructor(context: Context): this() {
        mIngredientUnits = context.resources.getStringArray(R.array.measurements)
        mIngredientTypes = context.resources.getStringArray(R.array.ingredient_type)
    }

    @get:Bindable
    var ingredientName: String? = null
        set(ingredientName) {
            if (ingredientName != this.ingredientName) {
                field = ingredientName
                notifyPropertyChanged(BR.ingredientName)
            }
        }

    @get:Bindable
    var ingredientType: String? = null
        set(ingredientType) {
            if (ingredientType != this.ingredientType) {
                field = ingredientType
                notifyPropertyChanged(BR.ingredientType)
            }
        }

    private var shelfLifeInterval: Int = 0
    @get:Bindable
    var ingredientAmount: Double = 0.toDouble()
        set(ingredientAmount) {
            if (this.ingredientAmount != ingredientAmount) {
                field = ingredientAmount
                notifyPropertyChanged(BR.ingredientAmount)
            }
        }

    var preparation1: String? = null
        set(preparation1) {
            if (this.preparation1 != preparation1) {
                field = preparation1
            }
        }

    @get:Bindable
    var ingredientTypePosition: Int = 0
        set(position) {
            field = position
            if(mIngredientTypes.isNotEmpty())
                ingredientType = mIngredientTypes[position]
            notifyPropertyChanged(BR.ingredientTypePosition)
        }

    @get:Bindable
    var shelfLife: Int = 0
        set(shelfLife) {
            if (field != shelfLife) {
                field = shelfLife
                notifyPropertyChanged(BR.shelfLife)
            }
        }

    var shelfLifeText: String
        get() = shelfLife.toString()
        @Bindable
        set(shelfLifeText) {
            if (shelfLifeText.isNotEmpty() && Integer.parseInt(shelfLifeText) != shelfLife) {
                shelfLife = Integer.parseInt(shelfLifeText)
                notifyPropertyChanged(BR.shelfLifeText)
            }
        }

    @Exclude
    @get:Bindable
    var shelfLifeIntervalPosition: Int = 0
        set(position) {
            field = position
            when (position) {
                0 -> shelfLifeInterval = 1
                1 -> shelfLifeInterval = 7
                2 -> shelfLifeInterval = 30
            }
            notifyPropertyChanged(BR.shelfLifeIntervalPosition)
        }

    val formattedIngredientAmount: String
        get() = toFraction(ingredientAmount, 10)

    @get:Bindable
    var ingredientUnit: String = ""
        set(ingredientUnit) {
            if (ingredientUnit != field) {
                field = ingredientUnit
                notifyPropertyChanged(BR.ingredientUnit)
            }
        }

    @Exclude
    @get:Bindable
    var ingredientUnitPosition: Int = 0
        set(ingredientUnitPosition) {
            if (field != ingredientUnitPosition) {
                field = ingredientUnitPosition
                if(mIngredientUnits.isNotEmpty())
                    ingredientUnit = mIngredientUnits[ingredientUnitPosition]
                notifyPropertyChanged(BR.ingredientUnitPosition)
            }
        }

    fun save(): Boolean {
        val database = FirebaseDatabase.getInstance()
        val ingredientRef = database.getReference("ingredient")

        if (ingredientKey != null) {
            ingredientRef.child(ingredientKey!!).setValue(this)
        } else {
            val newIngredientRef = ingredientRef.push()
            ingredientKey = newIngredientRef.key
            newIngredientRef.setValue(this)
        }
        return true
    }

    private fun toFraction(number: Double, factor: Int): String {
        var d = number
        val sb = StringBuilder()
        if (d < 0) {
            sb.append('-')
            d = -d
        }
        val l = d.toLong()
        if (l != 0L) sb.append(l)
        d -= l.toDouble()
        var error = abs(d)
        var bestDenominator = 1
        for (i in 2..factor) {
            val error2 = abs(d - (d * i).roundToInt().toDouble() / i)
            if (error2 < error) {
                error = error2
                bestDenominator = i
            }
        }
        if (bestDenominator > 1) {
            if (l != 0L) sb.append(' ')
            sb.append((d * bestDenominator).roundToInt()).append('/').append(bestDenominator)
        }
        return sb.toString()
    }

    companion object {
        fun getIngredient(ingredientKey: String): Ingredient {
            val ingredient = arrayOfNulls<Ingredient>(1)
            val database = FirebaseDatabase.getInstance()
            val ingredientRef = database.getReference("recipe/$ingredientKey")
            ingredientRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    ingredient[0] = dataSnapshot.getValue(Ingredient::class.java)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Recipe Error", "Unable to Retrieve Recipe: $ingredientKey")
                }
            })
            return ingredient[0]!!
        }
    }
}
