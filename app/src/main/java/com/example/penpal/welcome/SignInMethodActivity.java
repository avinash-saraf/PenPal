package com.example.penpal.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.penpal.R;

public class SignInMethodActivity extends AppCompatActivity implements View.OnClickListener {

    Button emailLoginBtn, gmailLoginBtn, phoneNumberLoginBtn;
    TextView createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_method);

        InitializeControllers();
        emailLoginBtn.setOnClickListener(this);
        gmailLoginBtn.setOnClickListener(this);
        phoneNumberLoginBtn.setOnClickListener(this);
        createAccount.setOnClickListener(this);
    }

    private void InitializeControllers() {

        emailLoginBtn = findViewById(R.id.signin_method_email);
        gmailLoginBtn = findViewById(R.id.signin_method_gmail);
        phoneNumberLoginBtn = findViewById(R.id.signin_method_phone);

        createAccount = findViewById(R.id.signin_method_create_account);
    }

    //after choosing desired method of sign-in, sends user to sign-in activity
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.signin_method_email:
                Intent emailLoginIntent = new Intent(SignInMethodActivity.this, LoginActivity.class);

                // instead of making separate activities to sign-in with email/gmail
                // sent info to LoginActivity to change the page accordingly (as code for each are very similar)
                // here user signing in using email and accordingly info sent to activity via intent

                emailLoginIntent.putExtra("type", "email");
                startActivity(emailLoginIntent);

                break;

            case R.id.signin_method_gmail:
                Intent gmailLoginIntent = new Intent(SignInMethodActivity.this, LoginActivity.class);

                // same as above except user signing in with gmail this time

                gmailLoginIntent.putExtra("type", "gmail");
                startActivity(gmailLoginIntent);

                break;
            case R.id.signin_method_phone:
                Intent phoneSignInIntent = new Intent(SignInMethodActivity.this, PhoneLoginActivity.class);

                // instead of making two separate activities to sign-in/create account through phone number
                // sent information to the PhoneLoginActivity to change the page according to whether the user is signin-in/create account
                // since both processes have the same code
                // here user signing in using phone number and accordingly information sent to activity through intent

                phoneSignInIntent.putExtra("type", "sign_in");
                startActivity(phoneSignInIntent);
                break;
            case R.id.signin_method_create_account:
                Intent registerIntent = new Intent(SignInMethodActivity.this, CreateAccountMethodActivity.class);

                //if the user has not created a account
                startActivity(registerIntent);
                break;
        }
    }


}
