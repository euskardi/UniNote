package com.example.uninote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.uninote.fragments.MessageFragment;
import com.example.uninote.fragments.ProfileFragment;
import com.example.uninote.fragments.ReminderProjectFragment;
import com.example.uninote.fragments.ShareFragment;
import com.example.uninote.fragments.ToDoProjectFragment;
import com.example.uninote.models.ProjectFirebase;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProjectActivity extends AppCompatActivity {

    public final static String TAG = "Project";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        final ProjectFirebase project = getIntent().getParcelableExtra(ProjectFirebase.class.getSimpleName());
        final Bundle args = new Bundle();
        args.putParcelable("code", project);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_directory:
                        fragment = new ShareFragment();
                        fragment.setArguments(args);
                        break;
                    case R.id.action_reminder:
                        fragment = new ReminderProjectFragment();
                        fragment.setArguments(args);
                        break;
                    case R.id.action_todo:
                        fragment = new ToDoProjectFragment();
                        fragment.setArguments(args);
                        break;
                    case R.id.action_message:
                        fragment = new MessageFragment();
                        fragment.setArguments(args);
                        break;
                    default:
                        fragment = new ProfileFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_reminder);
    }
}