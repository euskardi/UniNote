package com.example.uninote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.uninote.models.Project;
import com.example.uninote.models.ToDo;
import com.example.uninote.toDo.ToDoDetailActivity;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ProjectDetailActivity extends AppCompatActivity {

    public static final String TAG = "ProjectUpload";
    private EditText etTitle;
    private EditText etDescription;
    private EditText etShareCode;
    private Button btnSubmit;
    private Button btnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        etTitle = findViewById(R.id.etInputTitleProject);
        etDescription = findViewById(R.id.etInputDescription);
        etShareCode = findViewById(R.id.etShareCodeProject);
        btnSubmit = findViewById(R.id.btnCreateProject);
        btnShare = findViewById(R.id.btnLinkProject);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etShareCode.getText().toString().isEmpty()) {
                    Toast.makeText(ProjectDetailActivity.this, "Empty Code", Toast.LENGTH_SHORT).show();
                    return;
                }
                linkProject(etShareCode.getText().toString());
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = etTitle.getText().toString();
                final String description = etDescription.getText().toString();
                if (description.isEmpty() || title.isEmpty()) {
                    Toast.makeText(ProjectDetailActivity.this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                final ParseUser currentUser = ParseUser.getCurrentUser();
                saveToDo(title, description, currentUser);
            }
        });
    }

    private void saveToDo(String title, String description, ParseUser currentUser) {
        final Project project = new Project();
        project.setTitle(title);
        project.setDescription(description);
        project.setEditor(currentUser);

        project.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(ProjectDetailActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Project save was succesful!!");
                etDescription.setText("");
            }
        });

        final ParseObject entity = new ParseObject("User_Project");
        final ParseACL parseACL = new ParseACL(ParseUser.getCurrentUser());
        parseACL.setPublicReadAccess(true);
        ParseUser.getCurrentUser().setACL(parseACL);

        entity.put("username", currentUser);
        entity.put("project", project);
        entity.put("type", true);

        entity.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    Log.i(TAG, "Porject save was succesful!!");
                    return;
                }
                Log.e(TAG, "Error while saving 2", e);
                Toast.makeText(ProjectDetailActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
            }
        });

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void linkProject(String code) {
        final String[] parts = code.split("-");
        Log.i(TAG, parts[0] + " " + parts[1]);
        final ParseObject entity = new ParseObject("User_Project");

        entity.put("username", ParseUser.getCurrentUser());
        entity.put("type", parts[1].equals("1"));


        ParseQuery<Project> query = ParseQuery.getQuery("Project");
        query.getInBackground(parts[0], (object, e) -> {
            if (e != null) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            entity.put("project", object);

            entity.saveInBackground(new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if (e == null) {
                        Log.i(TAG, "Link was successfully!!");
                        return;
                    }
                    Log.e(TAG, "Error while saving 2", e);
                    Toast.makeText(ProjectDetailActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                }
            });
        });
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}