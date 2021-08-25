package com.android.example.instaclone.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.instaclone.Model.User;
import com.android.example.instaclone.R;
import com.android.example.instaclone.utils.OnItemCustomClickListner;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewHolder> {
    private static final String TAG = UserAdapter.class.toString();
    private Context mContext;
    private List<User> mUserList;
    private boolean isFragment;
    private FirebaseUser firebaseUser;
    private OnItemCustomClickListner<User> listner;
    public UserAdapter(Context mContext, List<User> mUserList, boolean isFragment , OnItemCustomClickListner<User> listner) {
        this.mContext = mContext;
        this.mUserList = mUserList;
        this.isFragment = isFragment;
        this.listner = listner;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_user_search_item, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.bind(mUserList.get(position),listner);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = mUserList.get(position);
        holder.follow.setVisibility(View.VISIBLE);
        holder.userName.setText(user.getUserName());
        Glide.with(holder.itemView).load(user.getProfileimg()).into(holder.userProfilePhoto);
        isFollowed(user.getId(), holder.follow);
        if (user.getId().equals(firebaseUser.getUid())) {
            holder.follow.setVisibility(View.GONE);
        }
        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.follow.getText().toString().equals("Follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).
                            child("Following").child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).
                            child("Followers").child(firebaseUser.getUid()).setValue(true);

                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).
                            child("Following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).
                            child("Followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
    }

    private void isFollowed(String id, Button follow) {
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following");
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(id).exists()) {
                    follow.setText(R.string.following);
                } else {
                    follow.setText(R.string.follow);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "error - " + error);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        public ImageView userProfilePhoto;
        public TextView userName;
        public AutoCompleteTextView searchBar;
        public Button follow;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            initWidget(itemView);
        }

        private void initWidget(View view) {
            userProfilePhoto = view.findViewById(R.id.search_user_photo);
            userName = view.findViewById(R.id.search_user_username);
            follow = view.findViewById(R.id.followButton);
            searchBar = view.findViewById(R.id.search_bar);
        }

        public void bind( User item, OnItemCustomClickListner listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.OnItemClick(item);

                }
            });
        }
    }
}
