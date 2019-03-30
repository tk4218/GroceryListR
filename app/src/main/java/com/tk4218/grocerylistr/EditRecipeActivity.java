package com.tk4218.grocerylistr;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tk4218.grocerylistr.Image.ImageManager;
import com.tk4218.grocerylistr.Model.ApplicationSettings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.tk4218.grocerylistr.databinding.ActivityEditRecipeBinding;

public class EditRecipeActivity extends AppCompatActivity {
    ApplicationSettings mSettings;

    private static final int REQUEST_PERMISSIONS = 100;
    String mCurrentPhotoPath = "";
    String mNewPhotoPath = "";
    boolean mTempImage;

    ImageManager mImageManager = new ImageManager();
    RecyclerView mIngredientListView;
    AddIngredientAdapter mAdapter;

    String mRecipeKey;
    Recipe mRecipe;
    ArrayList<Ingredient> mIngredientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = new ApplicationSettings(this);

        ActivityEditRecipeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_recipe);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTempImage = false;
        mIngredientListView = findViewById(R.id.edit_ingredient_list);
        mIngredientListView.setLayoutManager(new LinearLayoutManager(this));

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            mRecipeKey = extras.getString("recipeKey");
        }

        if(mRecipeKey != null) {
            mRecipe = Recipe.getRecipe(mRecipeKey);
        } else {
            setTitle("Add New Recipe");
            mRecipe = new Recipe(this);
            mIngredientList = new ArrayList<>();
            mAdapter = new AddIngredientAdapter(this, mIngredientList);
            mIngredientListView.setAdapter(mAdapter);
        }

        binding.setRecipe(mRecipe);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            mRecipe.setIngredients(mIngredientList);
            mRecipe.save();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*********************************************
     *  Event Handlers
     *********************************************/
    public void addIngredient(View view){
        mIngredientList.add(new Ingredient(this));
        mAdapter.notifyItemInserted(mIngredientList.size()-1);
    }

    public void changePicture(View view){
        Log.d("Change Picture", "Recipe Image Clicked");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasStoragePermissions = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(hasStoragePermissions != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
                return;
            }
        }
        capturePicture();
    }

    private void capturePicture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Load Image").setItems(new CharSequence[]{"Take Picture", "Load Image from Gallery"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            try {
                                File f = mImageManager.createNewPhotoFile(EditRecipeActivity.this, mRecipeKey, true);
                                mNewPhotoPath = f.getAbsolutePath();
                                mTempImage = true;
                                Uri photoURI = FileProvider.getUriForFile(EditRecipeActivity.this,
                                        "com.example.android.fileprovider", f);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            } catch (IOException e) {
                                e.printStackTrace();
                                mNewPhotoPath = "";
                            }
                            startActivityForResult(takePictureIntent, ImageManager.REQUEST_TAKE_PHOTO);
                        }
                        if(which == 1){
                            Intent selectImageIntent = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            startActivityForResult(selectImageIntent, ImageManager.REQUEST_LOAD_IMAGE);
                        }
                    }
                });

        builder.show();
    }

    /******************************************
     * Activity Result
     ******************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ImageManager.REQUEST_TAKE_PHOTO:
                if(!mNewPhotoPath.equals("")) {
                    mRecipe.setRecipeImage(mNewPhotoPath);
                }
                break;

            case ImageManager.REQUEST_LOAD_IMAGE:
                if(data == null) break;

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                if(cursor != null){
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mNewPhotoPath = cursor.getString(columnIndex);
                    mTempImage = false;
                    cursor.close();
                    if(!mNewPhotoPath.equals("")){
                        mRecipe.setRecipeImage(mNewPhotoPath);
                    }
                }
                break;

            default:
                break;
        }
    }


    /******************************************
     * Permissions Handling
     ******************************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode){
            case REQUEST_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     capturePicture();
                } else
                    Log.d("Permissions", "Write External Storage Denied");
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
