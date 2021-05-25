package com.example.penpal.welcome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.penpal.MainActivity;
import com.example.penpal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

    private EditText userEmail, userPassword, userConfirmPassword;
    private Button createAccountButton;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;

    private TextView signInButton;

    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        userEmail = findViewById(R.id.register_email);
        userPassword = findViewById(R.id.register_password);
        userConfirmPassword = findViewById(R.id.register_confirm_password);
        createAccountButton = findViewById(R.id.register_create_account_button);
        signInButton = findViewById(R.id.register_signin);

        loadingBar = new ProgressDialog(this);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = new Intent(RegisterActivity.this, SignInMethodActivity.class);
                startActivity(signInIntent);
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
    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void CreateNewAccount() {
        final String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        String confirmPassword = userConfirmPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(RegisterActivity.this,"Please Write Your Email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this,"Please Write Your Password", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(RegisterActivity.this,"Please Confirm Your Password", Toast.LENGTH_SHORT).show();
        }
        else if(! password.equals(confirmPassword)){
            Toast.makeText(RegisterActivity.this,"Your Password does not match with your confirm password", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please Wait While We Are Creating Your New Account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);


            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        String currentUserId = mAuth.getCurrentUser().getUid();
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        rootRef.child("Users").child(currentUserId).child("device_token")
                                .setValue(deviceToken);

                        Toast.makeText(RegisterActivity.this, "You Are Authenticated Successfully!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        final FirebaseUser user = mAuth.getCurrentUser();
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, "Email Verification Has Been Sent To: " + email,Toast.LENGTH_SHORT ).show();
                                } else {
                                    String messageException = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this,
                                            "Failed to send verification email. Error: " + messageException,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        sendUserToSetupActivity();
                    } else {

                        String message = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                    }
                }
            });

        }

    }

    private void sendUserToSetupActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();

    }
}
