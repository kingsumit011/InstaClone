package com.android.example.instaclone.Profile;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.instaclone.Adapter.PostAdapter;
import com.android.example.instaclone.Model.Post;
import com.android.example.instaclone.R;
import com.android.example.instaclone.home.HomeFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostZoomFragment extends Fragment {

    private static final String TAG = HomeFragment.class.toString();
    private RecyclerView mRecycleView;
    private List<Post> mPost;
    private PostAdapter mPostAdapter;
    private String publisherID, postId;
    private int position;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_zoom, container, false);
        init(view);
        publisherID = getArguments().getString("key");
        postId = getArguments().getString("position");
        addPost();
        return view;
    }

    private void addPost() {
        FirebaseDatabase.getInstance().getReference().child("Posts").orderByChild("time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPost.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    Post post = snapshot1.getValue(Post.class);

                    if (post.getPublisher().equals(publisherID)) {
                        mPost.add(0, post);
                    }

                    position++;

                    if (post.getPostId().equals(postId)) {
                        Log.d(TAG, "Post Found");
                        position = 0;
                    }
                }

                mPostAdapter.notifyDataSetChanged();
                //TODO
//                Log.d(TAG , "Post Found At position "+ position);
//                mRecycleView.smoothScrollToPosition(position);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init(View view) {
        mRecycleView = view.findViewById(R.id.post_con);
        mRecycleView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(layoutManager);

        mPost = new ArrayList<>();
        mPostAdapter = new PostAdapter(getContext(), mPost, getActivity(), true);
        mRecycleView.setAdapter(mPostAdapter);

    }
}