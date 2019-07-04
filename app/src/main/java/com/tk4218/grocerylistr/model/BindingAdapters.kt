package com.tk4218.grocerylistr.model

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import java.io.File

@BindingAdapter("android:imageUrl")
fun ImageView.loadImage(recipeImage: String?) {
    if (recipeImage != null) {
        Picasso.with(this.context)
                .load(File(recipeImage))
                .fit()
                .centerCrop()
                .into(this)
    }
}