package com.example.reportproblem.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reportproblem.Activity.MainActivity;
import com.example.reportproblem.Activity.ProfileActivity;
import com.example.reportproblem.Model.User;
import com.example.reportproblem.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;

import com.example.reportproblem.Model.Problem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProblemAdapter extends RecyclerView.Adapter<ProblemAdapter.ProblemViewHolder> {

    Context context;
    List<Problem> problemList;
    DatabaseReference ref;
    FirebaseUser mUser;
    boolean isProfileAdapter;
    static String problemID;

    public ProblemAdapter(Context context, List<Problem> problemList, boolean isProfileAdapter) {
        this.context = context;
        this.problemList = problemList;
        this.isProfileAdapter=isProfileAdapter;
    }

    @NonNull
    @Override
    public ProblemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.problem, parent, false);
        return new ProblemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ProblemViewHolder holder, int position) {
        Problem problem;
        problem= problemList.get(position);

        holder.typeOfProblemText.setText("Type:" + problem.getType().toString());
        holder.SeverityText.setText("Severity: " + String.valueOf(problem.getSeverity()));
        holder.problemIDText.setText("Problem ID: " + problem.getId());
        holder.descriptionText.setText("Description: " + problem.getDescription());
        holder.createdAt.setText(problem.getCreatedAt().toString());
        holder.creatorNameText.setText("Creator Name: " + problem.getCreatorName());

        String url = problem.getImg();
        Picasso.get().load(url).into(holder.problemPhoto);
        holder.nameOfthePlaceText.setText("Place: " + problem.getNameOfThePlace());
        holder.locationText.setText("Location: " + problem.getLocation());
        problemID=problem.getId();
        Log.i("isProfileAdapter",String.valueOf(isProfileAdapter));
        if(isProfileAdapter){
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteProblem(problemID);
                }
            });
        }
        else{
            holder.deleteButton.setVisibility(View.GONE);
        }

    }

    private void deleteProblem(String id){
        Log.i("DeleteProblem",id);
        ref = FirebaseDatabase.getInstance().getReference("Problems");
        ref.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.i("DeleteProblemQuery", snapshot.getValue(Problem.class).toString());
                    dataSnapshot.getRef().removeValue();
                }
                Toast.makeText(context.getApplicationContext(), "Problems Successfully deleted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error Deleting from Firebase", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return problemList.size();
    }


    public static class ProblemViewHolder extends RecyclerView.ViewHolder {

        ImageView problemPhoto;
        TextView nameOfthePlaceText;
        TextView locationText;
        TextView creatorNameText;
        TextView SeverityText;
        TextView problemIDText;
        TextView descriptionText;
        TextView typeOfProblemText;
        TextView createdAt;
        Button deleteButton;

        public ProblemViewHolder(@NonNull View itemView) {
            super(itemView);
            initXML(itemView);
        }

        private void initXML(View itemView) {
            problemPhoto = itemView.findViewById(R.id.ProblemImageView);
            nameOfthePlaceText = itemView.findViewById(R.id.nameOfPlace);
            locationText = itemView.findViewById(R.id.problemLocationText);
            creatorNameText = itemView.findViewById(R.id.ProblemcreatorUser);
            SeverityText = itemView.findViewById(R.id.ProblemSeverityText);
            problemIDText = itemView.findViewById(R.id.ProblemID);
            descriptionText = itemView.findViewById(R.id.ProblemDescription);
            typeOfProblemText = itemView.findViewById(R.id.typeOfProblem);
            createdAt = itemView.findViewById(R.id.createdAtProblem);
            deleteButton=itemView.findViewById(R.id.deleteProblem);
        }
    }
}
