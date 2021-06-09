package com.shuworld.databinding_expression;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.shuworld.databinding_expression.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fetchUserProfile();

    }

    private void fetchUserProfile() {
        UserProfile userProfile = new UserProfile();
        userProfile.name = "Hong Gil Dong";
        userProfile.phone = "010-1000-1000";
        userProfile.address = "수원시 권선동";
        userProfile.ImageUrl = "https://e1.pngegg.com/pngimages/370/527/png-clipart-render-irene.png";
        binding.setUserProfile(userProfile);
    }
}