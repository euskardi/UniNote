package com.example.uninote.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.uninote.R;
import com.example.uninote.models.ToDoFirebase;
import com.example.uninote.models.UserHasToDo;
import com.example.uninote.toDo.ToDoDetailActivity;
import com.example.uninote.toDo.ToDoAdapter;
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

public class ToDoFragment extends Fragment {

    public static final String TAG = "ToDosFragment";
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvToDos;
    private LinearLayoutManager mLayoutManager;
    private ImageButton btnAdd;
    private ToDoAdapter adapter;
    private List<ToDoFirebase> allToDos;

    private DatabaseReference reference;
    final private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public ToDoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_to_do, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnAdd = view.findViewById(R.id.btnAdd);
        rvToDos = view.findViewById(R.id.rvToDos);
        allToDos = new ArrayList<>();
        adapter = new ToDoAdapter(getContext(), allToDos, true);
        rvToDos.setAdapter(adapter);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvToDos.setLayoutManager(mLayoutManager);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ToDoDetailActivity.class));
            }
        });

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContent);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                queryToDos();
                swipeContainer.setRefreshing(false);
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("ToDos");

        queryToDos();
    }

    private void queryToDos() {
        final Query query = FirebaseDatabase.getInstance().getReference("UserHasToDo")
                .orderByChild("user")
                .equalTo(firebaseAuth.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    final UserHasToDo userHasToDo = dataSnapshot.getValue(UserHasToDo.class);
                    final Query innerQuery = FirebaseDatabase.getInstance().getReference("ToDos")
                            .orderByChild("id")
                            .equalTo(userHasToDo.getToDo());

                    innerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                allToDos.add(dataSnapshot.getValue(ToDoFirebase.class));
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