package org.techtown.pager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(2);
            }
        });

        pager = findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3); // 미리 로딩해 놓을 아이템의 갯수를 세 개로 늘림. 좌우 스크롤 할 때 빠르게 보여줄 수 있음.

        MyPagerAdater adater = new MyPagerAdater(getSupportFragmentManager());

        Fragment1 fragment1 = new Fragment1();
        adater.addItem(fragment1);

        Fragment2 fragment2 = new Fragment2();
        adater.addItem(fragment2);

        Fragment3 fragment3 = new Fragment3();
        adater.addItem(fragment3);

        pager.setAdapter(adater);
    }

    class MyPagerAdater extends FragmentStatePagerAdapter {
        ArrayList<Fragment> items = new ArrayList<Fragment>();
        public MyPagerAdater(FragmentManager fm) {
            super(fm);
        }

        public void addItem(Fragment item) {
            items.add(item);
        }

        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }
}