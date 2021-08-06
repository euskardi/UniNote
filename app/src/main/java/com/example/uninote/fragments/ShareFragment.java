package com.example.uninote.fragments;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uninote.R;
import com.example.uninote.ShareAdapter;
import com.example.uninote.models.Project;
import com.example.uninote.models.ProjectFirebase;
import com.example.uninote.models.ReminderFirebase;
import com.example.uninote.models.UserFirebase;
import com.example.uninote.models.UserHasProject;
import com.example.uninote.models.UserHasReminder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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

public class ShareFragment extends Fragment {

    public static final String TAG = "ShareFragment";
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvProfiles;
    private TextView tvLecture;
    private TextView tvWrite;
    private LinearLayoutManager mLayoutManager;
    private ShareAdapter adapter;
    private List<UserFirebase> allUsers;

    public ShareFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvProfiles = view.findViewById(R.id.rvShare);
        tvLecture = view.findViewById(R.id.tvLecture);
        tvWrite = view.findViewById(R.id.tvWrite);
        allUsers = new ArrayList<>();
        adapter = new ShareAdapter(getContext(), allUsers);
        rvProfiles.setAdapter(adapter);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvProfiles.setLayoutManager(mLayoutManager);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContentShare);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                queryProfiles();
                swipeContainer.setRefreshing(false);
            }
        });

        final ProjectFirebase project = getArguments().getParcelable("code");
        tvLecture.setText("Lecture Code: " + project.getId() + "-0");
        tvWrite.setText("Write Code: " + project.getId() + "-1");

        queryProfiles();
    }

    private void queryProfiles() {
        final ProjectFirebase project = getArguments().getParcelable("code");
        final Query query = FirebaseDatabase.getInstance().getReference("UserHasProject")
                .orderByChild("project")
                .equalTo(project.getId());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    final UserHasProject userHasProject = dataSnapshot.getValue(UserHasProject.class);
                    final Query innerQuery = FirebaseDatabase.getInstance().getReference("Users")
                            .orderByKey()
                            .equalTo(userHasProject.getUser());
                    innerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                allUsers.add(dataSnapshot.getValue(UserFirebase.class));
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