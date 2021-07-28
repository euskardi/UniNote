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

import com.example.uninote.R;
import com.example.uninote.toDo.ToDoDetailActivity;
import com.example.uninote.models.ToDo;
import com.example.uninote.toDo.ToDoAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
    private List<ToDo> allToDos;

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
        queryToDos();
    }

    private void queryToDos() {
        final ParseQuery<ParseUser> innerQuery = ParseQuery.getQuery("_User");
        innerQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("User_ToDo");
        query.whereMatchesQuery("username", innerQuery);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> toDos, ParseException e) {
                for (ParseObject toDo : toDos) {
                    Log.i(TAG, "ToDo is good " + toDo.getParseObject("toDo").getObjectId());
                    final ParseQuery<ToDo> query = ParseQuery.getQuery("ToDo");
                    query.whereEqualTo("objectId", toDo.getParseObject("toDo").getObjectId());

                    query.findInBackground(new FindCallback<ToDo>() {
                        @Override
                        public void done(List<ToDo> objects, ParseException e) {
                            allToDos.addAll(objects);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}