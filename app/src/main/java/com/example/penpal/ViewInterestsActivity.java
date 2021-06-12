package com.example.penpal;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.penpal.interests.SelectedInterestsFragment;
import com.example.penpal.interests.model.Interest;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ViewInterestsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private String recieverUserId;

    private DatabaseReference interestsRef;

    private FirebaseRecyclerAdapter<Interest, ViewInterestsActivity.InterestsViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_interests);

        recieverUserId = getIntent().getExtras().get("visit_user_id").toString();

        mRecyclerView = findViewById(R.id.view_interests_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        interestsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(recieverUserId).child("interests");

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Interest>()
                .setQuery(interestsRef, Interest.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Interest, ViewInterestsActivity.InterestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewInterestsActivity.InterestsViewHolder interestsViewHolder, int position, @NonNull Interest interest) {
                String hobbyname = getRef(position).getKey();

                interestsRef.child(hobbyname).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String hobbyimageurl = snapshot.child("image").getValue().toString();
                            String hobbyname = snapshot.child("name").getValue().toString();
                            interestsViewHolder.hobbyName.setText(hobbyname);

                            Glide.with(interestsViewHolder.itemView.getContext()).load(hobbyimageurl).centerCrop().placeholder(R.drawable.profile_icon).into(interestsViewHolder.hobbyImage);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public ViewInterestsActivity.InterestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.interest_cardview, parent, false);
                ViewInterestsActivity.InterestsViewHolder viewHolder = new ViewInterestsActivity.InterestsViewHolder(view);
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
