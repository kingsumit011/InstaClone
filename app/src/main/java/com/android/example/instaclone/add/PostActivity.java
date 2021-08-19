package com.android.example.instaclone.add;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.example.instaclone.R;
import com.android.example.instaclone.StartActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.kroegerama.imgpicker.BottomSheetImagePicker;
import com.kroegerama.imgpicker.ButtonType;

import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity implements BottomSheetImagePicker.OnImagesSelectedListener {
    private static final String TAG = PostActivity.class.toString();
    private ImageView close , postImage , postUserImage;
    private Uri imageUri;
    private String imageUrl;
    private TextView postUserName , postDescription , postButton;
    private FirebaseUser firebaseUser;
    private String profileId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        initWidgets();
        init();

    }

    private void init(){
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this , StartActivity.class));
                finish();
            }
        });
        postImage.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG , "click on post image");
                new BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                        .cameraButton(ButtonType.Button)
                        .galleryButton(ButtonType.Button)
                        .singleSelectTitle(R.string.pick_single)
                        .requestTag("single")
                        .show(getSupportFragmentManager(), null);
            }
        }));
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG , " Post is Uploding");
                uploadPost();
            }
        });
        FirebaseDatabase.getInstance().getReference().child("User").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User user = snapshot.getClass(User.class);
                String userName = String.valueOf(snapshot.child("userName").getValue());
                postUserName.setText(userName);
                Uri profileUri = Uri.parse(String.valueOf(snapshot.child("Profileimg").getValue()));
                Glide.with(PostActivity.this).load(profileUri).into(postUserImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void initWidgets(){
        close = findViewById(R.id.post_add_close);
        postImage = findViewById(R.id.post_add_con);
        postUserImage = findViewById(R.id.post_add_user_profile_photo);
        postUserName = findViewById(R.id.post_add_username);
        postDescription = findViewById(R.id.post_add_discription);
        postButton = findViewById(R.id.post_add_post_button);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profileId = firebaseUser.getUid();
    }


    @Override
    public void onImagesSelected(@NonNull List<? extends Uri> uri, @Nullable String tag) {
        Log.d(TAG , "Image uri " + uri.get(0).toString()+tag);
        imageUri = uri.get(0);
        Glide.with(PostActivity.this).load(imageUri).into(postImage);
//        postImage.setImageURI(uri.get(0));
    }
    public void uploadPost(){
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Post is Uploading");
        pd.show();
        if(imageUri != null){
            final StorageReference filePath = FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis() + "." + getFileExtention(imageUri));
            StorageTask uploadTask = filePath.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Uri downLoaduri = (Uri) task.getResult();
                    imageUrl = downLoaduri.toString();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                    String postId = ref.push().getKey();
                    HashMap<String , Object> temp = new HashMap<>();
                    temp.put("postId" , postId);
                    temp.put("imageUrl" , imageUrl);
                    temp.put("description" , postDescription.getText().toString());
                    temp.put("publisher" , FirebaseAuth.getInstance().getCurrentUser().getUid());
                    ref.child(postId).setValue(temp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(PostActivity.this, "Post uploded", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PostActivity.this , StartActivity.class));
                }
            });
        }else{
            Log.e(TAG , "No post selected");
            Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show();
        }

    }

    private String getFileExtention(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));

    }
}