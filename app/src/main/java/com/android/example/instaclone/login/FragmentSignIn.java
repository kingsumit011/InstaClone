package com.android.example.instaclone.login;

import android.app.Activity;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.example.instaclone.Model.User;
import com.android.example.instaclone.R;
import com.android.example.instaclone.StartActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FragmentSignIn extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private final static String TAG = FragmentSignIn.class.toString();
    private final int RC_SIGN_IN = 1;
    private EditText userName, emailID, password;
    private Button signIn, signInGoogle;
    private DatabaseReference myRef;
    private FirebaseAuth myAuth;
    private ProgressDialog pd;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
    private ActivityResultLauncher<Intent> launchSomeActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        initWidgets(view);
        init(view);
        myRef = FirebaseDatabase.getInstance().getReference();
        myAuth = FirebaseAuth.getInstance();

        return view;
    }

    private void init(View view) {
        signIn.setOnClickListener(v -> {
            String txtuserName = userName.getText().toString();
            String txtEmailId = emailID.getText().toString();
            String txtPassword = password.getText().toString();
            if (TextUtils.isEmpty(txtEmailId) || TextUtils.isEmpty(txtPassword) || TextUtils.isEmpty(txtuserName)) {
                Toast.makeText(getContext(), "Enter full Credential", Toast.LENGTH_SHORT).show();
            } else if (txtPassword.length() < 6) {
                Toast.makeText(getContext(), "Password Length To short", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(txtEmailId, txtPassword, txtuserName);
            }
        });
        signInGoogle.setOnClickListener(v -> {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.web_client_id))
                    .requestServerAuthCode(getString(R.string.web_client_id))
                    .requestEmail()
                    .build();
            Intent intent = GoogleSignIn.getClient(getContext(), gso).getSignInIntent();
            launchSomeActivity.launch(intent);
        });

    }

    private void initWidgets(View view) {
        userName = view.findViewById(R.id.Input_User_Name);
        emailID = view.findViewById(R.id.Input_Email_ID);
        password = view.findViewById(R.id.Input_Password);
        signIn = view.findViewById(R.id.SignIn);
        pd = new ProgressDialog(getContext());

        signInGoogle = view.findViewById(R.id.sign_in_google);

        launchSomeActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        handlesignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));

                    }
                });
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                startActivity(new Intent(getContext(), StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

    }

    private void handlesignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            String idToken = account.getIdToken();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String profileImg = account.getPhotoUrl().toString();
            User user = new User(
                    name,
                    "Hello " + name + " Here",
                    email,
                    null,
                    profileImg != null ? profileImg : "https://image.flaticon.com/icons/png/512/21/21104.png"
            );
            // you can store user data to SharedPreference
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuthWithGoogle(credential, user);
        } else {
            Toast.makeText(getContext(), "Sig In Fail", Toast.LENGTH_SHORT).show();
        }
    }

    //Register user by google
    private void firebaseAuthWithGoogle(AuthCredential credential, User user) {

        myAuth.signInWithCredential(credential).addOnCompleteListener((Activity) getContext(), task -> {
            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Login successful", Toast.LENGTH_SHORT).show();

            } else {
                Log.d(TAG, "signInWithCredential" + task.getException().getMessage());
                task.getException().printStackTrace();
                Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(authResult -> {
            String userID = authResult.getUser().getUid();

            final boolean[] userexist = new boolean[1];
            myRef.child("User").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userexist[0] = snapshot.child(userID).exists();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if(!userexist[0]) {
                user.setId(userID);

                myRef.child("User").child(userID).setValue(user).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Log.d(TAG, "user added || updated");
//                        Toast.makeText(getContext(), "ID crated , Update profile", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {

                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // Regester user by email id
    private void registerUser(String emailId, String password, String userName) {
        final ProgressDialog pb = new ProgressDialog(getContext());
        pb.setMessage("Please Wait");
        pb.show();
        myAuth.createUserWithEmailAndPassword(emailId, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

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
                        pd.dismiss();
                        Toast.makeText(getContext(), "ID crated , Update profile", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pb.dismiss();
                Toast.makeText(getContext(), "Account Not Available", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error : " + e);
            }
        });
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}