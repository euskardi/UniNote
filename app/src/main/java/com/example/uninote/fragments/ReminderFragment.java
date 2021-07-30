package com.example.uninote.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.uninote.R;
import com.example.uninote.models.ReminderFirebase;
import com.example.uninote.models.UserHasReminder;
import com.example.uninote.reminder.ReminderAdapter;
import com.example.uninote.reminder.ReminderDetailActivity;
import com.example.uninote.models.Reminder;
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

import java.util.ArrayList;
import java.util.List;


public class ReminderFragment extends Fragment {

    public static final String TAG = "RemindersFragment";
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvReminders;
    private LinearLayoutManager mLayoutManager;
    private ImageButton btnAdd;
    private ReminderAdapter adapter;
    private List<ReminderFirebase> allReminders;

    public ReminderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reminder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnAdd = view.findViewById(R.id.btnAdd);
        rvReminders = view.findViewById(R.id.rvReminders);
        allReminders = new ArrayList<>();
        adapter = new ReminderAdapter(getContext(), allReminders, true);
        rvReminders.setAdapter(adapter);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvReminders.setLayoutManager(mLayoutManager);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ReminderDetailActivity.class));
            }
        });

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContent);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                queryReminders();
                swipeContainer.setRefreshing(false);
            }
        });
        queryReminders();
    }

    private void queryReminders() {
        final Query query = FirebaseDatabase.getInstance().getReference("UserHasReminder")
                .orderByChild("user")
                .equalTo(ParseUser.getCurrentUser().getUsername());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    final UserHasReminder userHasReminder = dataSnapshot.getValue(UserHasReminder.class);
                    final Query innerQuery = FirebaseDatabase.getInstance().getReference("Reminders")
                            .orderByKey()
                            .equalTo(userHasReminder.getReminder());

                    innerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                allReminders.add(dataSnapshot.getValue(ReminderFirebase.class));
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