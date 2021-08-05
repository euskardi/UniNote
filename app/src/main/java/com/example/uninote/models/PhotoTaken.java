package com.example.uninote.models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.uninote.ProjectActivity;
import com.example.uninote.R;
import com.example.uninote.toDo.EditToDo;
import com.example.uninote.toDo.EditToDoProject;
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
import com.parse.ParseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.UUID;

public class PhotoTaken extends AppCompatActivity {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final String photoFileName = "photo.jpg";
    public static final String TAG = "Camara";
    public File photoFile;
    public Bitmap takenImage;
    public ImageView ivPostImage;
    public Uri fileProvider;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private final DatabaseReference rootDatabase = FirebaseDatabase.getInstance().getReference();
    public final ToDoFirebase toDoFirebase = new ToDoFirebase();


    public void launchCamera() {
        ivPostImage = findViewById(R.id.ivImageToDo);
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);
        fileProvider = FileProvider.getUriForFile(PhotoTaken.this, "com.codepath.fileprovider.UniNote", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(PhotoTaken.this.getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private File getPhotoFileUri(String fileName) {
        final File mediaStorageDir = new File(PhotoTaken.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            ivPostImage.setVisibility(View.VISIBLE);
            ivPostImage.setImageBitmap(takenImage);
        }
    }

    public void updateScore(Boolean type, ProjectFirebase project, Context context) {
        final HashMap hashMap = new HashMap();
        final int count = type ? 1 : -1;
        project.setCountTodos(project.getCountTodos() + count);
        hashMap.put("countTodos", project.getCountTodos());

        final Query innerQuery = FirebaseDatabase.getInstance().getReference("Project")
                .orderByChild("name")
                .equalTo(project.getName());

        innerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final String key = dataSnapshot.getKey();
                    rootDatabase.child("Project").child(key).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            final Intent intentProject = new Intent(context, ProjectActivity.class);
                            intentProject.putExtra(ProjectFirebase.class.getSimpleName(), project);
                            startActivity(intentProject);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error In Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadImage(Context context) {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
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
                        Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "No Uploaded", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void updateToDo(String title, String description, ParseUser currentUser, File photoFile, ToDoFirebase toDo, Context context) {
        final DatabaseReference rootDatabase = FirebaseDatabase.getInstance().getReference();

        final HashMap hashMap = new HashMap();
        hashMap.put("title", title);
        hashMap.put("description", description);


        if (photoFile != null && ivPostImage.getDrawable() != null) {
            uploadImage(context);
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
                            Toast.makeText(context, "Your data is Successfully Updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error In Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
