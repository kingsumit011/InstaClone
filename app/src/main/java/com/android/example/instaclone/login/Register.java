package com.android.example.instaclone.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.example.instaclone.Model.User;
import com.android.example.instaclone.R;
import com.android.example.instaclone.StartActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    private EditText userName, emailID, password;
    private Button signIn, signInGoogle;
    private DatabaseReference myRef;
    private FirebaseAuth myAuth;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);
        initWidgets();

        myRef = FirebaseDatabase.getInstance().getReference();
        myAuth = FirebaseAuth.getInstance();
        init();


    }

    private void init() {
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtuserName = userName.getText().toString();
                String txtEmailId = emailID.getText().toString();
                String txtPassword = password.getText().toString();
                if (TextUtils.isEmpty(txtEmailId) || TextUtils.isEmpty(txtPassword) || TextUtils.isEmpty(txtuserName)) {
                    Toast.makeText(Register.this, "Enter full Credential", Toast.LENGTH_SHORT).show();
                } else if (txtPassword.length() < 6) {
                    Toast.makeText(Register.this, "Password Length To short", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(txtEmailId, txtPassword, txtuserName);
                }
            }
        });
        //TODO
//        signInGoogle.setOnClickListener((new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GoogleSignInOptions gso = new GoogleSignInOptions
//                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                        .requestIdToken(getString(R.string.default_web_client_id))
//                        .requestEmail()
//                        .build();
//            }
//        }));
    }

    private void initWidgets() {
        userName = (EditText) findViewById(R.id.Input_User_Name);
        emailID = (EditText) findViewById(R.id.Input_Email_ID);
        password = findViewById(R.id.Input_Password);
        signIn = findViewById(R.id.SignIn);
        pd = new ProgressDialog(this);
        signInGoogle = findViewById(R.id.sign_in_google);
    }


    // Regester user by email id
    private void registerUser(String emailId, String password, String userName) {
        final ProgressDialog pb = new ProgressDialog(this);
        pb.setMessage("Please Wait");
        pb.show();
        myAuth.createUserWithEmailAndPassword(emailId, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("emailId", emailId);
                map.put("password", password);
                map.put("userName", userName);
                map.put("id", myAuth.getCurrentUser().getUid());
                map.put("bio", "");
                map.put("profileimg", R.drawable.ic_action_person);
                User user = new User(
                        userName,
                        "Hello " + userName + " Here",
                        emailId,
                        myAuth.getCurrentUser().getUid(),
                        "https://image.flaticon.com/icons/png/512/21/21104.png"
                );
                myRef.child("User").child(myAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {

                        Toast.makeText(Register.this, "ID crated , Update profile", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Register.this, StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finsh();
                    }
                }).addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void finsh() {
        myAuth.signOut();
    }
}
