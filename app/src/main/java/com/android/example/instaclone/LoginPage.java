package com.android.example.instaclone;

//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginPage extends  AppCompatActivity{
    EditText input_Password , input_Email_Id ;
    Button login , signUp;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        input_Email_Id = (EditText) findViewById(R.id.Input_Email_ID);
        input_Password = (EditText) findViewById(R.id.Input_Password);
        login = (Button)findViewById(R.id.Login);
        signUp = (Button)findViewById(R.id.SignIn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_Id = input_Email_Id.getText().toString();
                String password = input_Password.getText().toString();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}