package com.android.example.instaclone.Adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {
    private static final String TAG = PostAdapter.class.toString();
    private final Context mContext;
    private final List<Post> mPostList;
    private final boolean isFragment;
    private final Activity mActivity;
    private final View view;
    private FirebaseUser firebaseUser;
    private long postDoubleClickLastTime;
    private final int PERMISSION_WRITE = 0;

    public PostAdapter(Context mContext, List<Post> mPostList, Activity mActivity, boolean isFragment, View view) {
        this.mContext = mContext;
        this.mPostList = mPostList;
        this.isFragment = isFragment;
        postDoubleClickLastTime = 0;
        this.mActivity = mActivity;
        this.view = view;
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
                Glide.with(mContext).load(snapshot.child("profileimg").getValue()).into(holder.postProfilePhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, " Eroor : " + error);
            }
        });

        holder.postUserName.setOnClickListener(v -> {
            if (post.getPublisher().equals(firebaseUser.getUid())) {
                Toast.makeText(mActivity.getApplicationContext(), "Yoc can't see your own Profile here \n Go to Profile menu", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle args = new Bundle();
            args.putString("key", post.getPublisher());
            Navigation.findNavController(view).navigate(R.id.action_fragment_home_to_fragment_search_profile, args);

        });

        FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()) {
                    holder.postLikeButton.setBackgroundResource(R.drawable.ic_baseline_favorite_red_24);
                    holder.isLiked = true;
                } else {
                    holder.postLikeButton.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
                    holder.isLiked = false;
                }
                Log.d(TAG, " post " + holder.isLiked);
                String likeCount = snapshot.getChildrenCount() + (snapshot.getChildrenCount() > 1 ? " Likes" : " Like");
                holder.postCountLike.setText(likeCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Error: " + error);

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Comment").child(post.getPostId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.postCountComment.setText(snapshot.getChildrenCount() + (snapshot.getChildrenCount() > 1 ? " Comments" : " Comment"));
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

            Bundle bundle = new Bundle();
            bundle.putString("PostId", post.getPostId());
            bundle.putString("Publisher", post.getPublisher());
            Navigation.findNavController(view).navigate(R.id.action_fragment_home_to_fragment_Comment, bundle);
        });

        holder.setting.setOnClickListener(v -> {

            PopupMenu popup = new PopupMenu(mContext, holder.setting);
            popup.inflate(R.menu.post_menu);
            if (!post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                popup.getMenu().findItem(R.id.delete_post).setEnabled(false);
            }
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.delete_post:
                        postDelete(post);
                        return true;
                    case R.id.share_post:
                        try {
                            shareImage(post, holder.postCon);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    default:
                        return false;
                }

            });
            popup.show();
        });

    }

    public boolean checkPermission() {
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
        int WRITE_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if ((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_WRITE);
            return false;
        }
        if ((WRITE_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE);
            return false;
        }

        return true;
    }

    private void shareImage(Post post, ImageView content) throws IOException {

        if (!checkPermission()) {
            Log.d(TAG, " Permission Not Available");
            return;
        }

        StorageReference islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(post.getImageUrl());

        File localFile = File.createTempFile("images", "jpg");

        islandRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {

            // Local temp file has been created
            Bitmap myImage = BitmapFactory.decodeFile(localFile.getAbsolutePath());
            final Uri uri = getImageUri(mContext, myImage);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, post.getDescription());
            try {
                mContext.startActivity(Intent.createChooser(shareIntent, " Share image"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }


    private void postDelete(Post post) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Delete Post");
        builder.setMessage("Are you Sure ? ");

        builder.setPositiveButton("YES", (dialog, which) -> {
            FirebaseDatabase.getInstance().getReference().child("Posts").child(post.getPostId()).removeValue();
            dialog.dismiss();
        });
        builder.setNegativeButton("NO", ((dialog, which) -> {
            dialog.dismiss();
        }));
        builder.show();

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Instacache", null);
        return Uri.parse(path);
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private ImageView postProfilePhoto, postCon, postCommentButton, postLikeButton, likeanimation, setting;

        private TextView postUserName, postDescription, postCountLike, postTime, postCountComment;
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
            setting = itemView.findViewById(R.id.post_title_setting);
        }
    }
}
