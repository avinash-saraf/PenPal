package com.example.penpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.penpal.interests.InterestsActivity;
import com.example.penpal.welcome.LoginActivity;
import com.example.penpal.welcome.SetupActivity;
import com.example.penpal.welcome.WelcomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;;
    private Fragment selectedFragment = null;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef, userRef, groupRef;
    private String TAG = "MainActivity", currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");
       // currentId = mAuth.getCurrentUser().getUid();

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("PenPal");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        mToolbar.showOverflowMenu();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new ChatsFragment()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()){
                        case R.id.bottom_nav_home:
                            selectedFragment = new ChatsFragment();
                            break;
                        case R.id.bottom_nav_search_penpal:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.bottom_nav_profile:
                            selectedFragment = new ContactsFragment();
                            break;
                        case R.id.bottom_nav_requests:
                            selectedFragment = new RequestsFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;

                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.overflow_setting:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                sendUserToSettingsActivity();
                return true;
            case R.id.overflow_find_friends:
                Toast.makeText(this, "Find Friends", Toast.LENGTH_SHORT).show();
                SendUserToFindActivity();
                return true;
            case R.id.overflow_logout:
                Toast.makeText(this, "Signing Out...", Toast.LENGTH_SHORT).show();
                updateUserStatus("offline");
                mAuth.signOut();
                SendUserToWelcomeActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            SendUserToLoginActivity();
        }
        else {
            updateUserStatus("online");

            CheckUserExistenceInDatabase();

        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
           updateUserStatus("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            updateUserStatus("offline");
        }

    }

    private void CheckUserExistenceInDatabase() {
        final String userId = mAuth.getCurrentUser().getUid();
        ValueEventListener valueEventListener =
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       if(!dataSnapshot.child(userId).hasChild("displayname")){
                            updateUserStatus("online");

                            SendUserToSetupActivity();

                        }
                        else if (!dataSnapshot.child(userId).hasChild("interests")){
                            updateUserStatus("online");

                            SendUserToInterestsActivity();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
        userRef.addValueEventListener(valueEventListener);
    }


    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    private void SendUserToWelcomeActivity() {
        Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
        welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(welcomeIntent);
        finish();
    }

    private void sendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);

    }
    private void SendUserToInterestsActivity() {
        Intent interestsActivity = new Intent(MainActivity.this, InterestsActivity.class);
        interestsActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(interestsActivity);
        startActivity(interestsActivity);

    }

    private void SendUserToFindActivity(){
        Intent friendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(friendsIntent);
    }

    private void updateUserStatus(String state){
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        rootRef.child("Users").child(currentUserId).child("userState")
                .updateChildren(onlineStateMap);

    }

    /*

    private void RequestNewGroup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name: ");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("E.g. Bruh");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();

                if(TextUtils.isEmpty(groupName)){
                    Toast.makeText(MainActivity.this, "Enter a group name...", Toast.LENGTH_SHORT).show();

                } else {
                    CreateNewGroup(groupName);

                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        builder.show();

    }

    private void CreateNewGroup(final String groupName){
        /*
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        saveCurrentDate = currentDate.format(callForDate.getTime());

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime  = new SimpleDateFormat("HH:mm", Locale.getDefault());
        saveCurrentTime = currentTime.format(callForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        String firebaseGroupName = groupName +"_"+ currentId + "_" + postRandomName;

        groupRef.child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
                Toast.makeText(MainActivity.this,groupName+  "  has been created successfully", Toast.LENGTH_SHORT).show();
            }

        }
    });

}
     */


}