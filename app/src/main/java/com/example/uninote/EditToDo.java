package com.example.uninote;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.uninote.models.ToDo;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;

public class EditToDo extends AppCompatActivity {

    public static final String TAG = "ToDoEdit";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private TextView tvName;
    private EditText etTitle;
    private EditText etDescription;
    private ImageButton btnCaptureImage;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private File photoFile;
    public String photoFileName = "photo.jpg";

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

        final ToDo toDo = Parcels.unwrap(getIntent().getParcelableExtra(ToDo.class.getSimpleName()));

        tvName.setText("Edit ToDo");
        etTitle.setText(toDo.getTitle());
        etDescription.setText(toDo.getContent());
        Glide.with(this).load(toDo.getImage().getUrl()).into(ivPostImage);
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

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(EditToDo.this, "com.codepath.fileprovider.UniNote", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(EditToDo.this.getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            ivPostImage.setVisibility(View.VISIBLE);
            ivPostImage.setImageBitmap(takenImage);
        } else {
            Toast.makeText(EditToDo.this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
        }
    }

    private File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(EditToDo.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }


    private void updateToDo(String title, String description, ParseUser currentUser, File photoFile, ToDo toDo) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("ToDo");
        query.getInBackground(toDo.getObjectId());
        query.getInBackground(toDo.getObjectId(), (object, e) -> {
            if (e == null) {
                object.put("Title", title);
                object.put("Content", description);
                if (photoFile != null && ivPostImage.getDrawable() != null) {
                    object.put("Photo", new ParseFile(photoFile));
                }
                object.put("Username", currentUser);
                object.saveInBackground();

            } else {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}