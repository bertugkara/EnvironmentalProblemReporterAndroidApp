package com.example.reportproblem.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.reportproblem.Adapter.ProblemAdapter;
import com.example.reportproblem.Model.Problem;
import com.example.reportproblem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileProblem extends Fragment {

    List<Problem> problemList;
    ProblemAdapter problemAdapter;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseUser mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile_problem, container, false);
        init(view);
        return view;
    }
    private void init(View view) {
        initAuthAndDatabase(view);
        getTheProblemsFromDatabase(view);
        initRecyclerView(view);
    }

    private void getTheProblemsFromDatabase(View view) {
        problemList=new ArrayList<>();
        problemAdapter=new ProblemAdapter(getContext(),problemList,true);
    }

    private void initAuthAndDatabase(View view) {
        mUser=FirebaseAuth.getInstance().getCurrentUser();
        String profileID=mUser.getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference("Problems");
        databaseReference.orderByChild("creatorUser").equalTo(profileID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren() ){
                    Problem problem=dataSnapshot.getValue(Problem.class);
                    problemList.add(problem);
                }
                problemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Error Fetching Problems",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initRecyclerView(View view) {
        recyclerView=view.findViewById(R.id.profileRECYCLERINFLATE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(problemAdapter);

    }
}