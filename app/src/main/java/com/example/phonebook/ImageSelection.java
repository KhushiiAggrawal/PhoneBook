package com.example.phonebook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageSelection extends AppCompatActivity {
    Uri photoUri= null;
    ActivityResultLauncher<Intent> cameraLauncher;
    ActivityResultLauncher<Intent> launcher;
    CardView camera,gallery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection);

        camera = findViewById(R.id.camera);
        gallery = findViewById(R.id.gallery);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(cameraIntent);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentimg = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                launcher.launch(intentimg);
            }
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Bundle extras = result.getData().getExtras();
                Bitmap imgbitmap = (Bitmap) extras.get("data");
                photoUri = saveImage(imgbitmap, ImageSelection.this);
                Toast.makeText(getApplicationContext(), "uri saved", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("photouri", photoUri.toString());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                            Intent data = result.getData();
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                                Bitmap imgbitmap = BitmapFactory.decodeStream(inputStream);
                                photoUri = saveImage(imgbitmap, ImageSelection.this);
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("photouri", photoUri.toString());
                            setResult(RESULT_OK, resultIntent);
                            finish();
//                        }
                    }
                });
    }
    private Uri saveImage(Bitmap imgbitmap, Context context ){
        File imagesFolder = new File(context.getCacheDir(),"images");
        Uri uri = null;
        try{
            if (!imagesFolder.exists()) {
                imagesFolder.mkdirs();
            }
            File file = new File(imagesFolder + "captured_image.jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            imgbitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream );
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(context.getApplicationContext(),"com.example.phonebook"+ ".provider",file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return uri;
    }

}