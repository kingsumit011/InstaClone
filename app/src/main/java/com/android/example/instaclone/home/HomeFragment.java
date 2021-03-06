package com.android.example.instaclone.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.instaclone.Adapter.PostAdapter;
import com.android.example.instaclone.Model.Post;
import com.android.example.instaclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.toString();
    private RecyclerView mRecycleView;
    private List<Post> mPost;
    private PostAdapter mPostAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Toolbar toolbar = view.findViewById(R.id.home_tool_bar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        init(view);
        addPost();
        return view;
    }

    private void addPost() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Posts").orderByChild("time").
                limitToLast(50);
        query.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPost.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Post post = snapshot1.getValue(Post.class);
                    mPost.add(0, post);
                }
                mPostAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, " Error at 65 : " + error);
            }
        });
    }

    private void init(View view) {
        mRecycleView = view.findViewById(R.id.home_post_list_root_viewr);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        mPost = new ArrayList<>();
        mPostAdapter = new PostAdapter(getContext(), mPost, getActivity(), true, view);
        mRecycleView.setAdapter(mPostAdapter);
    }
}