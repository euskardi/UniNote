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
import com.example.uninote.R;
import com.example.uninote.models.GeneratorId;
import com.example.uninote.models.PhotoTaken;
import com.example.uninote.models.ReminderFirebase;
import com.example.uninote.models.ToDo;
import com.example.uninote.models.ToDoFirebase;
import com.example.uninote.models.UserHasReminder;
import com.example.uninote.models.UserHasToDo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ToDoDetailActivity extends PhotoTaken {

    public static final String TAG = "ToDoUpload";
    private EditText etTitle;
    private EditText etDescription;
    private EditText etShareCode;
    private ImageButton btnCaptureImage;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private Button btnShare;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    final private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_detail);

        etTitle = findViewById(R.id.etInputTitleToDo);
        etDescription = findViewById(R.id.etInputDescription);
        etShareCode = findViewById(R.id.etShareCodeToDo);
        btnCaptureImage = findViewById(R.id.btnPhoto);
        ivPostImage = findViewById(R.id.ivImageToDo);
        btnSubmit = findViewById(R.id.btnCreateToDo);
        btnShare = findViewById(R.id.btnLinkToDo);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = etTitle.getText().toString();
                final String description = etDescription.getText().toString();
                if (description.isEmpty() || title.isEmpty()) {
                    Toast.makeText(ToDoDetailActivity.this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveToDo(title, description, photoFile);
            }
        });

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

    }

    private void saveToDo(String title, String description, File photoFile) {

        final String id = GeneratorId.get();
        final UserHasToDo userHasToDo = new UserHasToDo(firebaseAuth.getUid(), id);
        final SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");

        toDoFirebase.setTitle(title);
        toDoFirebase.setDescription(description);
        toDoFirebase.setId(id);
        if (photoFile != null) uploadImage(ToDoDetailActivity.this);

        rootNode = FirebaseDatabase.getInstance();

        final Runnable r = new Runnable() {
            @Override
            public void run() {
                reference = rootNode.getReference("ToDos");
                reference.child(id).setValue(toDoFirebase);
                reference = rootNode.getReference("UserHasToDo");
                reference.child(UUID.randomUUID().toString()).setValue(userHasToDo);
            }
        };

        final Handler h = new Handler();
        h.postDelayed(r, 5000);

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}