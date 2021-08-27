package com.android.example.instaclone.search;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.instaclone.Adapter.UserAdapter;
import com.android.example.instaclone.Model.User;
import com.android.example.instaclone.Profile.SearchProfileFragment;
import com.android.example.instaclone.R;
import com.android.example.instaclone.utils.OnItemCustomClickListner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private static final String TAG = SearchFragment.class.toString();

    private RecyclerView mRecyclerView;
    private List<User> mUser;
    private UserAdapter mUserAdapter;
    private AutoCompleteTextView mSearchBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        init(view);

        readUser();
        mRecyclerView.setAdapter(mUserAdapter);
        mSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void searchUser(String s) {
        Query query = FirebaseDatabase.getInstance().getReference().child("User").orderByChild("userName").startAt(s).endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mUser.add(dataSnapshot.getValue(User.class));
                }
                mUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUser() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(mSearchBar.getText().toString())) {
                    mUser.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        mUser.add(user);
                    }
                    mUserAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error" + error);
            }
        });
    }

    private void init(View view) {
        mRecyclerView = view.findViewById(R.id.search_list_root_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUser = new ArrayList<>();
        mUserAdapter = new UserAdapter(getContext(), mUser, true, new OnItemCustomClickListner<User>() {
            @Override
            public void OnItemClick(User user) {
                if(user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    return;
                }
                Bundle args = new Bundle();
                args.putString("key", user.getId());
                Fragment fragment = new SearchProfileFragment();
                fragment.setArguments(args);
                getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_container_view_tag, fragment).commit();
            }
        });
        mSearchBar = view.findViewById(R.id.search_bar);
    }
}