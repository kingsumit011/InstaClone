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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {
    private static final String TAG = ProfileFragment.class.toString();
    private ArrayList<Uri> posturi;
    private ImageView profileImg;
    private TextView post_count_view , followers_count_view , count_following_view;
    private TextView bio_view , profile_edit_button;
    private GridLayout profile_post_view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        init(view);
        initWidget(view);
        updatePage(view);
        // TODO updatePost
//        updatePost(view);
        return view;
    }

//    private void updatePost(View view) {
//        GridAd
//    }

    private void updatePage(View view) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bio_view.setText(String.valueOf(snapshot.child("bio").getValue()));
                Glide.with(view).load(Uri.parse(String.valueOf(snapshot.child("profileimg").getValue()))).into(profileImg);
                post_count_view.setText(String.valueOf(snapshot.child("Post").getChildrenCount()));
                followers_count_view.setText(String.valueOf(snapshot.child("Following").getChildrenCount()));
                count_following_view.setText(String.valueOf(snapshot.child("Follow").getChildrenCount()));
                for(DataSnapshot dS : snapshot.child("Post").getChildren()){
//                    addPost(dS.getKey());
                 }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG , "error" + error);
            }
        });
    }
//TODO

//    private void addPost(String key) {
//        FirebaseDatabase.getInstance().getReference().child("Post").child("key").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                posturi.add(Uri.parse(String.valueOf(snapshot.child("imageUrl").getValue())));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void initWidget(View view) {
        //Complete Edit bio
        profile_edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
    }

    private void init(View view) {
        profileImg = view.findViewById(R.id.user_profile_photo);
        post_count_view = view.findViewById(R.id.count_post);
        followers_count_view = view.findViewById(R.id.count_followers);
        count_following_view = view.findViewById(R.id.count_following);
        bio_view = view.findViewById(R.id.user_bio_data);
        profile_edit_button = view.findViewById(R.id.profile_edit_bio_data);
        profile_post_view = view.findViewById(R.id.profile_post);
        posturi = new ArrayList<>();
    }

}