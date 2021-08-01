package com.example.penpal;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private View requestsView;
    private RecyclerView myRequestsList;
    private DatabaseReference chatRequestRef, userRef, contactsRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestsView= inflater.inflate(R.layout.fragment_requests, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        myRequestsList = requestsView.findViewById(R.id.chat_requests_list);
        myRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));



        return requestsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options
                = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatRequestRef.child(currentUserId), Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, RequestsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestsViewHolder requestsViewHolder, int position, @NonNull final Contacts contacts) {
                final String list_user_id = getRef(position).getKey();
                DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String type = dataSnapshot.getValue().toString();
                            if(type.equals("received")){
                                userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       final String requestUserName = dataSnapshot.child("displayname").getValue().toString();
                                       final String requestUserStatus = dataSnapshot.child("status").getValue().toString();

                                       requestsViewHolder.userName.setText(requestUserName);
                                       requestsViewHolder.userStatus.setText("wants to connect with you");

                                       requestsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               CharSequence options[] = new CharSequence[]{
                                                       "Accept","Cancel"
                                               };
                                               AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                               builder.setTitle(requestUserName + " Chat Request");
                                               builder.setItems(options, new DialogInterface.OnClickListener() {
                                                   @Override
                                                   public void onClick(DialogInterface dialog, int which) {
                                                       if(which ==0){
                                                           contactsRef.child(currentUserId).child(list_user_id).child("Contacts")
                                                                   .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<Void> task) {
                                                                   if(task.isSuccessful()){
                                                                       contactsRef.child(list_user_id).child(currentUserId).child("Contacts")
                                                                               .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                           @Override
                                                                           public void onComplete(@NonNull Task<Void> task) {
                                                                               if(task.isSuccessful()){
                                                                                   chatRequestRef.child(currentUserId).child(list_user_id)
                                                                                           .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                       @Override
                                                                                       public void onComplete(@NonNull Task<Void> task) {
                                                                                           if(task.isSuccessful()){
                                                                                               chatRequestRef.child(list_user_id).child(currentUserId)
                                                                                                       .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                   @Override
                                                                                                   public void onComplete(@NonNull Task<Void> task) {
                                                                                                       if(task.isSuccessful()){
                                                                                                           Toast.makeText(getContext(), "New contact added", Toast.LENGTH_SHORT).show();

                                                                                                       }
                                                                                                   }
                                                                                               });
                                                                                           }
                                                                                       }
                                                                                   });
                                                                               }
                                                                           }
                                                                       });
                                                                   }
                                                               }
                                                           });
                                                       }
                                                       if(which == 1){
                                                           chatRequestRef.child(currentUserId).child(list_user_id)
                                                                   .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<Void> task) {
                                                                   if(task.isSuccessful()){
                                                                       chatRequestRef.child(list_user_id).child(currentUserId)
                                                                               .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                           @Override
                                                                           public void onComplete(@NonNull Task<Void> task) {
                                                                               if(task.isSuccessful()){
                                                                                   Toast.makeText(getContext(), "Contact deleted", Toast.LENGTH_SHORT).show();

                                                                               }
                                                                           }
                                                                       });
                                                                   }
                                                               }
                                                           });
                                                       }
                                                   }
                                               });
                                               builder.show();
                                           }
                                       });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else if(type.equals("sent")){
                                Button request_sent_btn = requestsViewHolder.itemView.findViewById(R.id.request_accept_button);
                                request_sent_btn.setText("Req Sent");

                                requestsViewHolder.itemView.findViewById(R.id.request_decline_button).setVisibility(View.INVISIBLE);

                                userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final String requestUserName = dataSnapshot.child("displayname").getValue().toString();
                                        final String requestUserStatus = dataSnapshot.child("status").getValue().toString();

                                        requestsViewHolder.userName.setText(requestUserName);
                                        requestsViewHolder.userStatus.setText("You have sent a request to " + requestUserName);

                                        requestsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence options[] = new CharSequence[]{
                                                        "Cancel Chat Request"
                                                };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Already sent request");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if(which == 0){
                                                            chatRequestRef.child(currentUserId).child(list_user_id)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        chatRequestRef.child(list_user_id).child(currentUserId)
                                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isSuccessful()){
                                                                                    Toast.makeText(getContext(), "You have cancelled the chat request", Toast.LENGTH_SHORT).show();

                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.users_request_display_layout, parent,false);
                RequestsViewHolder viewHolder = new RequestsViewHolder(view);
                return viewHolder;
            }
        };
        myRequestsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder{

        TextView userName, userStatus;
        Button acceptChat, declineChat;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name_request);
            userStatus = itemView.findViewById(R.id.user_status_request);
            acceptChat = itemView.findViewById(R.id.request_accept_button);
            declineChat = itemView.findViewById(R.id.request_decline_button);

        }
    }
}
