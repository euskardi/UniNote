package com.example.uninote.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.uninote.ProjectAdapter;
import com.example.uninote.ProjectDetailActivity;
import com.example.uninote.R;
import com.example.uninote.models.ProjectFirebase;
import com.example.uninote.models.UserHasProject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class ProjectFragment extends Fragment {

    public static final String TAG = "ProjectsFragment";
    final private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvProjects;
    private LinearLayoutManager mLayoutManager;
    private ImageButton btnAdd;
    private ProjectAdapter adapter;
    private List<ProjectFirebase> allProjects;

    public ProjectFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnAdd = view.findViewById(R.id.btnAdd);
        rvProjects = view.findViewById(R.id.rvProjects);
        allProjects = new ArrayList<>();
        adapter = new ProjectAdapter(getContext(), allProjects);
        rvProjects.setAdapter(adapter);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvProjects.setLayoutManager(mLayoutManager);

        swipeContainer = view.findViewById(R.id.swipeContent);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                queryProjects();
                swipeContainer.setRefreshing(false);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ProjectDetailActivity.class));
            }
        });

        queryProjects();
    }

    private void queryProjects() {

        final Query query = FirebaseDatabase.getInstance().getReference("UserHasProject")
                .orderByChild("user")
                .equalTo(firebaseAuth.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    final UserHasProject userHasProject = dataSnapshot.getValue(UserHasProject.class);
                    final Query innerQuery = FirebaseDatabase.getInstance().getReference("Project")
                            .orderByKey()
                            .equalTo(userHasProject.getProject());

                    innerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                allProjects.add(dataSnapshot.getValue(ProjectFirebase.class));
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Error In Connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error In Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}