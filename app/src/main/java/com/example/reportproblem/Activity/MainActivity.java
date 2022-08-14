package com.example.reportproblem.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.widget.Toast;

import com.example.reportproblem.Fragment.ListProblem;
import com.example.reportproblem.Fragment.createProblem;
import com.example.reportproblem.Model.Problem;
import com.example.reportproblem.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }


    public void init() {
        initNavigationBar();
        initAuth();
        fillProblemList();
    }

    private void fillProblemList() {
      if(firebaseAuth.getCurrentUser() != null) {
          ListProblem listProblem = new ListProblem();
          FragmentManager manager = getSupportFragmentManager();
          FragmentTransaction transaction = manager.beginTransaction();
          transaction.add(R.id.activityMain, listProblem, "listProblemFragment");
          transaction.commit();
      }
    }

    private void initAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            Intent LoginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(LoginIntent);
            Toast.makeText(getApplicationContext(), "Please Sign In", Toast.LENGTH_LONG).show();
        }

    }

    private void initNavigationBar() {
        bottomNavigationView = findViewById(R.id.NavigationView);
        bottomNavigationView.setSelectedItemId(R.id.MainActivityNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ProfileActivityNav:
                        Intent profileIntent= new Intent(MainActivity.this,ProfileActivity.class);
                        startActivity(profileIntent);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.AddProblemFragmentNav:
                        Intent goIntent=new Intent(MainActivity.this,ProfileActivity.class);
                        startActivity(goIntent);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.MainActivityNav:
                        return true;
                }
           return false; }
        });
    }


}