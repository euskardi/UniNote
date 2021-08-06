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
import com.example.uninote.models.ReminderFirebase;
import com.example.uninote.reminder.EditReminder;
import com.example.uninote.reminder.ReminderAdapter;
import com.example.uninote.models.Project;
import com.example.uninote.models.Reminder;
import com.example.uninote.reminder.ReminderDetailProject;
import com.example.uninote.toDo.ToDoDetailProject;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ReminderProjectFragment extends ReminderFragment {

    public static final String TAG = "RemindersFragment";
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvReminders;
    private LinearLayoutManager mLayoutManager;
    private ImageButton btnAdd;
    private ReminderAdapter adapter;
    private List<ReminderFirebase> allReminders;
    final private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final DatabaseReference rootDatabase = FirebaseDatabase.getInstance().getReference();

    public ReminderProjectFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reminder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final boolean userType = viewType();

        btnAdd = view.findViewById(R.id.btnAdd);
        if (!userType) {
            btnAdd.setVisibility(View.GONE);
        }

        rvReminders = view.findViewById(R.id.rvReminders);
        allReminders = new ArrayList<>();
        adapter = new ReminderAdapter(getContext(), allReminders, userType, getArguments().getParcelable("code"));
        rvReminders.setAdapter(adapter);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvReminders.setLayoutManager(mLayoutManager);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContent);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                queryReminders();
                swipeContainer.setRefreshing(false);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProjectFirebase project = getArguments().getParcelable("code");
                final Intent intent = new Intent(getContext(), ReminderDetailProject.class);
                intent.putExtra(ProjectFirebase.class.getSimpleName(), project);
                getContext().startActivity(intent);

            }
        });

        queryReminders();
    }

    private boolean viewType() {
        final ProjectFirebase project = getArguments().getParcelable("code");
        if (project.getEditor().equals(firebaseAuth.getUid())) return true;
        return false;
    }

    private void queryReminders() {
        final ProjectFirebase project = getArguments().getParcelable("code");
        final Query query = FirebaseDatabase.getInstance().getReference("Reminders")
                .orderByChild("project")
                .equalTo(project.getName());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    allReminders.add(dataSnapshot.getValue(ReminderFirebase.class));
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