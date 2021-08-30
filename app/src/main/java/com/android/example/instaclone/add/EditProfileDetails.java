package com.android.example.instaclone.add;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.example.instaclone.Model.User;
import com.android.example.instaclone.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kroegerama.imgpicker.BottomSheetImagePicker;
import com.kroegerama.imgpicker.ButtonType;

import java.util.List;

public class EditProfileDetails extends AppCompatActivity implements BottomSheetImagePicker.OnImagesSelectedListener {
    private static final String TAG = EditProfileDetails.class.toString();
    private TextView userName, saveButton;
    private AutoCompleteTextView bio;
    private ImageView profilePhoto, close;
    private String userid;
    private Uri profileimgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_details);
        init();
        initWidget();
    }

    private void initWidget() {
        FirebaseDatabase.getInstance().getReference().child("User").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userName.setText(user.getUserName());

                Glide.with(EditProfileDetails.this).load(Uri.parse(user.getProfileimg())).into(profilePhoto);
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error : " + error);
            }
        });

        profilePhoto.setOnClickListener(v -> {
            Log.d(TAG, "Photo is Selected");
            new BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                    .cameraButton(ButtonType.Button)
                    .galleryButton(ButtonType.Button)
                    .singleSelectTitle(R.string.pick_single)
                    .requestTag("single")
                    .show(getSupportFragmentManager(), null);
        });

        saveButton.setOnClickListener(v -> {

            updateProfile();
        });
        close.setOnClickListener(v -> {
            finish();
        });
    }

    private void updateProfile() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Saving");
        pd.show();
        FirebaseDatabase.getInstance().getReference().child("User").child(userid).child("bio").setValue(bio.getText().toString());
        if (profileimgUrl != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("User").child(userid).child(System.currentTimeMillis() + "." + getFileExtention(profileimgUrl));
            storageReference.putFile(profileimgUrl).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageReference.getDownloadUrl();

            }).addOnCompleteListener(task -> {
                Uri downLoadUri = (Uri) task.getResult();
                FirebaseDatabase.getInstance().getReference().child("User").child(userid).child("profileimg").setValue(downLoadUri.toString());
            }).addOnFailureListener(e ->
                    Log.e(TAG, "Error : " + e)
            ).addOnSuccessListener(uri -> {
                Toast.makeText(EditProfileDetails.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 2000);
                pd.dismiss();
                finish();//            startActivity(new Intent(EditProfileDetails.this, StartActivity.class));

            });
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 2000);
            pd.dismiss();
            finish();
        }
    }

    private void init() {
        userName = findViewById(R.id.username);
        close = findViewById(R.id.edit_close);
        bio = findViewById(R.id.profile_bio);
        profilePhoto = findViewById(R.id.profile_img);
        saveButton = findViewById(R.id.profile_save_button);
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        profileimgUrl = null;

    }

    @Override
    public void onImagesSelected(@NonNull List<? extends Uri> list, @Nullable String s) {
        Log.d(TAG, "Uri of selected Photo " + list.get(0).toString());
        profileimgUrl = list.get(0);
        Glide.with(EditProfileDetails.this).load(list.get(0)).into(profilePhoto);
    }

    private String getFileExtention(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));

    }
}