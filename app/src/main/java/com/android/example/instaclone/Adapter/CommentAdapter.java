package com.android.example.instaclone.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.instaclone.Model.Comment;
import com.android.example.instaclone.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewHolder> {
    private static final String TAG = CommentAdapter.class.toString();
    private final Context mContext;
    private final List<Comment> mCommentList;

    public CommentAdapter(Context mContext, List<Comment> mCommentList) {
        this.mContext = mContext;
        this.mCommentList = mCommentList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_layout, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Comment comment = mCommentList.get(position);
        FirebaseDatabase.getInstance().getReference().child("User").child(comment.getPublisher()).child("profileimg").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Glide.with(mContext).load(Uri.parse((String) snapshot.getValue())).into(holder.commentUserImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, " Error : " + error);
            }
        });
        holder.commentview.setText(comment.getCommentdata());
        holder.time.setText(comment.getTimeDiff());
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder {
        private final ImageView commentUserImage;
        private final TextView commentview;
        private final TextView time;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            commentview = itemView.findViewById(R.id.comment_Con);
            commentUserImage = itemView.findViewById(R.id.comment_user_photo);
            time = itemView.findViewById(R.id.comment_time);
        }
    }
}
