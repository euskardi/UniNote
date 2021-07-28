package com.example.uninote.toDo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.uninote.MainActivity;
import com.example.uninote.ProjectActivity;
import com.example.uninote.R;
import com.example.uninote.models.PhotoTaken;
import com.example.uninote.models.Project;
import com.example.uninote.models.ToDo;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;

public class ToDoDetailProject extends PhotoTaken {

    public static final String TAG = "ToDoUpload";
    private EditText etTitle;
    private EditText etDescription;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private Project project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_detail);

        etTitle = findViewById(R.id.etInputTitleToDo);
        etDescription = findViewById(R.id.etInputDescription);
        ImageButton btnCaptureImage = findViewById(R.id.btnPhoto);
        ivPostImage = findViewById(R.id.ivImageToDo);
        btnSubmit = findViewById(R.id.btnCreateToDo);

        project = Parcels.unwrap(getIntent().getParcelableExtra(Project.class.getSimpleName()));

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = etTitle.getText().toString();
                final String description = etDescription.getText().toString();
                if (description.isEmpty() || title.isEmpty()) {
                    Toast.makeText(ToDoDetailProject.this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                final ParseUser currentUser = ParseUser.getCurrentUser();
                saveToDo(title, description, currentUser, photoFile);
            }
        });

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

    }

    private void saveToDo(String title, String description, ParseUser currentUser, File photoFile) {
        final ToDo toDo = new ToDo();
        toDo.setTitle(title);
        toDo.setContent(description);
        if (photoFile != null && ivPostImage.getDrawable() != null) {
            toDo.setImage(new ParseFile(photoFile));
        }
        toDo.setUser(currentUser);
        toDo.setProject(project);

        toDo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(ToDoDetailProject.this, "Error while saving", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save was succesful!!");
                etDescription.setText("");
                ivPostImage.setImageResource(0);
            }
        });

        final Intent intentProject = new Intent(this, ProjectActivity.class);
        intentProject.putExtra(Project.class.getSimpleName(), Parcels.wrap(project));
        this.startActivity(intentProject);
        finish();
    }
}