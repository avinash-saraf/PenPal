package com.example.penpal.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.penpal.R;

public class CreateAccountMethodActivity extends AppCompatActivity implements View.OnClickListener {

    Button emailRegisterBtn, gmailRegisterBtn, phoneNumberRegisterBtn;
    TextView signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_method);

        InitializeControllers();

        emailRegisterBtn.setOnClickListener(this);
        gmailRegisterBtn.setOnClickListener(this);
        phoneNumberRegisterBtn.setOnClickListener(this);
        signIn.setOnClickListener(this);
    }

    private void InitializeControllers() {
        emailRegisterBtn = findViewById(R.id.create_account_method_email);
        gmailRegisterBtn = findViewById(R.id.create_account_method_gmail);
        phoneNumberRegisterBtn = findViewById(R.id.create_account_method_phone);

        signIn = findViewById(R.id.create_account_method_signin);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.create_account_method_email:
                Intent emailRegisterIntent = new Intent(CreateAccountMethodActivity.this, RegisterActivity.class);
                startActivity(emailRegisterIntent);
                break;
            case R.id.create_account_method_gmail:
                Intent gmailLoginIntent = new Intent(CreateAccountMethodActivity.this, LoginActivity.class);
                gmailLoginIntent.putExtra("type", "gmail");
                startActivity(gmailLoginIntent);
                break;
            case R.id.create_account_method_phone:
                Intent phoneRegisterIntent = new Intent(CreateAccountMethodActivity.this, PhoneLoginActivity.class);
                phoneRegisterIntent.putExtra("type", "create_account");
                startActivity(phoneRegisterIntent);
                break;
            case R.id.create_account_method_signin:
                Intent signInIntent = new Intent(CreateAccountMethodActivity.this, SignInMethodActivity.class);
                startActivity(signInIntent);
                break;
        }
    }
}
