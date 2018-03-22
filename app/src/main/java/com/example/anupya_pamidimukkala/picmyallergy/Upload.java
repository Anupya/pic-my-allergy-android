package com.example.anupya_pamidimukkala.picmyallergy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Upload extends AppCompatActivity {

    // --------------------------------------
    // IMAGE
    // --------------------------------------

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    String[] allergies;
    HashMap<String, Float> danger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Bundle b=this.getIntent().getExtras();
        String[] receivedAllergies = b.getStringArray("allergies");
        allergies = receivedAllergies;

        this.imageView = (ImageView)this.findViewById(R.id.imageView);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    // return a unique file name for a new photo using a date-time stamp
    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    // Take a photo with a camera app
    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    // Get the thumbnail and display it in ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }

        // get button, create a button click listener and register the listener to the button
        Button btnNextScreen = (Button) findViewById(R.id.amiallergic);
        Upload.ButtonListener btnClickListener = new Upload.ButtonListener();
        btnNextScreen.setOnClickListener(btnClickListener);
    }

    // takes you to the Camera (Upload) view
    class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            // --------------------------------------
            // MACHINE LEARNING
            // --------------------------------------



            // send danger map to Results activity
            Context context = v.getContext();
            Intent intent = new Intent(context, Results.class);
            intent.putExtra("danger", danger);
            startActivity(intent);
        }
    }

}
