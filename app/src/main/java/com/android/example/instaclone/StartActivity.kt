package com.android.example.instaclone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

private lateinit var bottomNav: BottomNavigationView
private lateinit var appBarConfiguration: AppBarConfiguration

//private lateinit var navController: NavController;
class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        bottomNav = findViewById(R.id.bottom_nav_bar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view_tag) as NavHostFragment
        val navController = navHostFragment.navController
//        navController = findNavController(R.id.fragment_container_view_tag);
        appBarConfiguration = AppBarConfiguration(setOf(R.id.fragment_home, R.id.fragment_search, R.id.postActivity, R.id.fragment_notification, R.id.fragment_profile))
        bottomNav.setupWithNavController(navController)
//        bottomNav.
    }
}