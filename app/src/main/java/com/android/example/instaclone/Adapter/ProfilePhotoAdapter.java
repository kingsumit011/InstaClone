package com.android.example.instaclone.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.instaclone.Model.Post;
import com.android.example.instaclone.R;
import com.android.example.instaclone.utils.OnItemCustomClickListner;
import com.bumptech.glide.Glide;

import java.util.List;

public class ProfilePhotoAdapter extends RecyclerView.Adapter<ProfilePhotoAdapter.viewHolder>{
    private static final String TAG = ProfilePhotoAdapter.class.toString();
    private final List<Post> mPostList;
    private final Context mContext;
    private OnItemCustomClickListner<Post> listner;
    public ProfilePhotoAdapter(Context mContext, List<Post> mPostList , OnItemCustomClickListner<Post> listner) {
        this.mPostList = mPostList;
        this.mContext = mContext;
        this.listner = listner;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.profile_photo_layout, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Post post = mPostList.get(position);
        Log.d(TAG, " Post updated");
        holder.bind(post , listner);
        Glide.with(holder.itemView).load(post.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }


    class viewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profile_post_photo);

        }

        public void bind(Post post, OnItemCustomClickListner<Post> listner) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listner.OnItemClick(post);

                }
            });
        }
    }
}
