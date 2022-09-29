package com.example.chattingapp.imagebgremover;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import com.slowmac.autobackgroundremover.BackgroundRemover;
import com.slowmac.autobackgroundremover.OnBackgroundChangeListener;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CAMERA = 200;
    Button camera;
    ImageView image;
    ActivityResultLauncher<Intent> resultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                Intent intent = result.getData();
                                Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
                                 removeBackground(bitmap);
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
   camera=findViewById(R.id.take_photo);
        image = findViewById(R.id.imageView);
        camera.setOnClickListener(view -> {
            Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            resultLauncher.launch(openCamera);
        });


    }

    private void removeBackground(Bitmap bitmap) {
        BackgroundRemover.INSTANCE.bitmapForProcessing(bitmap, false, new OnBackgroundChangeListener() {
            @Override
            public void onSuccess(@NonNull Bitmap bitmap) {
                image.setImageBitmap(bitmap);
            }
            @Override
            public void onFailed(@NonNull Exception e) {

            }
        });

    }
}