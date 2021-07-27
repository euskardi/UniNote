package com.example.uninote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.uninote.fragments.ProfileFragment;
import com.example.uninote.fragments.ProjectFragment;
import com.example.uninote.fragments.ReminderFragment;
import com.example.uninote.fragments.ReminderProjectFragment;
import com.example.uninote.fragments.ShareFragment;
import com.example.uninote.fragments.ToDoFragment;
import com.example.uninote.fragments.ToDoProjectFragment;
import com.example.uninote.models.Project;
import com.example.uninote.models.ToDo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class ProjectActivity extends AppCompatActivity {

    public final static String TAG = "Project";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    private Project project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        project = Parcels.unwrap(getIntent().getParcelableExtra(Project.class.getSimpleName()));
        final Bundle args = new Bundle();
        args.putParcelable("code", project);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_reminder:
                        fragment = new ReminderProjectFragment();
                        fragment.setArguments(args);
                        break;
                    case R.id.action_directory:
                        fragment = new ShareFragment();
                        break;
                    case R.id.action_message:
                        fragment = new ProjectFragment();
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        break;
                    default:
                        fragment = new ToDoProjectFragment();
                        fragment.setArguments(args);
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_reminder);
    }
}