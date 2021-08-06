package com.example.uninote.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.uninote.R;
import com.example.uninote.models.ProjectFirebase;
import com.example.uninote.models.ToDoFirebase;
import com.example.uninote.toDo.ToDoAdapter;
import com.example.uninote.toDo.ToDoDetailProject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class ToDoProjectFragment extends ToDoFragment {

    public static final String TAG = "ToDoFragment";
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvToDos;
    private LinearLayoutManager mLayoutManager;
    private ImageButton btnAdd;
    private ToDoAdapter adapter;
    private List<ToDoFirebase> allToDos;

    final private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final DatabaseReference rootDatabase = FirebaseDatabase.getInstance().getReference();

    public ToDoProjectFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_to_do, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final boolean userType = viewType();

        btnAdd = view.findViewById(R.id.btnAdd);
        if (!userType) {
            btnAdd.setVisibility(View.GONE);
        }

        rvToDos = view.findViewById(R.id.rvToDos);
        allToDos = new ArrayList<>();
        adapter = new ToDoAdapter(getContext(), allToDos, userType, getArguments().getParcelable("code"));
        rvToDos.setAdapter(adapter);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvToDos.setLayoutManager(mLayoutManager);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContent);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                queryToDos();
                swipeContainer.setRefreshing(false);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProjectFirebase project = getArguments().getParcelable("code");
                final Intent intent = new Intent(getContext(), ToDoDetailProject.class);
                intent.putExtra(ProjectFirebase.class.getSimpleName(), project);
                getContext().startActivity(intent);
            }
        });

        queryToDos();
    }

    private boolean viewType() {
        final ProjectFirebase project = getArguments().getParcelable("code");
        if (project.getEditor().equals(firebaseAuth.getUid())) return true;
        return false;
    }

    private void queryToDos() {
        final ProjectFirebase project = getArguments().getParcelable("code");
        final Query query = FirebaseDatabase.getInstance().getReference("ToDos")
                .orderByChild("project")
                .equalTo(project.getName());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    allToDos.add(dataSnapshot.getValue(ToDoFirebase.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Internet Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}