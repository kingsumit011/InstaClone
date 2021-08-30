package com.android.example.instaclone.Profile;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.instaclone.Adapter.ProfilePhotoAdapter;
import com.android.example.instaclone.Model.Post;
import com.android.example.instaclone.R;
import com.android.example.instaclone.utils.OnItemCustomClickListner;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchProfileFragment extends Fragment {
    private static final String TAG = SearchProfileFragment.class.toString();
    private static final int SPAN = 2;
    private FirebaseUser firebaseUser;
    private ImageView userPhoto;
    private TextView postCount , followCount , followerCount,userbio , followButton , messageButon , userName;
    private RecyclerView photoLayout;
    private  String userId ;
    private ProfilePhotoAdapter mAdapter;
    private List<Post> mPostList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_profile, container, false);
        init(view);
        Toolbar toolbar = view.findViewById(R.id.toolBar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
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
        getPost(view);
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

    private void getPost(View view) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Posts");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int post_count =0;
                mPostList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Post post=snapshot1.getValue(Post.class);
                    Log.d(TAG , " PostClass created");
                    if(post.getPublisher().equals(userId)){
                        Log.d(TAG , " Post is Added in adapter");
                        post_count++;
                        mPostList.add(0 , post);
                    }
                }
                mAdapter.notifyDataSetChanged();
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
        followerCount.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("User",userId);
            bundle.putString("List","Followers");
            Navigation.findNavController(getView()).navigate(R.id.action_fragment_search_profile_to_fragment_FollowersFollowing , bundle);
        });

        followCount.setOnClickListener((v -> {
            Bundle bundle = new Bundle();
            bundle.putString("User",userId);
            bundle.putString("List","Following");
            Navigation.findNavController(getView()).navigate(R.id.action_fragment_search_profile_to_fragment_FollowersFollowing , bundle);
        }));
    }

    private void init(View view) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userPhoto = view.findViewById(R.id.user_profile_photo);
        postCount = view.findViewById(R.id.count_post);
        followCount = view.findViewById(R.id.count_following);
        followerCount = view.findViewById(R.id.count_followers);
        followButton = view.findViewById(R.id.profile_search_follow_button);
        messageButon = view.findViewById(R.id.profile_search_message_buttom);
        photoLayout = view.findViewById(R.id.profile_post);
        photoLayout.setLayoutManager(new GridLayoutManager(getContext() , SPAN));
        userbio = view.findViewById(R.id.user_bio_data);
        userName= view.findViewById(R.id.username);
        mPostList = new ArrayList<>();
        mAdapter =new ProfilePhotoAdapter(getContext() , mPostList ,new OnItemCustomClickListner<Post>() {
            @Override
            public void OnItemClick(Post user) {
                Bundle args = new Bundle();
                args.putString("key", user.getPublisher());
                args.putString("position" , user.getPostId());
                Navigation.findNavController(getView()).navigate(R.id.action_fragment_search_profile_to_fragment_profile_post, args);

            }

        });
        photoLayout.setAdapter(mAdapter);
    }

}