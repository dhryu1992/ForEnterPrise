package org.techtown.recycleview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        PersonAdapter adapter = new PersonAdapter();

        adapter.addItem(new Person("김민수", "010-1000-1000"));
        adapter.addItem(new Person("김하늘", "010-2000-1000"));
        adapter.addItem(new Person("홍길동", "010-3000-1000"));
        adapter.addItem(new Person("류시우", "010-4000-1000"));
        adapter.addItem(new Person("송채원", "010-5000-1000"));
        adapter.addItem(new Person("개시우", "010-5000-1000"));
        adapter.addItem(new Person("이시우", "010-5000-1000"));
        adapter.addItem(new Person("강시우", "010-5000-1000"));


        recyclerView.setAdapter(adapter);
    }
}