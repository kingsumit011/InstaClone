package com.android.example.instaclone.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.instaclone.Model.Post;
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
    private Context mContext;
    private List<Post> mPostList;
    private boolean isFragment;
    private FirebaseUser firebaseUser;
    private boolean isLiked;
    public PostAdapter(Context mContext, List<Post> mPostList, boolean isFragment) {
        this.mContext = mContext;
        this.mPostList = mPostList;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(mContext).inflate(R.layout.post_layout,parent ,false);

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
                Log.e(TAG ," Eroor : " + error);
            }
        });
        isLike(post.getPostId() , holder.postLikeButton , holder.postCountLike);
        holder.postLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLiked){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                }
            }
        });
    }

    private void isLike(String id, Button postLikeButton , TextView postCountLike) {

        FirebaseDatabase.getInstance().getReference().child("Likes").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    postLikeButton.setBackgroundResource(R.drawable.ic_baseline_favorite_red_24);
                    isLiked=true;
                }else{
                    postLikeButton.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
                    isLiked = false;
                }
                String likeCount = snapshot.getChildrenCount() +"Likes";
                postCountLike.setText(likeCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        private ImageView postProfilePhoto , postCon, postCommentButton;
        private TextView postUserName , postDescription , postCountLike , postTime;
        private Button postLikeButton;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            init(itemView);
        }

        private void init(View itemView) {
            postProfilePhoto =itemView.findViewById(R.id.post_profile_photo);
            postCon = itemView.findViewById(R.id.post_con);
            postLikeButton = itemView.findViewById(R.id.post_like_button);
            postCommentButton = itemView.findViewById(R.id.post_comment_button);
            postUserName = itemView.findViewById(R.id.postUserName);
            postDescription = itemView.findViewById(R.id.post_discription);
            postCountLike = itemView.findViewById(R.id.post_no_user_like);
            postTime = itemView.findViewById(R.id.post_time);
        }
    }
}
