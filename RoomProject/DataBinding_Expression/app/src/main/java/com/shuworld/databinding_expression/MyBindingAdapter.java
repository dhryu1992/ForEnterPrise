package com.shuworld.databinding_expression;

import androidx.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MyBindingAdapter {
    @BindingAdapter("imageUrl")
    public static void loadImageUrl(ImageView view, String url) {
        Glide.with(view.getContext()).load(url).into(view);
    }
}
