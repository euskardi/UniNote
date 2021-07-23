package com.example.uninote.fragments;

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
import com.example.uninote.ShareAdapter;
import com.example.uninote.ShareContent;
import com.example.uninote.ToDoAdapter;
import com.example.uninote.models.ToDo;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShareFragment extends Fragment {

    public static final String TAG = "ShareFragment";
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvProfiles;
    private LinearLayoutManager mLayoutManager;
    private ShareAdapter adapter;
    private List<ParseUser> allUsers;

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
        queryProfiles();
    }

    private void queryProfiles() {
        final ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        query.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                for (ParseUser parseUser : objects) {
                    Log.i(TAG, "User: " + parseUser.getUsername());
                }
                allUsers.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }
}