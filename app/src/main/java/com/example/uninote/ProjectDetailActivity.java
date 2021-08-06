package com.example.uninote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.uninote.models.GeneratorId;
import com.example.uninote.models.Project;
import com.example.uninote.models.ProjectFirebase;
import com.example.uninote.models.ReminderFirebase;
import com.example.uninote.models.ToDo;
import com.example.uninote.models.UserHasProject;
import com.example.uninote.models.UserHasReminder;
import com.example.uninote.toDo.ToDoDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.UUID;

public class ProjectDetailActivity extends AppCompatActivity {

    final private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static final String TAG = "ProjectUpload";
    private EditText etTitle;
    private EditText etDescription;
    private EditText etShareCode;
    private Button btnSubmit;
    private Button btnShare;

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

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
                saveToDo(title, description);
            }
        });
    }

    private void saveToDo(String title, String description) {

        final String id = GeneratorId.get();
        final ProjectFirebase projectFirebase = new ProjectFirebase();
        final UserHasProject userHasProject = new UserHasProject(firebaseAuth.getUid(), id, true);
        final SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");

        projectFirebase.setName(title);
        projectFirebase.setDescription(description);
        projectFirebase.setEditor(firebaseAuth.getUid());
        projectFirebase.setCountTodos(0);
        projectFirebase.setCountReminders(0);

        rootNode = FirebaseDatabase.getInstance();

        reference = rootNode.getReference("Project");
        reference.child(id).setValue(projectFirebase);
        reference = rootNode.getReference("UserHasProject");
        reference.child(UUID.randomUUID().toString()).setValue(userHasProject);

        startActivity(new Intent(this, MainActivity.class));
        finish();


    }

    private void linkProject(String code) {
        final String[] parts = code.split("-");
        Log.i(TAG, parts[0] + " " + parts[1]);
        final ParseObject entity = new ParseObject("User_Project");

        entity.put("username", firebaseAuth.getUid());
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