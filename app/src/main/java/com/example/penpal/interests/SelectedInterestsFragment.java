package com.example.penpal.interests;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.penpal.Contacts;
import com.example.penpal.ContactsFragment;
import com.example.penpal.R;
import com.example.penpal.interests.model.Interest;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectedInterestsFragment extends Fragment {

    private View root;

    private RecyclerView mRecyclerView;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentUser = mAuth.getCurrentUser().getUid();
    private DatabaseReference interestsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("interests");

    private FirebaseRecyclerAdapter<Interest, InterestsViewHolder> adapter;


    public SelectedInterestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_selected_interests, container, false);

        mRecyclerView = root.findViewById(R.id.interests_selected_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Interest>()
                .setQuery(interestsRef, Interest.class)
                .build();

         adapter = new FirebaseRecyclerAdapter<Interest, InterestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SelectedInterestsFragment.InterestsViewHolder interestsViewHolder, int position, @NonNull Interest interest) {
                String hobbyname = getRef(position).getKey();

                interestsRef.child(hobbyname).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String hobbyimageurl = snapshot.child("image").getValue().toString();
                            String hobbyname = snapshot.child("name").getValue().toString();
                            interestsViewHolder.hobbyName.setText(hobbyname);

                            Glide.with(interestsViewHolder.itemView.getContext()).load(hobbyimageurl).centerCrop().placeholder(R.drawable.profile_icon).into(interestsViewHolder.hobbyImage);
                            interestsViewHolder.imageClicked.setVisibility(View.VISIBLE);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public SelectedInterestsFragment.InterestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.interest_cardview, parent, false);
                SelectedInterestsFragment.InterestsViewHolder viewHolder = new SelectedInterestsFragment.InterestsViewHolder(view);
                return viewHolder;
            }
        };

        mRecyclerView.setAdapter(adapter);
        adapter.startListening();


    }


    public static class InterestsViewHolder extends RecyclerView.ViewHolder{

        TextView hobbyName;
        ImageView hobbyImage, imageClicked;


        public InterestsViewHolder(@NonNull View itemView) {
            super(itemView);

            hobbyName = itemView.findViewById(R.id.interest_name);
            hobbyImage = itemView.findViewById(R.id.interest_imageView);
            imageClicked = itemView.findViewById(R.id.interest_clicked);


        }
    }





}
