package com.example.penpal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton sendMsgBttn;
    private EditText userMsgInput;
    private ScrollView mScrollView;
    private TextView displaytxtMsgs;

    private String currentGrpName, currentUserId, currentUserName, currentDate, currentTime;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef, groupNameRef, grpMessageKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGrpName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChatActivity.this, currentGrpName, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGrpName);

        InitializeFields();

        GetUserInfo();

        sendMsgBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessageInfoToDatabase();

                userMsgInput.setText("");

                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void InitializeFields() {
        mToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGrpName);

        sendMsgBttn = findViewById(R.id.group_chat_send_message_button);
        userMsgInput = findViewById(R.id.group_chat_input_message);
        displaytxtMsgs = findViewById(R.id.group_chat_text_display);
        mScrollView = findViewById(R.id.my_scroll_view);
    }


    private void GetUserInfo() {
        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentUserName = dataSnapshot.child("displayname").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SendMessageInfoToDatabase() {

        String msg = userMsgInput.getText().toString();
        String msgKey = groupNameRef.push().getKey(); //creates a key

        if(TextUtils.isEmpty(msg)){
            Toast.makeText(GroupChatActivity.this, "Please enter msg first..", Toast.LENGTH_SHORT).show();

        } else{
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime= currentTimeFormat.format(calForTime.getTime());

            HashMap<String, Object> grpMessageKey = new HashMap<>();
            groupNameRef.updateChildren(grpMessageKey);

            grpMessageKeyRef = groupNameRef.child(msgKey);

            HashMap<String, Object> msgInfoMap = new HashMap<>();
            msgInfoMap.put("name", currentUserName);
            msgInfoMap.put("message", msg);
            msgInfoMap.put("date", currentDate);
            msgInfoMap.put("time", currentTime);

            grpMessageKeyRef.updateChildren(msgInfoMap);

        }
    }

    private void DisplayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while(iterator.hasNext()){
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMsg = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

            displaytxtMsgs.append(chatName + ":\n" + chatMsg + "\n" + chatTime + "   " + chatDate + "\n\n\n");

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
}
