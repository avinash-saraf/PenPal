package com.example.penpal.welcome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.penpal.MainActivity;
import com.example.penpal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class SetupActivity extends AppCompatActivity {

    private EditText status, displayName;
    private Button saveInformationButton;
    private Spinner countrySpinner;
    private boolean isCountrySelected;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String currentUserId, selectedCountry;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        status = findViewById(R.id.setup_status);
        displayName = findViewById(R.id.setup_displayname);
        //countryName = findViewById(R.id.setup_country_name);
        saveInformationButton = findViewById(R.id.setup_information_button);
        countrySpinner = findViewById(R.id.spinner_country_name);

        progressDialog = new ProgressDialog(this);

        saveInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { SaveAccountSetupInformation();
            }
        });

        getCountryList();

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = (String) parent.getItemAtPosition(position);
                Toast.makeText(SetupActivity.this, selectedCountry + " selected", Toast.LENGTH_SHORT).show();
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

    private void SaveAccountSetupInformation() {
        String userStatus = status.getText().toString();
        String displayname = displayName.getText().toString();
        //String countryname = countryName.getText().toString();

        if(TextUtils.isEmpty(displayname)){
            Toast.makeText(SetupActivity.this, "Please enter your display name...", Toast.LENGTH_SHORT).show();
        }else if(!isCountrySelected){
            Toast.makeText(SetupActivity.this, "Please select your country...", Toast.LENGTH_SHORT).show();
        } else{
            progressDialog.setTitle("Saving Information");
            progressDialog.setMessage("Please wait while we are saving your information");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);

            if(userStatus.isEmpty()){
            userStatus = "Hello! I'm using PenPal";
            }

            HashMap userMap = new HashMap();
            userMap.put("uid", currentUserId);
            userMap.put("displayname", displayname);
            userMap.put("country", selectedCountry);
            userMap.put("status", userStatus);
            userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){
                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Your account is created successfully!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                    else {
                        String messageException = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error Occurred: " + messageException, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            });

        }
    }
    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }
}
