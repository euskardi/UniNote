package com.example.uninote;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.uninote.models.PhotoTaken;
import com.example.uninote.models.ToDo;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.io.File;

public class EditToDo extends PhotoTaken {

    private TextView tvName;
    private EditText etTitle;
    private EditText etDescription;
    private ImageButton btnCaptureImage;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private ToDo toDo;

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

        toDo = Parcels.unwrap(getIntent().getParcelableExtra(ToDo.class.getSimpleName()));

        tvName.setText("Edit ToDo");
        etTitle.setText(toDo.getTitle());
        etDescription.setText(toDo.getContent());
        try {
            Glide.with(this).load(toDo.getImage().getUrl()).into(ivPostImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                updateToDo(title, description, currentUser, photoFile, toDo);
            }
        });

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
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

    private void deleteToDo(ToDo toDo) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("ToDo");
        query.getInBackground(toDo.getObjectId(), (object, e) -> {
            if (e != null) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            object.deleteInBackground(e2 -> {
                if (e2 == null) {
                    Toast.makeText(this, "Delete Successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error: " + e2.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateToDo(String title, String description, ParseUser currentUser, File photoFile, ToDo toDo) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("ToDo");
        query.getInBackground(toDo.getObjectId());
        query.getInBackground(toDo.getObjectId(), (object, e) -> {
            if (e != null) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            object.put("Title", title);
            object.put("Content", description);
            if (photoFile != null && ivPostImage.getDrawable() != null) {
                object.put("Photo", new ParseFile(photoFile));
            }
            object.put("Username", currentUser);
            object.saveInBackground();
        });
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}