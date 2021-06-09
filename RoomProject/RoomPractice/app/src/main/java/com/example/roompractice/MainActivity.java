package com.example.roompractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;

import com.example.roompractice.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private UserProfileDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //allowMainThreadQuries 를 쓰고 빌드를 하게 되면 db로의 접근은 메인스레드(UI스레드) 에서 해도 상관없다.
        db = Room.databaseBuilder(this, UserProfileDatabase.class, "userprofile").allowMainThreadQueries().build();

        fetchUserProfileList();
    }

    private void fetchUserProfileList() {
        List<UserProfile> userProfileList = db.getUserProfileDao().getAll(); // 데이터베이스 안에 있는 UserProfile 객체가 목록으로 반환됨,
        String userListText = "사용자 목록";
        for (UserProfile userProfile : userProfileList) {
            userListText += "\n" + userProfile.id + ", " + userProfile.name + ", " + userProfile.phone + ", " + userProfile.address;
        }

        binding.userList.setText(userListText);
    }

    public void addUserProfile(View view) {
        UserProfile userProfile = new UserProfile();
        userProfile.name = binding.name.getText().toString();
        userProfile.phone = binding.phone.getText().toString();
        userProfile.address = binding.address.getText().toString();
        db.getUserProfileDao().insert(userProfile);

        fetchUserProfileList();
    }
}