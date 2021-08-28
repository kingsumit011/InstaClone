package com.android.example.instaclone.Adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.instaclone.ListDisplay.CommentActivity;
import com.android.example.instaclone.Model.Post;
import com.android.example.instaclone.Profile.SearchProfileFragment;
import com.android.example.instaclone.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {
    private static final String TAG = PostAdapter.class.toString();
    private final Context mContext;
    private final List<Post> mPostList;
    private final boolean isFragment;
    private FirebaseUser firebaseUser;
    private long postDoubleClickLastTime;
    private Activity mActivity;


    public PostAdapter(Context mContext, List<Post> mPostList, Activity mActivity ,boolean isFragment) {
        this.mContext = mContext;
        this.mPostList = mPostList;
        this.isFragment = isFragment;
        postDoubleClickLastTime = 0;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_layout, parent, false);

        return new PostAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPostList.get(position);
        holder.postDescription.setText(post.getDescription());
        holder.postTime.setText(post.getTimeDiff());
        Glide.with(holder.itemView).load(post.getImageUrl()).into(holder.postCon);
        String publisherID = post.getPublisher();

        FirebaseDatabase.getInstance().getReference().child("User").child(publisherID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.postUserName.setText(String.valueOf(snapshot.child("userName").getValue()));
                Glide.with(holder.itemView).load(snapshot.child("profileimg").getValue()).into(holder.postProfilePhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, " Eroor : " + error);
            }
        });

        holder.postUserName.setOnClickListener(v -> {
            if(post.getPublisher().equals(firebaseUser.getUid())){
                Toast.makeText(mActivity.getApplicationContext(), "Yoc can't see your own Profile here \n Go to Profile menu", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle args = new Bundle();
            args.putString("key", post.getPublisher());
            Fragment fragment = new SearchProfileFragment();
            fragment.setArguments(args);
            mActivity.getFragmentManager().beginTransaction().replace(R.id.fragment_container_view_tag, fragment).commit();
        });

        FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    holder.postLikeButton.setBackgroundResource(R.drawable.ic_baseline_favorite_red_24);
                    holder.isLiked = true;
                } else {
                    holder.postLikeButton.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
                    holder.isLiked = false;
                }
                String likeCount = snapshot.getChildrenCount() + (snapshot.getChildrenCount() > 1 ? " Likes" : " Like");
                holder.postCountLike.setText(likeCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Comment").child(post.getPostId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.postCountComment.setText(snapshot.getChildrenCount() + (snapshot.getChildrenCount() >1 ?" Comments" : " Comment"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.postLikeButton.setOnClickListener(v -> {

            if (holder.isLiked) {
                FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                Log.d(TAG, post.getPostId() + " : Post is disliked by : " + firebaseUser.getUid());
            } else {
                FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                Log.d(TAG, post.getPostId() + " : Post is liked by : " + firebaseUser.getUid());

            }
        });

        holder.postCon.setOnClickListener(v -> {
            if (System.currentTimeMillis() - postDoubleClickLastTime < 300) {
                postDoubleClickLastTime = 0;
                holder.postLikeButton.callOnClick();
            } else {
                postDoubleClickLastTime = System.currentTimeMillis();
            }
        });

        holder.postCommentButton.setOnClickListener(v -> {
            Intent intent = new Intent(mContext , CommentActivity.class);
            intent.putExtra("PostId" , post.getPostId());
            intent.putExtra("Publisher" , post.getPublisher());
            mContext.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private ImageView postProfilePhoto, postCon, postCommentButton, postLikeButton, likeanimation;

        private TextView postUserName, postDescription, postCountLike, postTime , postCountComment;
        private boolean isLiked;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            init(itemView);
        }

        private void init(View itemView) {
            postProfilePhoto = itemView.findViewById(R.id.post_profile_photo);
            postCon = itemView.findViewById(R.id.post_con);
            likeanimation = itemView.findViewById(R.id.like_animation);
            postLikeButton = itemView.findViewById(R.id.post_like_button);
            postCommentButton = itemView.findViewById(R.id.post_comment_button);
            postUserName = itemView.findViewById(R.id.postUserName);
            postDescription = itemView.findViewById(R.id.post_discription);
            postCountLike = itemView.findViewById(R.id.post_no_user_like);
            postTime = itemView.findViewById(R.id.post_time);
            postCountComment = itemView.findViewById(R.id.post_no_user_comment);
            isLiked = false;
        }
    }
}
