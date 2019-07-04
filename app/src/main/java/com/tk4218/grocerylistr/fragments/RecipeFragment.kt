package com.tk4218.grocerylistr.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tk4218.grocerylistr.adapters.RecipeAdapter
import com.tk4218.grocerylistr.model.ApplicationSettings
import com.tk4218.grocerylistr.model.Recipe
import com.tk4218.grocerylistr.model.UpdatePinterestRecipes
import com.tk4218.grocerylistr.R
import com.tk4218.grocerylistr.model.User

import kotlinx.android.synthetic.main.fragment_recipe.*

import java.util.ArrayList

class RecipeFragment : Fragment() {
    private var mSettings: ApplicationSettings? = null

    private var mShowUserRecipes: Boolean = false
    private var mShowFavorites: Boolean = false
    private var mRecipeSort: String? = null
    private var mLastIndex: String? = null
    private var mAdapter: RecipeAdapter? = null
    private var mRecipes: ArrayList<Recipe>? = null

    private var mLoadingRecipes: Boolean = false
    private var mAllRecipesLoaded: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mSettings = ApplicationSettings(activity)
        return inflater.inflate(R.layout.fragment_recipe, container, false)
    }

    override fun onResume() {
        super.onResume()
        /*-------------------------------------------------------------------
         * Retrieving Recipes from the database. Doing it in onResume
         * guarantees the list will be updated upon returning to the fragment.
         *-------------------------------------------------------------------*/
        if (mSettings!!.isPinterestLoggedIn) {
            val updatePinterestRecipes = UpdatePinterestRecipes()
            updatePinterestRecipes.execute(User.currentUsername(activity!!))
        }

        /*--------------------------------
        *  Set recipes on grid view
        *--------------------------------*/
        mShowUserRecipes = true
        mRecipeSort = "recipeName"
        mRecipes = ArrayList()
        recipeGridView.layoutManager = GridLayoutManager(context, 2)
        mAdapter = RecipeAdapter(context, mRecipes)
        recipeGridView.adapter = mAdapter

        recipeGridView.setOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = recyclerView.childCount
                val totalItemCount = recipeGridView.layoutManager!!.itemCount
                val firstVisibleItem = (recipeGridView.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
                val visibleThreshold = 6

                val loadMore = totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold

                if (loadMore && !mLoadingRecipes && !mAllRecipesLoaded) {
                    retrieveRecipes(false)
                }
            }
        })

        refresh_recipes!!.setColorSchemeColors(Color.RED)
        refresh_recipes!!.setOnRefreshListener { retrieveRecipes(true) }
        retrieveRecipes(true)
    }

    fun filterRecipes(filterString: String) {
        if (mAdapter != null)
            mAdapter!!.filter.filter(filterString)
    }

    fun toggleRecipeList(showUserRecipes: Boolean, recipeSort: String, favorites: Boolean) {
        if (favorites) {
            mShowUserRecipes = true
        } else {
            mShowUserRecipes = showUserRecipes
        }
        mShowFavorites = favorites
        mRecipeSort = recipeSort
        recipe_loading!!.visibility = View.VISIBLE
        retrieveRecipes(true)
    }

    private fun retrieveRecipes(clearRecipes: Boolean) {
        if (clearRecipes) {
            mRecipes!!.clear()
            mAllRecipesLoaded = false
            mLastIndex = ""
        }

        mLoadingRecipes = true
        val recipeRef = FirebaseDatabase.getInstance().reference
        val recipes = recipeRef.child("recipe").limitToFirst(10).orderByChild(mRecipeSort!!)
        if (mLastIndex != "") recipes.startAt(mLastIndex)
        recipes.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.childrenCount < 10)
                    mAllRecipesLoaded = true

                for (recipe in dataSnapshot.children) {
                    mRecipes!!.add(recipe.getValue(Recipe::class.java)!!)
                    setLastIndex(recipe.getValue(Recipe::class.java))
                }

                mAdapter!!.notifyDataSetChanged()
                refresh_recipes!!.isRefreshing = false
                recipe_loading!!.visibility = View.GONE
                mLoadingRecipes = false
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun setLastIndex(recipe: Recipe?) {
        when (mRecipeSort) {
            "recipeName" -> mLastIndex = recipe!!.recipeName
            "" -> {
            }
        }
    }

    companion object {
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(): RecipeFragment {
            return RecipeFragment()
        }
    }

}