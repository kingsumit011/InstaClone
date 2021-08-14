package com.android.example.instaclone;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.android.example.instaclone.utils.BottomNavigationhelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = StartActivity.class.getName();
    private final Context mContext = this;
    private static final int ACTIVITY_NUM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setupBottomNavigationView();
    }
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationViewEx = findViewById(R.id.bottom_nav_bar);
        BottomNavigationhelper.setUpBottomNavigation(bottomNavigationViewEx);
        BottomNavigationhelper.enableNavigation(mContext, this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}