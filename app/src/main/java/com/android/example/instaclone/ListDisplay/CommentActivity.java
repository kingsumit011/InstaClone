package com.android.example.instaclone.ListDisplay;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.instaclone.Adapter.CommentAdapter;
import com.android.example.instaclone.Model.Comment;
import com.android.example.instaclone.R;
import com.android.example.instaclone.StartActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG = CommentActivity.class.toString();
    private String postId , publisher;
    private ImageView userImage , backButton;
    private RecyclerView mCommentRecyclerView;
    private List<Comment> mCommentList;
    private CommentAdapter mAdapter;
    private TextView postButtom;
    private AutoCompleteTextView comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_activity);
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comment");
        init();
        initWidget();
    }

    private void initWidget() {
        FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Glide.with(CommentActivity.this).load(Uri.parse(snapshot.child("profileimg").getValue().toString())).into(userImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG , " Error : " + error);
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Comment").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mCommentList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Comment comment = snapshot1.getValue(Comment.class);
                    Log.d(TAG , " Comment is added in list "+comment.getCommentdata() );
                    mCommentList.add(0 , comment);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG , " Error : " + error);
            }
        });

        postButtom.setOnClickListener(v -> {
            if(comment.getText().toString() == ""){
                Toast.makeText(this, "Enter Comment", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadComment();
        });
        backButton.setOnClickListener(v->{
            startActivity(new Intent(CommentActivity.this , StartActivity.class));
        finish();

        });
    }

    private void uploadComment() {
        String commentID = FirebaseDatabase.getInstance().getReference().child("Comment").child(postId).push().getKey();
        HashMap<String , Object> temp = new HashMap<>();
        temp.put("id" , commentID);
        temp.put("commentdata" , comment.getText().toString());
        temp.put("publisher" , FirebaseAuth.getInstance().getCurrentUser().getUid());
        temp.put("time" , System.currentTimeMillis());
        Log.d(TAG ,"Comment is Uploading");
        FirebaseDatabase.getInstance().getReference().child("Comment").child(postId).child(commentID).setValue(temp);
        comment.setText("");
    }

    private void init(){
        Intent intent = getIntent();
        postId = intent.getStringExtra("PostId");
        publisher = intent.getStringExtra("Publisher");
        userImage = findViewById(R.id.user_profile_photo);
        backButton = findViewById(R.id.close_button);
        postButtom = findViewById(R.id.comment_post_button);
        comment = findViewById(R.id.post_comment);
        mCommentList = new ArrayList<>();
        mCommentRecyclerView = findViewById(R.id.comment_Con);
        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mCommentRecyclerView.setHasFixedSize(true);
        mAdapter = new CommentAdapter(getApplicationContext() , mCommentList);
        mCommentRecyclerView.setAdapter(mAdapter);


    }

}