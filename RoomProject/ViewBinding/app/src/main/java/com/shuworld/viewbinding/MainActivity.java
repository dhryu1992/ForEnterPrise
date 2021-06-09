package com.shuworld.viewbinding;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.shuworld.viewbinding.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    //private TextView nameView, phoneView, addressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // gradle 에 뷰바인딩을 선언해두면 ActivityMainBinding이라는 클래스가 자동으로 만들어진다.
        // ActivityMainBinding 에는 inflate 메소드가 있다. 파라미터로 getLayoutInflater을 취급.
        // Activity_main이라는 파일을 inflation해서 그 안에 id로 정해져있는 View들을 레퍼런스로 만들고
        // 그 레퍼런스에 실제 데이터를 대입해주는 역할까지 함.
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // binding 객체에는 inflation 시킨 이후 최상위 객체를 가져오는 getroot() 라는 메소드가 있다.
        //setContentView(R.layout.activity_main); 대신
        setContentView(binding.getRoot());

        // 1. 레퍼런스를 가져온다. 밑부분을 뷰바인딩이라고 한다. 뷰바인딩 덕에 없어져도 되는 곳.
        /*nameView = findViewById(R.id.name);
        phoneView = findViewById(R.id.phone);
        addressView = findViewById(R.id.address);*/

        // 데이터 로딩
        fetchUserProfile();
    }

    private void fetchUserProfile() {

        // 2. 데이터를 로딩한다

        UserProfile userProfile = new UserProfile();
        userProfile.name = "Hong Gil Dong";
        userProfile.phone = "010-1000-1000";
        userProfile.address = "수원시 권선동";


        updateUI(userProfile);
    }


    private void updateUI(UserProfile userProfile) {
        // 3. 레퍼런스에 데이터를 세팅한다. 밑부분은 데이터바인딩이라고 한다.
        binding.name.setText(userProfile.name);
        binding.phone.setText(userProfile.phone);
        binding.address.setText(userProfile.address);

    }
}