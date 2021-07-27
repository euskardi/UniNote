package com.example.uninote.fragments;

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

import com.example.uninote.R;
import com.example.uninote.ReminderAdapter;
import com.example.uninote.ToDoAdapter;
import com.example.uninote.models.Project;
import com.example.uninote.models.Reminder;
import com.example.uninote.models.ToDo;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
    private List<ToDo> allToDos;

    public ToDoProjectFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_to_do, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final boolean typeOfLecture = viewType();
        btnAdd = view.findViewById(R.id.btnAdd);
        rvToDos = view.findViewById(R.id.rvToDos);
        allToDos = new ArrayList<>();
        adapter = new ToDoAdapter(getContext(), allToDos, viewType());
        rvToDos.setAdapter(adapter);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvToDos.setLayoutManager(mLayoutManager);

        if (!typeOfLecture) {
            btnAdd.setVisibility(View.GONE);
        }

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContent);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                queryToDos();
                swipeContainer.setRefreshing(false);
            }
        });
        queryToDos();
    }

    private boolean viewType() {
        final Project project = getArguments().getParcelable("code");
        final boolean[] type = new boolean[1];

        final ParseQuery<ParseUser> innerQueryOne = ParseQuery.getQuery("_User");
        innerQueryOne.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        final ParseQuery<ParseUser> innerQueryTwo = ParseQuery.getQuery("Project");
        innerQueryTwo.whereEqualTo("objectId", project.getObjectId());

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("User_Project");
        query.whereMatchesQuery("username", innerQueryOne);
        query.whereMatchesQuery("project", innerQueryTwo);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> reminders, ParseException e) {
                type[0] = reminders.get(0).getBoolean("type");
            }
        });
        return type[0];
    }

    private void queryToDos() {
        final Project project = getArguments().getParcelable("code");
        final ParseQuery<ParseUser> innerQuery = ParseQuery.getQuery("Project");
        innerQuery.whereEqualTo("objectId", project.getObjectId());
        final ParseQuery<ToDo> query = ParseQuery.getQuery("ToDo");
        query.whereMatchesQuery("Project", innerQuery);

        query.findInBackground(new FindCallback<ToDo>() {
            @Override
            public void done(List<ToDo> toDos, ParseException e) {
                allToDos.addAll(toDos);
                adapter.notifyDataSetChanged();
            }
        });
    }
}