package com.example.uninote;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.uninote.models.PhotoTaken;
import com.example.uninote.models.Reminder;
import com.example.uninote.models.ToDo;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class ToDoDetailActivity extends PhotoTaken {

    public static final String TAG = "ToDoUpload";
    private EditText etTitle;
    private EditText etDescription;
    private EditText etShareCode;
    private ImageButton btnCaptureImage;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private Button btnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_detail);

        findViewById(R.id.shareToDo).setVisibility(View.VISIBLE);
        etTitle = findViewById(R.id.etInputTitleToDo);
        etDescription = findViewById(R.id.etInputDescription);
        etShareCode = findViewById(R.id.etShareCodeToDo);
        btnCaptureImage = findViewById(R.id.btnPhoto);
        ivPostImage = findViewById(R.id.ivImageToDo);
        btnSubmit = findViewById(R.id.btnCreateToDo);
        btnShare = findViewById(R.id.btnLinkToDo);


        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etShareCode.getText().toString().isEmpty()) {
                    Toast.makeText(ToDoDetailActivity.this, "Empty Code", Toast.LENGTH_SHORT).show();
                    return;
                }
                linkToDo(etShareCode.getText().toString());
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = etTitle.getText().toString();
                final String description = etDescription.getText().toString();
                if (description.isEmpty() || title.isEmpty()) {
                    Toast.makeText(ToDoDetailActivity.this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
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

    private void linkToDo(String code) {
        final ParseObject entity = new ParseObject("User_ToDo");
        entity.put("username", ParseUser.getCurrentUser());

        ParseQuery<ToDo> query = ParseQuery.getQuery("ToDo");
        query.getInBackground(code, (object, e) -> {
            if (e != null) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            entity.put("toDo", object);
            entity.saveInBackground(new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if (e == null) {
                        Log.i(TAG, "Link was successfully!!");
                        return;
                    }
                    Log.e(TAG, "Error while saving 2", e);
                    Toast.makeText(ToDoDetailActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                }
            });
        });
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    private void saveToDo(String title, String description, ParseUser currentUser, File photoFile) {
        final ToDo toDo = new ToDo();
        toDo.setTitle(title);
        toDo.setContent(description);
        if (photoFile != null && ivPostImage.getDrawable() != null) {
            toDo.setImage(new ParseFile(photoFile));
        }
        toDo.setUser(currentUser);

        toDo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(ToDoDetailActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save was succesful!!");
                etDescription.setText("");
                ivPostImage.setImageResource(0);
            }
        });

        final ParseObject entity = new ParseObject("User_ToDo");
        final ParseACL parseACL = new ParseACL(ParseUser.getCurrentUser());
        parseACL.setPublicReadAccess(true);
        ParseUser.getCurrentUser().setACL(parseACL);

        entity.put("username", currentUser);
        entity.put("toDo", toDo);

        entity.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    Log.i(TAG, "Post save was succesful!!");
                    return;
                }
                Log.e(TAG, "Error while saving 2", e);
                Toast.makeText(ToDoDetailActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
            }
        });

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}