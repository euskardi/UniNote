package com.example.uninote.toDo;

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

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.uninote.ProjectActivity;
import com.example.uninote.R;
import com.example.uninote.models.PhotoTaken;
import com.example.uninote.models.ProjectFirebase;
import com.example.uninote.models.ToDoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class EditToDoProject extends PhotoTaken {

    private final DatabaseReference rootDatabase = FirebaseDatabase.getInstance().getReference();
    private TextView tvName;
    private EditText etTitle;
    private EditText etDescription;
    private ImageButton btnCaptureImage;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private ToDoFirebase toDo;
    private ProjectFirebase project;

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
        project = getIntent().getParcelableExtra(ProjectFirebase.class.getSimpleName());

        tvName.setText("Edit ToDo");
        etTitle.setText(toDo.getTitle());
        etDescription.setText(toDo.getDescription());
        try {
            Glide.with(this).load(toDo.getUrl()).into(ivPostImage);
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
                    Toast.makeText(EditToDoProject.this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateToDo(title, description, photoFile, toDo, EditToDoProject.this);
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
        getMenuInflater().inflate(R.menu.menu_detail_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        final Intent intentProject = new Intent(this, ProjectActivity.class);
        switch (item.getItemId()) {
            case R.id.delete:
                deleteToDo(toDo);
                return true;

            case R.id.cancel:
                intentProject.putExtra(ProjectFirebase.class.getSimpleName(), project);
                this.startActivity(intentProject);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteToDo(ToDoFirebase toDo) {
        rootDatabase.child("ToDos").child(toDo.getId()).removeValue();
        updateScore(false, project, EditToDoProject.this);

    }

}