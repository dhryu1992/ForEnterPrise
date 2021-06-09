package com.example.room_java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.room_java.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // LiveData도 반영해서 데이터 바인딩 가능해짐
        binding.setLifecycleOwner(this);


        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        binding.;

        // UI 갱신
        viewModel.getAll().observe(this, todos -> { //getAll()을 했을 때 결과가 todos 로 들어옴.
            binding.resultText.setText(todos.toString());
        });

        // 버튼 클릭 시 DB에 insert
        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.insert(new Todo(binding.todoEditText.getText().toString()));

            }
        });

        findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //db.todoDao().delete();
                binding.resultText.setText(viewModel.getAll().toString());

            }
        });
    }
}