package com.android.example.instaclone.ListDisplay;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.instaclone.Adapter.UserAdapter;
import com.android.example.instaclone.Model.User;
import com.android.example.instaclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowerFollowing extends Fragment {
    private static final String TAG = FollowerFollowing.class.toString();
    private RecyclerView mRecyclerView;
    private ImageView close;
    private List<User> mUserList;
    private String userId, list;
    private UserAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_follower_following, container, false);
        userId = getArguments().getString("User");
        list = getArguments().getString("List");

        Toolbar toolbar = view.findViewById(R.id.toolBar);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        init(view);
        return view;
    }

    private void init(View view) {
        mUserList = new ArrayList<>();
        close = view.findViewById(R.id.back);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.hasFixedSize();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new UserAdapter(getContext(), mUserList, false, user -> {
            if(user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                Toast.makeText(getContext(), "Can't See Your own Profile", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle args = new Bundle();
            args.putString("key", user.getId());
            Navigation.findNavController(getView()).navigate(R.id.action_fragment_FollowersFollowing_to_fragment_search_profile , args);
        });
        mRecyclerView.setAdapter(mAdapter);
        FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).child(list).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String user = dataSnapshot.getKey();
                    addUser(user);
                    Log.d(TAG, "User added" + user + " " + mUserList.size());
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        close.setOnClickListener(v -> {

        });
    }

    private void addUser(String user) {

        FirebaseDatabase.getInstance().getReference().child("User").child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUserList.add(snapshot.getValue(User.class));
                mAdapter.notifyDataSetChanged();
                Log.d(TAG, "User added" + user + " " + mUserList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "error" + error);
            }
        });
    }
}