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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.uninote.R;

import java.io.File;

public class PhotoTaken extends AppCompatActivity {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final String photoFileName = "photo.jpg";
    public static final String TAG = "Camara";
    public File photoFile;
    public Bitmap takenImage;
    public ImageView ivPostImage;

    public void launchCamera() {
        ivPostImage = findViewById(R.id.ivImageToDo);
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);
        final Uri fileProvider = FileProvider.getUriForFile(PhotoTaken.this, "com.codepath.fileprovider.UniNote", photoFile);
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

}
