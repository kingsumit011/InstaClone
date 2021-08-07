package com.android.example.instaclone;

import android.os.Bundle;
//import android.support.*;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginPage Id = new LoginPage();
        setContentView(R.layout.login_page);
    }
}