package com.example.uninote.toDo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.uninote.MainActivity;
import com.example.uninote.ProjectActivity;
import com.example.uninote.R;
import com.example.uninote.models.GeneratorId;
import com.example.uninote.models.PhotoTaken;
import com.example.uninote.models.Project;
import com.example.uninote.models.ProjectFirebase;
import com.example.uninote.models.ReminderFirebase;
import com.example.uninote.models.ToDo;
import com.example.uninote.models.ToDoFirebase;
import com.example.uninote.models.UserHasToDo;
import com.example.uninote.reminder.ReminderDetailProject;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class ToDoDetailProject extends PhotoTaken {

    public static final String TAG = "ToDoUpload";
    private EditText etTitle;
    private EditText etDescription;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private ProjectFirebase project;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private final DatabaseReference rootDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_detail);

        etTitle = findViewById(R.id.etInputTitleToDo);
        etDescription = findViewById(R.id.etInputDescription);
        ImageButton btnCaptureImage = findViewById(R.id.btnPhoto);
        ivPostImage = findViewById(R.id.ivImageToDo);
        btnSubmit = findViewById(R.id.btnCreateToDo);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        project = getIntent().getParcelableExtra(ProjectFirebase.class.getSimpleName());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = etTitle.getText().toString();
                final String description = etDescription.getText().toString();
                if (description.isEmpty() || title.isEmpty()) {
                    Toast.makeText(ToDoDetailProject.this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveToDo(title, description, photoFile, project.getName());
            }
        });

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

    }

    private void saveToDo(String title, String description, File photoFile, String name) {
        final String id = GeneratorId.get();

        toDoFirebase.setTitle(title);
        toDoFirebase.setDescription(description);
        toDoFirebase.setId(id);
        toDoFirebase.setProject(name);
        if (photoFile != null) uploadImage(ToDoDetailProject.this);

        rootNode = FirebaseDatabase.getInstance();

        final Runnable r = new Runnable() {
            @Override
            public void run() {
                reference = rootNode.getReference("ToDos");
                reference.child(id).setValue(toDoFirebase);
            }
        };

        final Handler h = new Handler();
        h.postDelayed(r, 5000);
        updateScore(true, project, ToDoDetailProject.this);
    }

}