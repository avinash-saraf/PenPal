package com.example.penpal;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private View chatsView;
    private RecyclerView chatsList;
    private DatabaseReference chatsRef, userRef;

    private FirebaseAuth mAuth;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        chatsView = inflater.inflate(R.layout.fragment_home, container, false);

        chatsList = chatsView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        chatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return chatsView;

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options
                = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatsRef, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, ChatsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder chatsViewHolder, int position, @NonNull Contacts contacts) {
                final String userId = getRef(position).getKey();
                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                            final String retName = dataSnapshot.child("displayname").getValue().toString();
                            // final String retStatus = dataSnapshot.child("status").getValue().toString();

                            chatsViewHolder.userName.setText(retName);

                            if(dataSnapshot.child("userState").hasChild("state")){
                                String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                String time = dataSnapshot.child("userState").child("time").getValue().toString();
                                if(state.equals("online")) {
                                    chatsViewHolder.userStatus.setText("online");
                                } else if(state.equals("offline")){
                                    chatsViewHolder.userStatus.setText("Last seen: " + " " + date + " " + time);

                                }
                            } else{
                                chatsViewHolder.userStatus.setText("offline");

                            }


                        chatsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra("visit_user_id", userId);
                                chatIntent.putExtra("visit_user_name", retName);
                                startActivity(chatIntent);
                            }
                        });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                return new ChatsViewHolder(view);
            }
        };
        chatsList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder{

        TextView userStatus, userName;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name);
            userStatus = itemView.findViewById(R.id.user_status);
        }
    }
}

