package com.example.reportproblem.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.reportproblem.Activity.MainActivity;
import com.example.reportproblem.Activity.ProfileActivity;
import com.example.reportproblem.Model.Problem;
import com.example.reportproblem.Model.Type;
import com.example.reportproblem.Model.User;
import com.example.reportproblem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Locale;


public class createProblem extends Fragment {


    ImageView profilePhotoView;
    EditText descriptionText;
    EditText locationText;
    EditText nameOfThePlaceText;
    EditText severityText;
    RadioGroup radioGroup;
    String creatorUserID;
    RadioButton finalCheckedButton;
    Button addPhoto;
    Button submitProblem;
    Uri imageUri;
    Problem problem;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    FragmentManager manager;
    Type type;
    String problemID;
    String finalUrlForThePhoto;
    BottomNavigationView navigationView;
    DatabaseReference usernameQuery;
    static User creatorOfProblemUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_problem, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        initAuth();
        manager = getFragmentManager();
        problemID = String.valueOf((int) Math.floor(Math.random() * (100000)));
        initNavigationView(view);
        profilePhotoView = view.findViewById(R.id.PhotoOfCreateProblem);
        descriptionText = view.findViewById(R.id.CreateProblemDescription);
        locationText = view.findViewById(R.id.CreateProblemLocation);
        nameOfThePlaceText = view.findViewById(R.id.CreateProblemNameOfThePlace);
        severityText = view.findViewById(R.id.CreateProblemSeverity);
        radioGroup = view.findViewById(R.id.RadioGroupCreateProblem);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                handleType(checkedId);
            }
        });

        addPhoto = view.findViewById(R.id.createProblemPhoto);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhotoUploadDatabaseAndCallPicasso(view);
            }
        });
        submitProblem = view.findViewById(R.id.submitProblem);
        submitProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProblem(view);
            }
        });


    }

    private void initNavigationView(View view) {
        navigationView = view.findViewById(R.id.CreateFragmentNavigationView);
        navigationView.setSelectedItemId(R.id.AddProblemFragmentNav);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ProfileActivityNav:
                        Intent profileActivityIntent = new Intent(getContext(), ProfileActivity.class);
                        startActivity(profileActivityIntent);
                        return true;

                    case R.id.AddProblemFragmentNav:
                        return true;

                    case R.id.MainActivityNav:
                        Intent mainActivityIntent = new Intent(getContext(), MainActivity.class);
                        startActivity(mainActivityIntent);
                        return true;
                }
                return false;
            }
        });
    }

    public void initAuth() {
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            creatorUserID = mUser.getUid();
        }
        usernameQuery = FirebaseDatabase.getInstance().getReference("Users");
        creatorOfProblemUser = new User();
        creatorOfProblemUser= callDatabaseQuery();
        storageReference = FirebaseStorage.getInstance().getReference();
        ref = FirebaseDatabase.getInstance().getReference("Problems");
        firebaseDatabase = FirebaseDatabase.getInstance();


    }

    private void addPhotoUploadDatabaseAndCallPicasso(View view) {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, 10002);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10002 && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            if (imageUri != null) {
                profilePhotoView.setImageURI(imageUri);
                uploadToFirebase(imageUri);
            }
        }
    }

    private void uploadToFirebase(Uri imageUri) {
        StorageReference userStorage = storageReference.child("problem_images").child(problemID + ".jpg");
        userStorage.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        finalUrlForThePhoto = uri.toString();

                    }
                });
            }
        });
    }


    public void createProblem(View view) {
        Toast.makeText(getContext(), "Please Wait", Toast.LENGTH_LONG).show();
        problem = new Problem();
        problem.setDescription(descriptionText.getText().toString());
        if (finalUrlForThePhoto != null) {
            problem.setImg(finalUrlForThePhoto);
        }
        problem.setCreatorUser(creatorUserID);
        Log.i("userinfo",creatorOfProblemUser.toString());
        problem.setCreatorName(creatorOfProblemUser.getEmail());
        problem.setType(type);
        problem.setLocation(locationText.getText().toString());
        problem.setSeverity(Integer.parseInt(severityText.getText().toString()));
        problem.setNameOfThePlace(nameOfThePlaceText.getText().toString());
        problem.setId(problemID);

        firebaseDatabase.getReference("Problems").child(problemID).setValue(problem).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Problem has been created", Toast.LENGTH_LONG).show();
                    removeThisFragment(view);
                } else {
                    Toast.makeText(getContext(), "Problem has NOT been created", Toast.LENGTH_LONG).show();
                    Log.i("ProblemCreateFailure", task.getException().getMessage());
                }
            }
        });
    }



    public User callDatabaseQuery() {
        //QUERY
        usernameQuery.child(creatorUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("USERPROBLEM", snapshot.getValue(User.class).toString());
                creatorOfProblemUser = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occured on User Query", Toast.LENGTH_LONG).show();
            }

        });
    return creatorOfProblemUser;
    }

    private void removeThisFragment(View view) {
        Intent homeIntent = new Intent(getContext(), MainActivity.class);
        startActivity(homeIntent);
        createProblem createproblem = (createProblem) manager.findFragmentById(R.id.AddProblemFragment);
        FragmentTransaction transaction = manager.beginTransaction();
        if (createproblem != null) {
            transaction.remove(createproblem);
            transaction.commit();
        } else {
            Toast.makeText(getContext(), "Fragment Couldn't Closed, Error Occured", Toast.LENGTH_LONG).show();
        }
    }

    public void handleType(int checkedID) {

        switch (checkedID) {
            case R.id.TypeFire:
                type = Type.FIRE;
                break;

            case R.id.typePOLLUTION:
                type = Type.POLLUTION;
                break;

            case R.id.typeNOISY:
                type = Type.NOISY;
                break;

            case R.id.typeSTORM:
                type = Type.STORM;

            case R.id.typeOTHER:
                type = Type.OTHER;

        }
    }
}


