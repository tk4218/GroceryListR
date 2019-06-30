package com.tk4218.grocerylistr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.databinding.DataBindingUtil
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View

import com.tk4218.grocerylistr.adapters.AddIngredientAdapter
import com.tk4218.grocerylistr.Image.ImageManager
import com.tk4218.grocerylistr.model.ApplicationSettings

import java.io.IOException
import java.util.ArrayList

import com.tk4218.grocerylistr.model.Ingredient
import com.tk4218.grocerylistr.model.Recipe
import com.tk4218.grocerylistr.databinding.ActivityEditRecipeBinding

import kotlinx.android.synthetic.main.activity_edit_recipe.*

class EditRecipeActivity : AppCompatActivity() {
    private lateinit var mSettings: ApplicationSettings
    private var mCurrentPhotoPath = ""
    private var mNewPhotoPath = ""
    private var mTempImage: Boolean = false
    private var mImageManager = ImageManager()

    private var mRecipeKey: String = ""
    private lateinit var mRecipe: Recipe
    private var mIngredientList: ArrayList<Ingredient> = ArrayList()

    companion object {
        private const val REQUEST_PERMISSIONS = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSettings = ApplicationSettings(this)

        val binding = DataBindingUtil.setContentView<ActivityEditRecipeBinding>(this, R.layout.activity_edit_recipe)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        mTempImage = false
        edit_ingredient_list.layoutManager = LinearLayoutManager(this)

        val extras = intent.extras
        if (extras != null) {
            mRecipeKey = extras.getString("recipeKey") ?: ""
        }

        if (mRecipeKey.isNotEmpty()) {
            mRecipe = Recipe.getRecipe(mRecipeKey)
        } else {
            title = "Add New Recipe"
            mRecipe = Recipe(this)
            edit_ingredient_list.adapter = AddIngredientAdapter(this, mIngredientList)
        }
        binding.recipe = mRecipe
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_edit_recipe, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_save -> {
                mRecipe.ingredients = mIngredientList
                mRecipe.save()
                finish()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    /*********************************************
     * Event Handlers
     */
    fun addIngredient(view: View) {
        mIngredientList.add(Ingredient(this))
        edit_ingredient_list.adapter?.notifyItemInserted(mIngredientList.size - 1)
    }

    fun changePicture(view: View) {
        Log.d("Change Picture", "Recipe Image Clicked")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val hasStoragePermissions = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (hasStoragePermissions != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSIONS)
                return
            }
        }
        capturePicture()
    }

    private fun capturePicture() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Load Image").setItems(arrayOf<CharSequence>("Take Picture", "Load Image from Gallery")
        ) { _, which ->
            if (which == 0) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    val f = mImageManager.createNewPhotoFile(this@EditRecipeActivity, mRecipeKey, true)
                    mNewPhotoPath = f.absolutePath
                    mTempImage = true
                    val photoURI = FileProvider.getUriForFile(this@EditRecipeActivity,
                            "com.example.android.fileprovider", f)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                } catch (e: IOException) {
                    e.printStackTrace()
                    mNewPhotoPath = ""
                }

                startActivityForResult(takePictureIntent, ImageManager.REQUEST_TAKE_PHOTO)
            }
            if (which == 1) {
                val selectImageIntent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startActivityForResult(selectImageIntent, ImageManager.REQUEST_LOAD_IMAGE)
            }
        }

        builder.show()
    }

    /******************************************
     * Activity Result
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ImageManager.REQUEST_TAKE_PHOTO -> if (mNewPhotoPath.isNotEmpty()) {
                mRecipe.recipeImage = mNewPhotoPath
            }

            ImageManager.REQUEST_LOAD_IMAGE -> {
                if (data == null) return

                val selectedImage = data.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

                val cursor = contentResolver.query(selectedImage!!,
                        filePathColumn, null, null, null)
                if (cursor != null) {
                    cursor.moveToFirst()
                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    mNewPhotoPath = cursor.getString(columnIndex)
                    mTempImage = false
                    cursor.close()
                    if (mNewPhotoPath != "") {
                        mRecipe.recipeImage = mNewPhotoPath
                    }
                }
            }

            else -> {
            }
        }
    }

    /******************************************
     * Permissions Handling
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturePicture()
            } else
                Log.d("Permissions", "Write External Storage Denied")
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
