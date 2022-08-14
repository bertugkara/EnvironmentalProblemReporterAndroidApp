package com.example.reportproblem.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reportproblem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ImageView img;
    EditText emailText;
    EditText passwordText;
    Button loginButton;
    TextView loginToRegisterIntentText;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    public void init() {
        initAuth();
        initXml();
    }

    public void initAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void initXml() {
        img = findViewById(R.id.LoginImageView);
        emailText = findViewById(R.id.LoginEditTextTextEmailAddress);
        passwordText = findViewById(R.id.LoginEditTextTextPassword);
        loginButton = findViewById(R.id.LoginButton);
        progressBar = findViewById(R.id.LoginProgressBar);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!emailText.getText().toString().isEmpty() && !passwordText.getText().toString().isEmpty()) {

                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent profileIntent = new Intent(LoginActivity.this, ProfileActivity.class);
                                        startActivity(profileIntent);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Failed To Login!", Toast.LENGTH_LONG).show();
                                        Log.i("LoginFailed", task.toString());
                                    }
                                }
                            });
                }
            }
        });

        loginToRegisterIntentText = findViewById(R.id.LoginToRegisterIntentText);
        loginToRegisterIntentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                Toast.makeText(getApplicationContext(), "You can Register Now", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
