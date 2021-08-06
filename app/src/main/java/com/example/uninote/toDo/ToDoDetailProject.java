package com.example.uninote.toDo;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.uninote.R;
import com.example.uninote.models.GeneratorId;
import com.example.uninote.models.PhotoTaken;
import com.example.uninote.models.ProjectFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class ToDoDetailProject extends PhotoTaken {

    public static final String TAG = "ToDoUpload";
    private final DatabaseReference rootDatabase = FirebaseDatabase.getInstance().getReference();
    private EditText etTitle;
    private EditText etDescription;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private ProjectFirebase project;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

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