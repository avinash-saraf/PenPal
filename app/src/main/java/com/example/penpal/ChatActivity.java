package com.example.penpal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverId, messageReceiverName, messageSenderId;
    private String saveCurrentTime, saveCurrentDate;
    private String checker="", myUrl;

    private TextView userName, userLastSeen;

    private Toolbar chatToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private ImageButton sendMessageButton, sendFileButton;
    private EditText userMessageInput;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;

    private StorageTask uploadTask;
    private Uri fileUri;
    private Bitmap compressedBitmap;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth =FirebaseAuth.getInstance();
        messageSenderId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        messageReceiverId = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();

        InitializeControllers();

        userName.setText(messageReceiverName);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        displayLastSeen();

        sendFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[] {
                    "Images", "PDF Files", "Ms Word Files"
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select File");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            checker = "image";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Select Image"), 438);
                        }
                        if(which == 1){
                            checker = "pdf";
                        }
                        if(which == 2){
                            checker = "docx";
                        }
                    }
                });
               builder.show();
            }
        });
    }

    private void InitializeControllers() {

        chatToolbar = findViewById(R.id.chat_bar_layout);
        setSupportActionBar(chatToolbar);
        getSupportActionBar().setTitle(null);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        // inflater to inflate custom app bar layout for chat activity
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // custom actionBarView is inflated by inflater
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        // actionBar is displayed
        actionBar.setCustomView(actionBarView);

        userName = findViewById(R.id.custom_profile_name);
        userLastSeen = findViewById(R.id.custom_user_last_seen);

        sendMessageButton = findViewById(R.id.send_message_button);
        sendFileButton = findViewById(R.id.send_files_button);
        userMessageInput = findViewById(R.id.input_message);

        // initialize custom adapter to display messages
        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = findViewById(R.id.messages_list_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        progressDialog = new ProgressDialog(this);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==438 && resultCode == RESULT_OK && data!=null && data.getData() != null){

            progressDialog.setTitle("Sending File");
            progressDialog.setMessage("Please wait, we are sending your file ");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            fileUri = data.getData();
            if(!checker.equals("image")){


            } else if(checker.equals("image")){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

                final String messageSenderRef = "Messages/" + messageSenderId + "/" + messageReceiverId;
                final String messageReceiverRef = "Messages/" + messageReceiverId + "/" + messageSenderId;

                // created unique key for each message
                DatabaseReference userMessageKeyRef = rootRef.child("Messages").child(messageSenderId)
                        .child(messageReceiverId).push();

                final String messagePushId = userMessageKeyRef.getKey();

                //final StorageReference filePath = storageReference.child(messagePushId + "." + "jpg");

                File compressedFilePathUri = new File(fileUri.getPath());

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(new File(fileUri.getPath()).getAbsolutePath(), options);
                int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;


                try{
                    compressedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                    compressedBitmap = new Compressor(this)
                            .setMaxWidth(imageHeight+10)
                            .setMaxHeight(imageWidth+10)
                            .setQuality(30)
                            .compressToBitmap(compressedFilePathUri);

                } catch (IOException e){
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                final byte[] compressedByte = byteArrayOutputStream.toByteArray();

                final StorageReference compressedFilePath = storageReference.child(messagePushId + "." + "jpg");

                uploadTask = compressedFilePath.putBytes(compressedByte);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return compressedFilePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();

                            Map messageImageBody = new HashMap();
                            messageImageBody.put("message", myUrl);
                            messageImageBody.put("name", fileUri.getLastPathSegment());
                            messageImageBody.put("type", checker);
                            messageImageBody.put("from", messageSenderId);
                            messageImageBody.put("to", messageReceiverId);
                            messageImageBody.put("messageID", messagePushId);
                            messageImageBody.put("time", saveCurrentTime);
                            messageImageBody.put("date", saveCurrentDate);

                            Map messageBodyDetails= new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageImageBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushId, messageImageBody);

                            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Toast.makeText(ChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                                    } else{
                                        progressDialog.dismiss();
                                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                    userMessageInput.setText("");
                                }
                            });
                        }
                    }
                });

            } else{
                progressDialog.dismiss();
                Toast.makeText(this, "Nothing Selected, Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displayLastSeen(){
        rootRef.child("Users").child(messageReceiverId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("userState").hasChild("state")){
                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();
                            if(state.equals("online")) {
                                userLastSeen.setText("online");
                            } else if(state.equals("offline")){
                                userLastSeen.setText("Last seen: " + " " + date + " " + time);

                            }
                        } else{
                            userLastSeen.setText("offline");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        rootRef.child("Messages").child(messageSenderId).child(messageReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        messagesList.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
        messagesList.clear();
    }

    private void sendMessage(){
        String messageText = userMessageInput.getText().toString();
        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(this, "First write your message", Toast.LENGTH_SHORT).show();
        } else{
            String messageSenderRef = "Messages/" + messageSenderId + "/" + messageReceiverId;
            String messageReceiverRef = "Messages/" + messageReceiverId + "/" + messageSenderId;

            // created unique key for each message
            DatabaseReference userMessageKeyRef = rootRef.child("Messages").child(messageSenderId)
                    .child(messageReceiverId).push();

            String messagePushId = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderId);
            messageTextBody.put("to", messageReceiverId);
            messageTextBody.put("messageID", messagePushId);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);

            Map messageBodyDetails= new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushId, messageTextBody);

            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    userMessageInput.setText("");
                }
            });

        }

    }


}
