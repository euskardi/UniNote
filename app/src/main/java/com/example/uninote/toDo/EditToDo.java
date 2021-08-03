package com.example.uninote.toDo;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.uninote.MainActivity;
import com.example.uninote.R;
import com.example.uninote.ShareContent;
import com.example.uninote.models.PhotoTaken;
import com.example.uninote.models.ReminderFirebase;
import com.example.uninote.models.ToDo;
import com.example.uninote.models.ToDoFirebase;
import com.example.uninote.reminder.EditReminder;
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
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EditToDo extends PhotoTaken {

    private TextView tvName;
    private EditText etTitle;
    private EditText etDescription;
    private ImageButton btnCaptureImage;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private ToDoFirebase toDo;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private final ToDoFirebase toDoFirebase = new ToDoFirebase();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_detail);

        tvName = findViewById(R.id.Title);
        etTitle = findViewById(R.id.etInputTitleToDo);
        etDescription = findViewById(R.id.etInputDescription);
        btnCaptureImage = findViewById(R.id.btnPhoto);
        ivPostImage = findViewById(R.id.ivImageToDo);

        btnSubmit = findViewById(R.id.btnCreateToDo);

        toDo = getIntent().getParcelableExtra(ToDoFirebase.class.getSimpleName());

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        tvName.setText("Edit ToDo");
        etTitle.setText(toDo.getTitle());
        etDescription.setText(toDo.getDescription());

        btnSubmit.setText("EDIT");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = etTitle.getText().toString();
                final String description = etDescription.getText().toString();
                if (description.isEmpty() || title.isEmpty()) {
                    Toast.makeText(EditToDo.this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                final ParseUser currentUser = ParseUser.getCurrentUser();
                updateToDo(title, description, currentUser, photoFile);
            }
        });

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });
    }

    private void uploadImage() {
        final String randomKey = UUID.randomUUID().toString();
        final StorageReference imageRef = storageReference.child("images/" + randomKey);

        imageRef.putFile(fileProvider)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful()) ;
                        Log.i(TAG, String.valueOf(urlTask.getResult()));
                        toDoFirebase.setUrl(String.valueOf(urlTask.getResult()));
                        Toast.makeText(EditToDo.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditToDo.this, "No Uploaded", Toast.LENGTH_SHORT).show();

                    }
                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                final Intent intent = new Intent(this, ShareContent.class);
                intent.putExtra(ToDo.class.getSimpleName(), Parcels.wrap(toDo));
                startActivity(intent);
                finish();
                return true;

            case R.id.delete:
                deleteToDo(toDo);
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;

            case R.id.cancel:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteToDo(ToDoFirebase toDo) {
        final DatabaseReference rootDatabase = FirebaseDatabase.getInstance().getReference();
        rootDatabase.child("ToDos").child(toDo.getId()).removeValue();

        final Query innerQuery = FirebaseDatabase.getInstance().getReference("UserHasToDo")
                .orderByChild("toDo")
                .equalTo(toDo.getId());

        innerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final String key = dataSnapshot.getKey();
                    rootDatabase.child("UserHasToDo").child(key).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditToDo.this, "Error In Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateToDo(String title, String description, ParseUser currentUser, File photoFile) {
        final DatabaseReference rootDatabase = FirebaseDatabase.getInstance().getReference();
        final SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");

        final HashMap hashMap = new HashMap();
        hashMap.put("title", title);
        hashMap.put("description", description);


        if (photoFile != null && ivPostImage.getDrawable() != null) {
            uploadImage();
            hashMap.put("url", toDoFirebase.getUrl());
        }

        final Query innerQuery = FirebaseDatabase.getInstance().getReference("ToDos")
                .orderByChild("title")
                .equalTo(toDo.getTitle());

        innerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final String key = dataSnapshot.getKey();
                    rootDatabase.child("ToDos").child(key).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(EditToDo.this, "Your data is Successfully Updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditToDo.this, "Error In Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}