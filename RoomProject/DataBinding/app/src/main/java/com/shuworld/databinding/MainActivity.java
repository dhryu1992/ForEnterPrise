package com.shuworld.databinding;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shuworld.databinding.databinding.ActivityMainBinding;

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

        binding.setUserProfile(userProfile);
        //updateUI(userProfile);
    }

/*
    private void updateUI(UserProfile userProfile) {

        // 데이터 바인딩

        binding.name.setText(userProfile.name);
        binding.phone.setText(userProfile.phone);
        binding.address.setText(userProfile.address);
    }
*/
}