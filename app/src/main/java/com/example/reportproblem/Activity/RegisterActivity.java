package com.example.reportproblem.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reportproblem.Model.User;
import com.example.reportproblem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;

public class RegisterActivity extends AppCompatActivity {

    ImageView img;
    EditText emailText;
    EditText passwordText;
    EditText firstnameText;
    EditText lastnameText;
    Button submitRegister;
    TextView intentToLoginPage;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    public void init() {
        initAuth();
        initXml();

    }

    public void initAuth() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void initXml() {
        img = findViewById(R.id.RegisterImageView);
        emailText = findViewById(R.id.RegisterEditTextTextEmailAddress);
        passwordText = findViewById(R.id.RegisterEditTextTextPassword);
        firstnameText = findViewById(R.id.RegisterEditTextFirstName);
        lastnameText = findViewById(R.id.RegisterEditTextLastName);
        progressBar = findViewById(R.id.progressBar);
        submitRegister = findViewById(R.id.RegisterButton);


        intentToLoginPage = findViewById(R.id.RegisterToLoginIntentText);
        intentToLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                Toast.makeText(getApplicationContext(), "You can Login Now", Toast.LENGTH_SHORT).show();
            }
        });


        submitRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(passwordText.getText().toString()).isEmpty() &&
                        (Patterns.EMAIL_ADDRESS.matcher(emailText.getText().toString()).matches())) {

                    progressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(RegisterActivity.this, "Please Wait", Toast.LENGTH_LONG).show();
                    firebaseAuth.createUserWithEmailAndPassword(
                                    emailText.getText().toString(),
                                    passwordText.getText().toString()).

                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                      @Override
                                                      public void onComplete(@NonNull Task<AuthResult> task) {
                                                          if (task.isSuccessful()) {
                                                              User user = new User(
                                                                      emailText.getText().toString(),
                                                                      firstnameText.getText().toString(),
                                                                      lastnameText.getText().toString(),
                                                                      "default"
                                                              );
                                                              if (!task.isSuccessful()) {
                                                                  Toast.makeText(RegisterActivity.this, "This User May already be Registered!", Toast.LENGTH_LONG).show();
                                                              }
                                                              firebaseDatabase.getReference("Users")
                                                                      .child(firebaseAuth.getCurrentUser().getUid())
                                                                      .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                    if (task.isSuccessful()) {
                                                                                                                        Toast.makeText(RegisterActivity.this, "User has been Registered, Now sign in!", Toast.LENGTH_LONG).show();
                                                                                                                        progressBar.setVisibility(View.GONE);
                                                                                                                        Log.i("RegisterSuccess", task.getResult().toString());
                                                                                                                        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                                                                                        startActivity(loginIntent);
                                                                                                                    } else {
                                                                                                                        Toast.makeText(RegisterActivity.this, "There is a problem.", Toast.LENGTH_LONG).show();
                                                                                                                        Log.i("RegisterActivityFailure", task.getException().getMessage());
                                                                                                                        progressBar.setVisibility(View.GONE);
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                      );

                                                          }
                                                      }
                                                  }
                            );
                }
            }
        });
    }

}