package com.android.example.instaclone.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.*;

import com.android.example.instaclone.Profile.ProfileFragment;
import com.android.example.instaclone.R;
import com.android.example.instaclone.add.PostActivity;
import com.android.example.instaclone.home.HomeFragment;
import com.android.example.instaclone.notification.NotificationFragment;
import com.android.example.instaclone.search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

//import androidx.fragment.app.FragmentManager;


public class BottomNavigationhelper extends FragmentManager {

    private final static String TAG = BottomNavigationhelper.class.getName();

    public static void setUpBottomNavigation(BottomNavigationView bottomNavigation){
        Log.d(TAG , "Setting up Bottom Navigation");
        bottomNavigation.setActivated(true);
        bottomNavigation.setItemHorizontalTranslationEnabled(false);
    }
    public static void enableNavigation(Context context , Activity activity , BottomNavigationView bottomNavigation){
        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.nav_home:
                           activity.getFragmentManager().beginTransaction().replace(R.id.fragment_container_view_tag , new HomeFragment() ).commit();

                        break;
                    case R.id.nav_search:
                        activity.getFragmentManager().beginTransaction().replace(R.id.fragment_container_view_tag , new SearchFragment() , null).commit();

//                        context.startActivity(new Intent(context , SearchActivity.class));
                        break;
                    case R.id.nav_add:
                        Log.d(TAG , "Start Add activity");
                        context.startActivity(new Intent(context , PostActivity.class));
                        break;
                    case R.id.nav_notification:
//                        context.startActivity(new Intent(context , SearchActivity.class));
//                        fragment = new NotificationFragment();
                        activity.getFragmentManager().beginTransaction().replace(R.id.fragment_container_view_tag , new NotificationFragment() , null).commit();

                        break;
                    case R.id.nav_profile:
//                        context.startActivity(new Intent(context , ProfileActivity.class));
//                        fragment = new ProfileFragment();
                        activity.getFragmentManager().beginTransaction().replace(R.id.fragment_container_view_tag , new ProfileFragment() , null).commit();
                        break;
                    default:Log.e(TAG , "Error No item selected");
                        activity.getFragmentManager().beginTransaction().replace(R.id.fragment_container_view_tag , new HomeFragment() ).commit();


                }

                return true;
            }
        });
    }


}
