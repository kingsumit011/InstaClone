package com.android.example.instaclone.Profile;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.example.instaclone.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SearchProfileFragment extends Fragment {
    private static final String TAG = SearchProfileFragment.class.toString();
    private FirebaseUser firebaseUser;
    private ImageView userPhoto;
    private TextView postCount , followCount , followerCount,userbio , followButton , messageButon , userName;
    private GridLayout gridLayout;
    private  String userId ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_profile, container, false);
        init(view);
        userId = getArguments().getString("key");
        isFollowed(userId, followButton);
        initWidget(view);
        updatePage(view);
        return view;
    }

    private void isFollowed(String userId, TextView followButton) {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(userId).exists()){
                    followButton.setText(R.string.following);
                }else{
                    followButton.setText(R.string.follow);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updatePage(View view) {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("User").child(userId);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName.setText(String.valueOf(snapshot.child("userName").getValue()));
                userbio.setText(String.valueOf(snapshot.child("bio").getValue()));
                Glide.with(view).load(Uri.parse(String.valueOf(snapshot.child("profileimg").getValue()))).into(userPhoto);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG , "error" + error);
            }
        });
        getPostCount(view);
        getFollowersCount(view);
    }

    private void getFollowersCount(View view) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Follow").child(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followerCount.setText(String.valueOf(snapshot.child("Followers").getChildrenCount()));
                followCount.setText(String.valueOf(snapshot.child("Following").getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG,"Error : " + error);
            }
        });
    }

    private void getPostCount(View view) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Post").orderByChild("publisher");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int post_count = (int) snapshot.child(userId).getChildrenCount();

                postCount.setText(String.valueOf(post_count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG , "EROOR : "+ error);
            }
        });
    }

    private void initWidget(View view) {
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (followButton.getText().toString().equals("Follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).
                            child("Following").child(userId).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).
                            child("Followers").child(firebaseUser.getUid()).setValue(true);

                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).
                            child("Following").child(userId).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).
                            child("Followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        messageButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });
    }

    private void init(View view) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userPhoto = view.findViewById(R.id.user_profile_photo);
        postCount = view.findViewById(R.id.count_post);
        followCount = view.findViewById(R.id.count_following);
        followerCount = view.findViewById(R.id.count_followers);
        followButton = view.findViewById(R.id.profile_search_follow_button);
        messageButon = view.findViewById(R.id.profile_search_message_buttom);
        gridLayout = view.findViewById(R.id.profile_post);
        userbio = view.findViewById(R.id.user_bio_data);
        userName= view.findViewById(R.id.username);
    }

}