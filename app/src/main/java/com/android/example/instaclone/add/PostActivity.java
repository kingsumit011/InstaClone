package com.android.example.instaclone.add;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.example.instaclone.R;
import com.android.example.instaclone.StartActivity;
import com.android.example.instaclone.utils.ImageManager;

public class PostActivity extends AppCompatActivity {
    private ImageView close , postImage , postUserImage;
    private Uri imageUri;
    private String imageUrl;
    private TextView postUserName , postDescription , postButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        initWidgets();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this , StartActivity.class));
                finish();
            }
        });
//        CropImage.activity().start(this);
        ImageManager im = new ImageManager(this , this,postImage);
//        imageUri = im.getMProfileUri();
//        postImage.setImageURI(imageUri);


    }
    private void initWidgets(){
        close = findViewById(R.id.post_add_close);
        postImage = findViewById(R.id.post_add_con);
        postUserImage = findViewById(R.id.post_add_user_profile_photo);
        postUserName = findViewById(R.id.post_add_username);
        postDescription = findViewById(R.id.post_add_discription);
        postButton = findViewById(R.id.post_add_post_button);
    }
    /*
//    @Override
//    private void  onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            //Image Uri will not be null for RESULT_OK
//            val uri: Uri = data?.data!!
//
//                    // Use Uri object instead of File to avoid storage permissions
//                    imgProfile.setImageURI(fileUri)
//        } else if (resultCode == ImagePicker.RESULT_ERROR) {
//            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
//        }
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode , resultCode , data);
//        if (requestCode == Activity.RESULT_OK) {
////            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            imageUri = data.getData();
//            postImage.setImageURI(imageUri);
//            } else if (resultCode == ImagePicker.RESULT_ERROR) {
//            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
//            }else{
//            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
//        }


    }*/
}
