package com.example.chattingapp.imagebgremover;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsProvider;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static String Path="";

    Button camera;
    ImageView image;
    ActivityResultLauncher<Intent> chooseFolder =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                Intent intent = result.getData();
                                String path= String.valueOf(intent.getData().getPath().replaceAll("/tree/primary:",""));
                                path.trim();
                                Log.i("Path",path);
                                Uri selectPathUri=getImageUri(path);
                                if(selectPathUri!=null)
                                {
                                    Path=path;
                                    takeImage(selectPathUri);

                                }

                            }
                        }
                    }
            );

    ActivityResultLauncher<Intent> captureImage =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                Snackbar snackbar = Snackbar
                                        .make(camera, "Saved\n"+Path, Snackbar.LENGTH_LONG);
                                snackbar.show();

                            }
                        }
                    }
            );
    private ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            //
        }
    });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        camera = findViewById(R.id.take_photo);
        image = findViewById(R.id.imageView);
        String[] permissions = new String[]
                {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE

                };

        requestPermissionLauncher.launch(permissions);
        camera.setOnClickListener(view -> {
               Intent intent1=new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
               chooseFolder.launch(intent1);
        });


    }





    void takeImage(Uri uri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        captureImage.launch(intent);


    }
    private Uri getImageUri(String path)
    {
        Uri u=Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, UUID.randomUUID() + ".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        //if the folder do not exits then it will create a new folder
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,path);
        ContentResolver r = getContentResolver();
        Uri imageUri=null;
        try {
            imageUri= r.insert(u, contentValues);
        }
        catch (IllegalArgumentException exception)
        {
            Log.i("Path", String.valueOf(exception));
            Snackbar snackbar = Snackbar
                    .make(camera, "For Image\nallowed directories are DCIM,Pictures", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        return imageUri;

    }




}