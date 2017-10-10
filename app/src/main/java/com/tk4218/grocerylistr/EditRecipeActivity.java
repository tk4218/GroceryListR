package com.tk4218.grocerylistr;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.tk4218.grocerylistr.Adapters.AddIngredientAdapter;
import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;
import com.tk4218.grocerylistr.Image.ImageManager;
import com.tk4218.grocerylistr.Model.Ingredient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class EditRecipeActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 100;
    String mCurrentPhotoPath = "";

    ImageButton mRecipeImage;
    ImageManager mImageManager = new ImageManager();
    ListView mIngredientListView;
    ArrayList<Ingredient> mIngredientList;

    QueryBuilder mQb = new QueryBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_edit_recipe);
        setTitle("Add New Recipe");

        mRecipeImage = (ImageButton) findViewById(R.id.edit_recipe_image);

        mIngredientListView = (ListView) findViewById(R.id.edit_ingredient_list);
        mIngredientList = new ArrayList<>();
        mIngredientListView.setAdapter(new AddIngredientAdapter(this, mIngredientList));
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
            if(saveRecipe()){
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*********************************************
     *  Event Handlers
     *********************************************/
    public void addIngredient(View view){
        updateIngredientList();
        mIngredientList.add(new Ingredient());
        mIngredientListView.setAdapter(new AddIngredientAdapter(this, mIngredientList));
    }

    public void deleteIngredient(View view){
        int ingredientPosition = (int) view.getTag();
        updateIngredientList();
        mIngredientList.remove(ingredientPosition);
        mIngredientListView.setAdapter(new AddIngredientAdapter(this, mIngredientList));
    }

    private void updateIngredientList(){
        for(int i = 0; i < mIngredientList.size(); i++){
            Ingredient ingredient = (Ingredient) mIngredientListView.getAdapter().getItem(i);
            mIngredientList.get(i).setIngredientName(ingredient.getIngredientName());
        }
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
                            File f;
                            try {
                                f = mImageManager.createNewPhotoFile();
                                mCurrentPhotoPath = f.getAbsolutePath();
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            } catch (IOException e) {
                                e.printStackTrace();
                                mCurrentPhotoPath = "";
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
                Intent intent = new Intent("com.android.camera.action.CROP");

                File f = new File(mCurrentPhotoPath);
                Uri contentUri = Uri.fromFile(f);
                Bundle extras = data.getExtras();

                Log.d("Intent Data", ""+extras.size());

                intent.setType("image/*");
                intent.putExtra("outputX", 1024);
                intent.putExtra("outputY", 1024);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, ImageManager.REQUEST_CROP_PHOTO);
                break;
            case ImageManager.REQUEST_LOAD_IMAGE:
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mCurrentPhotoPath = cursor.getString(columnIndex);
                cursor.close();
                break;
            case ImageManager.REQUEST_CROP_PHOTO:
                break;
            default:
                break;
        }

        if(requestCode == ImageManager.REQUEST_LOAD_IMAGE){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mCurrentPhotoPath = cursor.getString(columnIndex);
            cursor.close();
        }
       // mRecipeImage.setImageBitmap(mImageManager.setPic(mCurrentPhotoPath, mRecipeImage.getLayoutParams().width, mRecipeImage.getLayoutParams().height));
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


    /************************************************
     * Save Recipe
     ************************************************/
    public boolean saveRecipe(){
        EditText recipeName = (EditText) findViewById(R.id.edit_recipe_name);
        Spinner mealType = (Spinner) findViewById(R.id.edit_meal_type);
        Spinner cuisineType = (Spinner) findViewById(R.id.edit_meal_style);

        if(recipeName.getText().toString().equals("")){
            Toast.makeText(this, "Please Add a Name for This Recipe", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mIngredientList.size() ==0){
            Toast.makeText(this, "Please Add At Least One Ingredient", Toast.LENGTH_SHORT).show();
            return false;
        }

        int recipeKey = mQb.insertRecipe(recipeName.getText().toString(), mealType.getSelectedItem().toString(), cuisineType.getSelectedItem().toString(), mCurrentPhotoPath);

        if(recipeKey != 0){
            for(int i = 0; i < mIngredientList.size(); i++){
                Ingredient ingredient = new Ingredient(mIngredientList.get(i).getIngredientName().trim());
                int ingredientKey = 0;
                if(ingredient.getIngredientKey() == 0){
                    ingredientKey = mQb.insertIngredient(mIngredientList.get(i).getIngredientName().trim(), "");
                } else {
                    ingredientKey = ingredient.getIngredientKey();
                }

                mQb.insertRecipeToIngredient(recipeKey, ingredientKey, mIngredientList.get(i).getIngredientAmount(), mIngredientList.get(i).getIngredientUnit(), "", "", false);
            }
        }

        return true;
    }
}
