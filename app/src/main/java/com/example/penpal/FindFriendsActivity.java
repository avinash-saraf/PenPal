package com.example.penpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView findFriendsRecyclerList;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        findFriendsRecyclerList = findViewById(R.id.find_friends_recycler_list);
        findFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(userRef, Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendsViewHolder findFriendsViewHolder, final int position, @NonNull Contacts contacts) {
                        findFriendsViewHolder.userName.setText(contacts.getDisplayname());
                        findFriendsViewHolder.userStatus.setText(contacts.getStatus());

                        findFriendsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_user_id = getRef(position).getKey();

                                Intent profileIntent = new Intent(  FindFriendsActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("visit_user_id", visit_user_id);
                                startActivity(profileIntent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent,false);
                        FindFriendsViewHolder viewHolder = new FindFriendsViewHolder(view);
                        return viewHolder;
                    }
                };
       findFriendsRecyclerList.setAdapter(adapter);
       adapter.startListening();
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{

        TextView userName, userStatus;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name);
            userStatus = itemView.findViewById(R.id.user_status);

        }
    }
}
