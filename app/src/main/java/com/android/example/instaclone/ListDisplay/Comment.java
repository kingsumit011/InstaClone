package com.android.example.instaclone.ListDisplay;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.instaclone.Adapter.CommentAdapter;
import com.android.example.instaclone.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Comment extends Fragment {
    private static final String TAG = Comment.class.toString();
    private String postId, publisher;
    private ImageView userImage, backButton;
    private RecyclerView mCommentRecyclerView;

    private List<com.android.example.instaclone.Model.Comment> mCommentList;
    private CommentAdapter mAdapter;
    private TextView postButtom;
    private AutoCompleteTextView comment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolBar);
        ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getContext()).getSupportActionBar().setTitle("Comment");
        postId = getArguments().getString("PostId");
        publisher = getArguments().getString("Publisher");
        init(view);
        initWidget();
        return view;
    }


    private void initWidget() {
        FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getActivity() != null)
                    Glide.with(getContext()).load(Uri.parse(snapshot.child("profileimg").getValue().toString())).into(userImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, " Error : " + error);
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Comment").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mCommentList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    com.android.example.instaclone.Model.Comment comment = snapshot1.getValue(com.android.example.instaclone.Model.Comment.class);
                    Log.d(TAG, " Comment is added in list " + comment.getCommentdata());
                    mCommentList.add(0, comment);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, " Error : " + error);
            }
        });

        postButtom.setOnClickListener(v -> {
            if (comment.getText().toString() == "") {
                Toast.makeText(getContext(), "Enter Comment", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadComment();
        });
        backButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().remove(Comment.this).commit();
            getActivity().onBackPressed();
        });
    }

    private void uploadComment() {
        String commentID = FirebaseDatabase.getInstance().getReference().child("Comment").child(postId).push().getKey();
        HashMap<String, Object> temp = new HashMap<>();
        temp.put("id", commentID);
        temp.put("commentdata", comment.getText().toString());
        temp.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
        temp.put("time", System.currentTimeMillis());
        Log.d(TAG, "Comment is Uploading");
        FirebaseDatabase.getInstance().getReference().child("Comment").child(postId).child(commentID).setValue(temp);
        comment.setText("");
    }

    private void init(View view) {

        userImage = view.findViewById(R.id.user_profile_photo);
        backButton = view.findViewById(R.id.close_button);
        postButtom = view.findViewById(R.id.comment_post_button);
        comment = view.findViewById(R.id.post_comment);
        mCommentList = new ArrayList<>();
        mCommentRecyclerView = view.findViewById(R.id.comment_Con);
        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCommentRecyclerView.setHasFixedSize(true);
        mAdapter = new CommentAdapter(getContext(), mCommentList);
        mCommentRecyclerView.setAdapter(mAdapter);

    }
}