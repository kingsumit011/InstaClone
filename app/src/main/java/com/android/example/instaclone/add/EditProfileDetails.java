package com.android.example.instaclone.add;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.example.instaclone.R;
import com.android.example.instaclone.StartActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kroegerama.imgpicker.BottomSheetImagePicker;
import com.kroegerama.imgpicker.ButtonType;

import java.util.List;

public class EditProfileDetails extends AppCompatActivity implements BottomSheetImagePicker.OnImagesSelectedListener {
    private static final String TAG = EditProfileDetails.class.toString();
    private TextView userName , saveButton;
    private AutoCompleteTextView bio;
    private ImageView profilePhoto ,close;
    private String userid , profileimgUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_details);
        init();
        initWidget();
    }
    private void initWidget(){
        FirebaseDatabase.getInstance().getReference().child("User").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName.setText(snapshot.child("userName").getValue().toString());

                Glide.with(EditProfileDetails.this).load(Uri.parse(snapshot.child("profileimg").getValue().toString())).into(profilePhoto);
                bio.setText(snapshot.child("bio").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG , "Error : "+error);
            }
        });

        profilePhoto.setOnClickListener(v ->{
            Log.d(TAG ,"Photo is Selected");
            new BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                    .cameraButton(ButtonType.Button)
                    .galleryButton(ButtonType.Button)
                    .singleSelectTitle(R.string.pick_single)
                    .requestTag("single")
                    .show(getSupportFragmentManager(), null);
        });

        saveButton.setOnClickListener(v -> {
            ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Saving");
            pd.show();
            updateProfile();
        });
        close.setOnClickListener(v -> {
            startActivity(new Intent(EditProfileDetails.this, StartActivity.class));
            finish();
        });
    }

    private void updateProfile() {
        FirebaseDatabase.getInstance().getReference().child("User").child(userid).child("profileimg").setValue(profileimgUrl);
        FirebaseDatabase.getInstance().getReference().child("User").child(userid).child("bio").setValue(bio.getText().toString());
        startActivity(new Intent(EditProfileDetails.this , StartActivity.class));

    }

    private void init(){
        userName = findViewById(R.id.username);
        close = findViewById(R.id.edit_close);
        bio = findViewById(R.id.profile_bio);
        profilePhoto = findViewById(R.id.profile_img);
        saveButton = findViewById(R.id.profile_save_button);
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        profileimgUrl = "default";
    }

    @Override
    public void onImagesSelected(@NonNull List<? extends Uri> list, @Nullable String s) {
        Log.d(TAG , "Uri of selected Photo "+ list.get(0).toString());
        profileimgUrl = list.get(0).toString();
        Glide.with(EditProfileDetails.this).load(list.get(0)).into(profilePhoto);
    }
}