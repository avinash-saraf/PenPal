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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.penpal.MainActivity;
import com.example.penpal.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private ImageView googleImageSigninButton;
    private EditText userEmail, userPassword;
    private TextView createAccount, loginHeader;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    private Button googleSigninButton;

    private static final int RC_SIGN_IN = 1;

    private GoogleApiClient mGoogleSignInClient;
    private static final String TAG = "LoginActivity";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        userEmail = findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button_email);
        googleSigninButton = findViewById(R.id.login_button_google);
        progressDialog = new ProgressDialog(this);
        createAccount = findViewById(R.id.login_email_create_account);

        //buttons for login set to default as GONE
        loginButton.setVisibility(View.GONE);
        googleSigninButton.setVisibility(View.GONE);

        //receives info from intent whether user using email or gmail to sign-in
        String type = getIntent().getExtras().get("type").toString();

        if(type.equals("gmail")){

            //makes googleSigninButton visible so user can login using gmail
            googleSigninButton.setVisibility(View.VISIBLE);

        } else if(type.equals("email")){

            //makes loginButton visible so user can login using email
            loginButton.setVisibility(View.VISIBLE);

        }

        // TODO: check if user provided with create account options when sent to register activity
        // if user wishes to create new account, sent to register activity
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });

        //configuring gmail sign-in
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(LoginActivity.this, "Connection to Google Sign In Failed...", Toast.LENGTH_SHORT).show();

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private void signIn(){

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            //configuring progress dialog
            progressDialog.setTitle("Google Sign-In");
            progressDialog.setMessage("Please wait, we logging you in with your Google account");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){

                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(LoginActivity.this, "Please wait while we are getting your auth result...", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(LoginActivity.this, "Can't get Auth Result", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }

        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithCredential:success");
                            SendUserToMainActivity();
                            progressDialog.dismiss();

                        } else {


                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            String messageException = task.getException().toString();
                            SendUserToLoginActivity();
                            Toast.makeText(LoginActivity.this, "Not Authenticated : " + messageException, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }

                        // ...
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            SendUserToMainActivity();
        }
    }

    // login via email
    private void AllowUserToLogin() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this, "Please enter your email...", Toast.LENGTH_SHORT).show();
        }else  if(TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Please enter your password...", Toast.LENGTH_SHORT).show();
        }
        else {
            //configuring progress dialog
            progressDialog.setTitle("Logging In");
            progressDialog.setMessage("Please wait while we are logging you in...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true); //prevents progress dialog from closing when user clicks on the screen

            //sign in with email
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        String currentUserId = mAuth.getCurrentUser().getUid();
                        String deviceToken = FirebaseInstanceId.getInstance().getToken(); //gets device token (useful to cloud notifications)

                        //saves device token in firebase database
                        usersRef.child(currentUserId).child("device_token")
                                .setValue(deviceToken)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(LoginActivity.this, "You are logged in Successfully!", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            SendUserToMainActivity();
                                        }
                                    }
                                });


                    }
                    else {
                        String messageException = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Error Occurred: " + messageException, Toast.LENGTH_SHORT);
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }
    private void SendUserToLoginActivity() {

        Intent mainIntent = new Intent(LoginActivity.this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);

    }
}
