package com.example.roompractice;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;

import com.example.roompractice.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private UserProfileViewModel model;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 뷰 모델 객체 생성
        model = new ViewModelProvider(this).get(UserProfileViewModel.class);
        //allowMainThreadQuries 를 쓰고 빌드를 하게 되면 db로의 접근은 메인스레드(UI스레드) 에서 해도 상관없다.
        model.userProfileList.observe(this, new Observer<List<UserProfile>>() {
            @Override
            public void onChanged(List<UserProfile> userProfiles) {
                updateUSerProfileList(userProfiles);
            }
        });
    }

    private void updateUSerProfileList(List<UserProfile> userProfiles) {
        String userListText = "사용자 목록";
        for (UserProfile userProfile : userProfiles) {
            userListText += "\n" + userProfile.id + ", " + userProfile.name + ", " + userProfile.phone + ", " + userProfile.address;
        }

        binding.userList.setText(userListText);
    }

    public void addUserProfile(View view) {
        UserProfile userProfile = new UserProfile();
        userProfile.name = binding.name.getText().toString();
        userProfile.phone = binding.phone.getText().toString();
        userProfile.address = binding.address.getText().toString();
        model.insert(userProfile);
    }

    public void deleteUserProfile(View view) {
        UserProfile userProfile = new UserProfile();

    }
}