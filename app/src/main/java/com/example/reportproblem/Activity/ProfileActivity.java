package com.example.reportproblem.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.canhub.cropper.CropImage;
import com.example.reportproblem.Fragment.ListProblem;
import com.example.reportproblem.Fragment.ProfileProblem;
import com.example.reportproblem.Fragment.createProblem;
import com.example.reportproblem.Model.Problem;
import com.example.reportproblem.Model.User;
import com.example.reportproblem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView profilePhoto;
    TextView emailText;
    TextView firstnameText;
    TextView lastnameText;
    Button logOutButton;
    FirebaseUser user;
    FirebaseAuth mAuth;
    DatabaseReference ref,ref2;
    String userID;
    Uri imageUri;
    StorageReference storageReference;
    BottomNavigationView bottomNavigationView;
    Button deleteButton;
    EditText deleteButtonInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
    }

    public void init() {

        initNavigationBar();
        initXml();
        initAuth();
        getUserDataAndAssignToTheTextViews();
        fillProblemList();
    }

    public void signOut() {
        mAuth.signOut();
        Intent loginIntent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    public void getUserDataAndAssignToTheTextViews() {
        ref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User profileUser = snapshot.getValue(User.class);

                if (profileUser != null) {
                    emailText.setText(profileUser.getEmail());
                    firstnameText.setText(profileUser.getFirstname());
                    lastnameText.setText(profileUser.getLastname());
                    callPicasso(profileUser.getImg());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("ProfileAssignError", "Something is wrong " + error.getMessage());
            }
        });
    }

    public void changePhoto() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, 10001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001 && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            if (imageUri != null) {
                profilePhoto.setImageURI(imageUri);
                uploadToFirebase(imageUri);
            }
        }
    }

    private void uploadToFirebase(Uri imageUri) {
        StorageReference userStorage = storageReference.child("profile_images").child(userID + ".jpg");
        userStorage.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        Map userUpdateMap = new HashMap();
                        userUpdateMap.put("img", url);
                        ref.child(userID).updateChildren(userUpdateMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "New Photo has been added and saved!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "An Error Occured, Check Logs", Toast.LENGTH_LONG).show();
                                    Log.i("PhotoUpdateError", task.getException().getMessage());
                                }
                            }
                        });

                    }
                });
            }
        });
    }

    public void callPicasso(String url) {
        Picasso.get().load(url).into(profilePhoto);
    }

    private void initNavigationBar() {
        bottomNavigationView = findViewById(R.id.ProfileNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.ProfileActivityNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ProfileActivityNav:
                        return true;

                    case R.id.AddProblemFragmentNav:
                        initCreateProblemFragment();
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.MainActivityNav:
                        Intent mainActivityIntent=new Intent(ProfileActivity.this,MainActivity.class);
                        startActivity(mainActivityIntent);
                        overridePendingTransition(0, 0);
                        return true;

                }
                return false;
            }
        });
    }

    public void initXml() {
        profilePhoto = findViewById(R.id.ProfileImageView);
        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePhoto();
            }
        });

        emailText = findViewById(R.id.emailProfile);
        firstnameText = findViewById(R.id.FirstNameProfile);
        lastnameText = findViewById(R.id.LastNameProfile);
        
        deleteButton=findViewById(R.id.deletebutton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProblem();
            }
        });

        logOutButton = findViewById(R.id.LogOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void deleteProblem() {
        ref2= FirebaseDatabase.getInstance().getReference("Problems");
        ref2.orderByChild("creatorUser").equalTo(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.i("DeleteProblemQuery", snapshot.getValue(Problem.class).toString());
                            dataSnapshot.getRef().removeValue();
                            }
                            Toast.makeText(getApplicationContext(), "Problems Successfully deleted", Toast.LENGTH_LONG).show();
                            Intent ProfileIntent = new Intent(ProfileActivity.this, MainActivity.class);
                            startActivity(ProfileIntent);
                        }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error Deleting from Firebase", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void initCreateProblemFragment() {
        createProblem createProblemFragment = new createProblem();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction().setReorderingAllowed(true);
        transaction.add(R.id.profileActivity, createProblemFragment,"createProblemFragment");
        transaction.commit();
    }

    public void initAuth() {
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();
        if (user != null) {
            userID = user.getUid();
        }
        if (user == null) {
            signOut();
        }
    }
    private void fillProblemList() {
            ProfileProblem listProblem = new ProfileProblem();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction().setReorderingAllowed(true);
            transaction.add(R.id.FragmentProfileProblemList, listProblem, "listProfileProblemFragment");
            transaction.commit();
        }

}