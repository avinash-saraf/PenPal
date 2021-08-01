package com.example.penpal;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.Random;


public class SearchFragment extends Fragment {

    private View root;
    private Button findButton;

    private DatabaseReference rootRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentUserId = mAuth.getUid();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);

        rootRef = FirebaseDatabase.getInstance().getReference();

        findButton = root.findViewById(R.id.find_penpal_button);
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseRandomInterest();
            }
        });

        return root;
    }

    // randomly selects an interest of the current user
    private void ChooseRandomInterest(){
    rootRef.child("Users").child(currentUserId).child("interests").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            if(snapshot.exists()){
                int numInterests = (int) snapshot.getChildrenCount();
                Random random = new Random();
                int rand = random.nextInt(numInterests);
                Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                for(int i=0;i<rand;i++){
                    iterator.next();
                }
                DataSnapshot interestSnapshot = (DataSnapshot) iterator.next();
                String interestName = Objects.requireNonNull(interestSnapshot.child("name").getValue()).toString();
                FindRandomUserWithSameInterest(interestSnapshot);

            }
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    });
    }

    public void FindRandomUserWithSameInterest(DataSnapshot interestSnapshot){
        rootRef.child("Interests").child(Objects.requireNonNull(interestSnapshot.child("type").getValue()).toString())
                .child(Objects.requireNonNull(interestSnapshot.child("name").getValue()).toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 1){
                    int usersCount = (int) snapshot.getChildrenCount();
                    Random random = new Random();
                    int rand = random.nextInt(usersCount);
                    Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                    for(int i=0;i<rand;i++){
                        iterator.next();
                    }
                    DataSnapshot userSnapshot = (DataSnapshot) iterator.next();
                    if(Objects.equals(userSnapshot.getKey(), currentUserId) ){
                        if (iterator.hasNext()){
                            userSnapshot = (DataSnapshot) iterator.next();
                            String userID = userSnapshot.getKey();

                            CheckIfRandomUserInContactList(userID);
                        }
                        else
                            Toast.makeText(root.getContext(), "User same as current user... Try Again", Toast.LENGTH_SHORT).show();
                    } else {
                        String userID = userSnapshot.getKey();
                        CheckIfRandomUserInContactList(userID);
                    }

                } else {
                    FindRandomUserWithSameInterestType(Objects.requireNonNull(interestSnapshot.child("type").getValue()).toString());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void CheckIfRandomUserInContactList(String userID) {
        rootRef.child("Contacts").child(currentUserId).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    SendChatRequest(userID);
                } else{
                    Toast.makeText(root.getContext(), "User Already In Contacts, Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    //only a problem with a small user base
    private void FindRandomUserWithSameInterestType(String interestType) {
        Toast.makeText(root.getContext(), "No user found with same interest/hobby, Try Searching Again or Add A Popular Interest", Toast.LENGTH_SHORT).show();

    }

    private void SendChatRequest(String recieverUserId) {
        String senderUserId = currentUserId;
        rootRef.child("Chat Requests").child(senderUserId).child(recieverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            rootRef.child("Chat Requests").child(recieverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                                rootRef.child("Users").child(recieverUserId).child("displayname").addListenerForSingleValueEvent( new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()){
                                                            Toast.makeText(root.getContext(), "Chat Request Sent to " + Objects.requireNonNull(snapshot.getValue()).toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

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
