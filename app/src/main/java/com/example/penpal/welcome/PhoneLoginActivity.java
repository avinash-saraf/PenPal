package com.example.penpal.welcome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.penpal.MainActivity;
import com.example.penpal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private Button sendVerificationCodeBttn, verifyButton;
    private EditText inputPhoneNumber, inputVerificationCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;

    private TextView phoneHeader, signIn, createAccount;

    private ProgressDialog loadingBar;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        sendVerificationCodeBttn = findViewById(R.id.send_verification_code_button);
        verifyButton = findViewById(R.id.verify_button);

        inputPhoneNumber = findViewById(R.id.input_phone_number);
        inputVerificationCode = findViewById(R.id.input_verification_code);

        loadingBar = new ProgressDialog(this);

        String type = getIntent().getExtras().get("type").toString();

        signIn = findViewById(R.id.phone_login_signin);
        createAccount = findViewById(R.id.phone_login_create_account);

        if(type.equals("sign_in")){
        verifyButton.setText("Verify And Sign In");
        phoneHeader = findViewById(R.id.phone_header);
        phoneHeader.setText("Sign In");
        signIn.setVisibility(View.GONE);
        createAccount.setVisibility(View.VISIBLE);
        }

        sendVerificationCodeBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber = inputPhoneNumber.getText().toString();
                if(TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(PhoneLoginActivity.this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("Phone verification");
                    loadingBar.setMessage("Please wait while we are authenticating your phone...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks
                }
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCodeBttn.setVisibility(View.INVISIBLE);
                inputPhoneNumber.setVisibility(View.INVISIBLE);

                String verificationCode = inputVerificationCode.getText().toString();
                if(TextUtils.isEmpty(verificationCode)){
                    Toast.makeText(PhoneLoginActivity.this, "Please enter verification code...", Toast.LENGTH_SHORT).show();
                } else{

                    loadingBar.setTitle("Verification code");
                    loadingBar.setMessage("Please wait while we are verifying your verification code...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingBar.dismiss();

                Toast.makeText(PhoneLoginActivity.this, "Invalid Phone Number, Please enter your correct phone number with your country code...", Toast.LENGTH_SHORT).show();

                sendVerificationCodeBttn.setVisibility(View.VISIBLE);
                inputPhoneNumber.setVisibility(View.VISIBLE);

                verifyButton.setVisibility(View.INVISIBLE);
                inputVerificationCode.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                loadingBar.dismiss();
                // ...
                Toast.makeText(PhoneLoginActivity.this, "Verification code sent successfully...", Toast.LENGTH_SHORT).show();

                sendVerificationCodeBttn.setVisibility(View.INVISIBLE);
                inputPhoneNumber.setVisibility(View.INVISIBLE);

                verifyButton.setVisibility(View.VISIBLE);
                inputVerificationCode.setVisibility(View.VISIBLE);

            }
        };

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = new Intent(PhoneLoginActivity.this, SignInMethodActivity.class);
                startActivity(signInIntent);
            }
        });
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(PhoneLoginActivity.this, CreateAccountMethodActivity.class);
                startActivity(registerIntent);
            }
        });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Congratulations, you are signed in successfully!", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();

                        } else {
                            String msg = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error: " + msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    /*
     private void saveToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String deviceToken = task.getResult();

                        String currentUserId = mAuth.getCurrentUser().getUid();

                        //saves device token in firebase database
                        userRef.child(currentUserId).child("device_token")
                                .setValue(deviceToken)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(PhoneLoginActivity.this, "You are logged in Successfully!", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                            SendUserToMainActivity();
                                        }
                                    }
                                });

                    }
                });
    }
     */

}
