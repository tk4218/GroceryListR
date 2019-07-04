package com.tk4218.grocerylistr.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView

import com.tk4218.grocerylistr.adapters.GroceryListAdapter
import com.tk4218.grocerylistr.adapters.IngredientDropdownAdapter
import com.tk4218.grocerylistr.model.GroceryList
import com.tk4218.grocerylistr.model.Ingredient
import com.tk4218.grocerylistr.R

import com.tk4218.grocerylistr.model.GroceryList.GroceryListLoadedCallback
import com.tk4218.grocerylistr.customlayout.NewIngredientDialog
import com.tk4218.grocerylistr.customlayout.NewIngredientDialog.IngredientAddedListener

import kotlinx.android.synthetic.main.activity_grocery_list.*

class GroceryListFragment : Fragment(), GroceryListLoadedCallback, IngredientAddedListener {
    private var mGroceryListKey: String = ""
    private var mGroceryList: GroceryList? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_grocery_list, container, false)
    }

    override fun onResume() {
        super.onResume()

        add_item_layout.visibility = View.INVISIBLE
        undo.visibility = View.INVISIBLE

        val adapter = IngredientDropdownAdapter(context, R.layout.dropdown_ingredient)
        add_item_name.setAdapter(adapter)

        add_item_name.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedIngredient = parent.adapter.getItem(position) as String
            if (selectedIngredient == "+ New Ingredient") {
                add_item_name!!.setText("")
                val newIngredientDialog = NewIngredientDialog(context!!, layoutInflater, "")
                newIngredientDialog.setOnIngredientAddedListener(this)
                newIngredientDialog.show()
            }
        }

        add_item.setOnClickListener {
            if (add_item_layout.visibility == View.INVISIBLE) {
                add_item_layout.visibility = View.VISIBLE
                undo!!.visibility = View.VISIBLE
                add_item.setImageResource(android.R.drawable.checkbox_on_background)

                add_item_amount.setText("1")
                val measurements = resources.getStringArray(R.array.measurements)
                for (i in measurements.indices) {
                    if (measurements[i].toLowerCase() == "count")
                        add_item_measurement.setSelection(i)
                }
                add_item_name.setText("")
            } else {
                val ingredient = Ingredient()
                ingredient.ingredientName = add_item_name.text.toString()
                ingredient.ingredientAmount = java.lang.Double.parseDouble(add_item_amount.text.toString())
                ingredient.ingredientUnit = add_item_measurement.selectedItem.toString()
                mGroceryList?.addGroceryListItem(ingredient, true)

                add_item_layout.visibility = View.INVISIBLE
                undo!!.visibility = View.INVISIBLE
                add_item.setImageResource(android.R.drawable.ic_input_add)
            }
        }

        undo.setOnClickListener {
            undo.visibility = View.INVISIBLE
            add_item_layout.visibility = View.INVISIBLE
            add_item.setImageResource(android.R.drawable.ic_input_add)
        }

        GroceryList.getGroceryList(context!!, mGroceryListKey, this)
    }

    override fun onGroceryListLoaded(groceryList: GroceryList?) {
        if(groceryList != null){
            mGroceryList = groceryList
            val adapter = GroceryListAdapter(context, mGroceryList!!.ingredientTypes, mGroceryList)
            list_grocerylist.setAdapter(adapter)
            if (adapter.groupCount > 0)
                list_grocerylist.expandGroup(0)
        } else {

        }
    }

    override fun onIngredientAdded(ingredient: Ingredient) {
        add_item_name.setText(ingredient.ingredientName)
    }

    companion object {
        fun newInstance(groceryListKey: String): GroceryListFragment {
            return GroceryListFragment().also {
                it.mGroceryListKey = groceryListKey
            }
        }
    }
}
