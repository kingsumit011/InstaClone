package com.android.example.instaclone.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.example.instaclone.R;
import com.android.example.instaclone.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentLogin extends Fragment {

    private final static String TAG = FragmentLogin.class.toString();
    EditText input_Password, input_Email_Id;
    Button login;
    private FirebaseAuth myAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initWidget(view);
        myAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailId = input_Email_Id.getText().toString();
                String password = input_Password.getText().toString();
                if (TextUtils.isEmpty(emailId) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getContext(), "Enter Credentials", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(emailId, password);
                }
            }
        });

        return view;
    }

    private void initWidget(View view) {
        input_Email_Id = view.findViewById(R.id.Input_Email_ID);
        input_Password = view.findViewById(R.id.Input_Password);
        login = view.findViewById(R.id.Login);

    }

    private void loginUser(String emailId, String password) {
        final ProgressDialog pb = new ProgressDialog(getContext());
        pb.setMessage("Please Wait");
        pb.show();
        myAuth.signInWithEmailAndPassword(emailId, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                pb.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Login Succesful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(), StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "ERRor" + e.getMessage());
            }
        });
    }
}