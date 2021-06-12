package com.example.penpal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatsFragment extends Fragment {

    private View chatsView;
    private RecyclerView chatsList;
    private DatabaseReference messagesRef, userRef;

    private FirebaseAuth mAuth;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        chatsView = inflater.inflate(R.layout.fragment_chats, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        chatsList = chatsView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        //puts a divider(black line) between every two 'chats'
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        chatsList.addItemDecoration(itemDecoration);

        return chatsView;

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Messages> options
                = new FirebaseRecyclerOptions.Builder<Messages>()
                .setQuery(messagesRef, Messages.class)
                .build();

        FirebaseRecyclerAdapter<Messages, ChatsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Messages, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder chatsViewHolder, int position, @NonNull Messages messages) {
                final String userId = getRef(position).getKey();

                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                            final String userName = dataSnapshot.child("displayname").getValue().toString();

                            chatsViewHolder.userName.setText(userName);


                        chatsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra("visit_user_id", userId);
                                chatIntent.putExtra("visit_user_name", userName);
                                startActivity(chatIntent);
                            }
                        });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // gets the last message sent between two users and displays it in the view holder
                messagesRef.child(userId).orderByKey().limitToLast(1)
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                if(snapshot.exists()){
                                    Messages message = snapshot.getValue(Messages.class);
                                    String lastMsg = message.getMessage();
                                    String type = message.getType();
                                    if(type.equals("image")){
                                        lastMsg = "image";
                                    }
                                    chatsViewHolder.lastmessage.setText(lastMsg);
                                }
                                else {
                                    chatsViewHolder.lastmessage.setText("Start a conversation!");
                                }
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

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

        private TextView userName, lastmessage;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name);
            lastmessage = itemView.findViewById(R.id.user_status);
        }
    }
}

