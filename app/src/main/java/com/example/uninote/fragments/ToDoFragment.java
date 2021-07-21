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
import com.example.uninote.ToDoDetailActivity;
import com.example.uninote.models.Reminder;
import com.example.uninote.models.ToDo;
import com.example.uninote.ToDoAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
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
        adapter = new ToDoAdapter(getContext(), allToDos);
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
        final ParseQuery<ToDo> query = ParseQuery.getQuery("ToDo");
        query.whereMatchesQuery("Username", innerQuery);

        query.findInBackground(new FindCallback<ToDo>() {
            @Override
            public void done(List<ToDo> toDos, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                }
                for (ToDo toDo : toDos) {
                    Log.i(TAG, "ToDo is good " + toDo.getTitle());
                }
                allToDos.addAll(toDos);
                adapter.notifyDataSetChanged();
            }
        });
    }
}