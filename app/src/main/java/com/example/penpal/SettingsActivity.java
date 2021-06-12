package com.example.penpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.penpal.interests.InterestsActivity;
import com.example.penpal.welcome.SetupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private EditText country, displayName, status;
    private Button updateAccountSettings, editInterests;

    private Spinner countrySpinner;
    private boolean isCountrySelected;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String currentUserId, selectedCountry;
    private ProgressDialog progressDialog;

    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        countrySpinner = findViewById(R.id.settings_spinner_country_name);

       // country = findViewById(R.id.settings_country);
        displayName = findViewById(R.id.settings_displayname);
        status = findViewById(R.id.settings_status);
        updateAccountSettings = findViewById(R.id.settings_update_button);
        editInterests = findViewById(R.id.settings_edit_interests_button);

        mToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

        RetrieveUserInfo();

        progressDialog = new ProgressDialog(this);

        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateAccountInformation();
            }
        });

        editInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interestsActivity = new Intent(SettingsActivity.this, InterestsActivity.class);
                startActivity(interestsActivity);
            }
        });

        getCountryList();

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = (String) parent.getItemAtPosition(position);
                Toast.makeText(SettingsActivity.this, selectedCountry + " selected", Toast.LENGTH_SHORT).show();
                isCountrySelected = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                isCountrySelected = false;
            }
        });
    }

    private void getCountryList(){
        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<>();
        String country;

        for(Locale locale: locales){
            country = locale.getDisplayCountry();
            if(country.length() !=  0 && !countries.contains(country)){
                countries.add(country);
            }
        }

        Collections.sort(countries,String.CASE_INSENSITIVE_ORDER);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,countries);
        countrySpinner.setAdapter(adapter);

    }

    private void UpdateAccountInformation() {
        //String countryname = country.getText().toString();
        String displayname = displayName.getText().toString();
        String statusInfo = status.getText().toString();

        if(!isCountrySelected){
            Toast.makeText(SettingsActivity.this, "Please enter your name...", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(displayname)){
            Toast.makeText(SettingsActivity.this, "Please enter your display name...", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(statusInfo)){
            Toast.makeText(SettingsActivity.this, "Please enter your status...", Toast.LENGTH_SHORT).show();
        } else{
            progressDialog.setTitle("Updating Account Information");
            progressDialog.setMessage("Please wait while we are updating your information");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            HashMap userMap = new HashMap();
            userMap.put("country", selectedCountry);
            userMap.put("displayname", displayname);
            userMap.put("status", statusInfo);
            userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){
                        SendUserToMainActivity();
                        Toast.makeText(SettingsActivity.this, "Your account is updated successfully!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                    else {
                        String messageException = task.getException().getMessage();
                        Toast.makeText(SettingsActivity.this, "Error Occurred: " + messageException, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            });

        }
    }

    private void RetrieveUserInfo() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("displayname"))){

                    String retrievedDisplayName = dataSnapshot.child("displayname").getValue().toString();
                    String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                    String retrieveCountry = dataSnapshot.child("country").getValue().toString();

                    displayName.setHint(retrievedDisplayName);
                    status.setHint(retrieveStatus);

                    ArrayAdapter myAdap = (ArrayAdapter) countrySpinner.getAdapter();

                    int spinnerPosition = myAdap.getPosition(retrieveCountry);

                    //set the default according to country name
                    countrySpinner.setSelection(spinnerPosition);


                } else {
                    Toast.makeText(SettingsActivity.this, "Update your profile information.", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

}
