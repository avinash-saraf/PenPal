package com.example.penpal.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.penpal.MainActivity;
import com.example.penpal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static int SPLASH_TIME_OUT = 4000; //time for which activity runs(in ms)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();

        //thread to send user to signin method activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //intent to send user to signin method activity once SPLASH_TIME_OUT has passed

                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                if (firebaseUser == null) {
                    Intent onboardingIntent = new Intent(WelcomeActivity.this, SignInMethodActivity.class);
                    startActivity(onboardingIntent);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        //if user already logged in, sent directly to home
        if(currentUser != null){

            SendUserToMainActivity();

        }

    }

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();

    }

}
