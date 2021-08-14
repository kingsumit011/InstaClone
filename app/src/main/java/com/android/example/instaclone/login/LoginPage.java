package com.android.example.instaclone.login;

//import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.example.instaclone.R;
import com.android.example.instaclone.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {

    private final static String TAG = LoginPage.class.getName();
    EditText input_Password, input_Email_Id;
    Button login, signUp;
    private FirebaseAuth myAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        initWidget();
        myAuth = FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailId = input_Email_Id.getText().toString();
                String password = input_Password.getText().toString();
                if(TextUtils.isEmpty(emailId) || TextUtils.isEmpty(password)){
                    Toast.makeText(LoginPage.this, "Enter Credentials", Toast.LENGTH_SHORT).show();
                }else{
                    loginUser(emailId , password);
                }
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this , Register.class));
            }
        });
    }
    private void initWidget(){
        input_Email_Id =  findViewById(R.id.Input_Email_ID);
        input_Password = findViewById(R.id.Input_Password);
        login = findViewById(R.id.Login);
        signUp =findViewById(R.id.SignIn);
    }
    private void loginUser(String emailId , String password){
        myAuth.signInWithEmailAndPassword(emailId,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginPage.this, "Login Succesful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginPage.this, StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG , "ERRor" + e.getMessage());
            }
        });
    }
}