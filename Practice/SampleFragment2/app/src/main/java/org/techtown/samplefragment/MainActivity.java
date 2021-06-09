package org.techtown.samplefragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements ListFragment.ImageSelectionCallback {

    ListFragment listFragment;
    ViewerFragment viewerFragment;

    int[] images = {R.drawable.dream01, R.drawable.dream02, R.drawable.dream03};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager manger= getSupportFragmentManager();
        listFragment = (ListFragment) manger.findFragmentById(R.id.listFragment);
        viewerFragment = (ViewerFragment) manger.findFragmentById(R.id.viewerFragment);
    }

    public void onImageSelected(int position) {
        viewerFragment.setImage(images[position]);
    }
}