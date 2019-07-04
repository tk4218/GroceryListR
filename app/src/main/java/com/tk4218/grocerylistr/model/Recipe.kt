package com.tk4218.grocerylistr.model

import android.content.Context
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import android.util.Log

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Exclude
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tk4218.grocerylistr.BR
import com.tk4218.grocerylistr.R

import java.util.ArrayList

/*
 * Created by tk4218 on 4/30/2017.
 */
class Recipe() : BaseObservable() {
    var recipeKey: String = ""
    var pinterestId: String = ""
    var isUserRecipe: Boolean = false
    var isUserEdited: Boolean = false
    var ingredients: ArrayList<Ingredient> = ArrayList()

    private var mealTypes = arrayOf<String>()
    private var cuisineTypes = arrayOf<String>()

    constructor(context: Context) : this() {
        mealTypes = context.resources.getStringArray(R.array.meal_types)
        cuisineTypes = context.resources.getStringArray(R.array.cuisine_type)
    }

    @get:Bindable
    var recipeName: String = ""
        set(recipeName) {
            if (recipeName != this.recipeName) {
                field = recipeName
                notifyPropertyChanged(BR.recipeName)
            }
        }
    @get:Bindable
    var recipeImage: String = ""
        set(recipeImage) {
            if (recipeImage != this.recipeImage) {
                field = recipeImage
                notifyPropertyChanged(BR.recipeImage)
            }
        }

    @get:Bindable
    var favorite: Boolean = false
        set(favorite) {
            if (favorite != this.favorite) {
                field = favorite
                notifyPropertyChanged(BR.favorite)
            }
        }
    @get:Bindable
    var rating: Double = 0.toDouble()
        set(rating) {
            if (rating != this.rating) {
                field = rating
                notifyPropertyChanged(BR.rating)
            }
        }
    @get:Bindable
    var ratingCount: Int = 0
        set(ratingCount) {
            if (ratingCount != this.ratingCount) {
                field = ratingCount
                notifyPropertyChanged(BR.ratingCount)
            }
        }
    @get:Bindable
    var userRating: Double = 0.toDouble()
        set(userRating) {
            if (userRating != this.userRating) {
                field = userRating
                notifyPropertyChanged(BR.userRating)
            }
        }
    @get:Bindable
    var mealType: String = ""
        set(mealType) {
            if (mealType != this.mealType) {
                field = mealType
                notifyPropertyChanged(BR.mealType)
            }
        }
    @get:Bindable
    @Exclude
    var mealTypeSpinnerPosition: Int = 0
        set(mealTypeSpinnerPosition) {
            field = mealTypeSpinnerPosition
            if(mealType.isNotEmpty())
                mealType = mealTypes[mealTypeSpinnerPosition]
        }
    @get:Bindable
    var cuisineType: String = ""
        set(cuisineType) {
            if (cuisineType != field) {
                field = cuisineType
                notifyPropertyChanged(BR.cuisineType)
            }
        }
    @get:Bindable
    @Exclude
    var cuisineTypeSpinnerPosition: Int = 0
        set(cuisineTypeSpinnerPosition) {
            field = cuisineTypeSpinnerPosition
            if(cuisineTypes.isNotEmpty())
                cuisineType = cuisineTypes[cuisineTypeSpinnerPosition]
        }

    fun save(): Boolean {
        val database = FirebaseDatabase.getInstance()
        val recipeRef = database.getReference("recipe")

        if (recipeKey != "") {
            recipeRef.child(recipeKey).setValue(this)
        } else {
            val newRecipeRef = recipeRef.push()
            recipeKey = newRecipeRef.key ?: ""
            newRecipeRef.setValue(this)
        }
        return true
    }

    companion object {
        fun getRecipe(recipeKey: String): Recipe {
            Log.i("Recipe", "Retrieving recipe: $recipeKey")
            val recipe = arrayOfNulls<Recipe>(1)
            val database = FirebaseDatabase.getInstance()
            val recipeRef = database.getReference("recipe/$recipeKey")
            recipeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.i("Recipe", "Recipe Found!")
                    recipe[0] = dataSnapshot.getValue(Recipe::class.java)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Recipe Error", "Unable to Retrieve Recipe: $recipeKey")
                }
            })
            return recipe[0]!!
        }
    }
}

