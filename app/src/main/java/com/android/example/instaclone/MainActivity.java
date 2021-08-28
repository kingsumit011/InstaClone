package com.android.example.instaclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.example.instaclone.login.LoginPage;
import com.android.example.instaclone.login.Register;
import com.google.firebase.auth.FirebaseAuth;

//import android.support.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        startActivity(new Intent());
//        FirebaseApp.initializeApp(this);

        if(FirebaseAuth.getInstance().getCurrentUser()!= null){
            startActivity(new Intent(MainActivity.this, StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }

        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser()!= null){
                startActivity(new Intent(MainActivity.this, StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }

        Button login = (Button)findViewById(R.id.Login_main);
        Button register = (Button)findViewById(R.id.SignIn_main);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , LoginPage.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , Register.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

    }
}