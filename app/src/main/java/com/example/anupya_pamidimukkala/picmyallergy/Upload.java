package com.example.anupya_pamidimukkala.picmyallergy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class Upload extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1;
    ArrayList<String> allergies;
    ArrayList<Integer> allergyNums;
    HashMap<String, Float> danger;
    Bitmap image;
    Intent cameraIntent;
    Uri imageURI;

    public static final int GET_FROM_GALLERY = 3;


    //@Inject App App;
    @BindView(R.id.resultsList) RecyclerView resultsList;
    //@BindView(R.id.imageView) ImageView imageView;
    ImageView imageView = null;

    // the FAB that the user clicks to select an image
    //@BindView(R.id.fab) View fab;
    @NonNull private final RecognizeConceptsAdapter adapter = new RecognizeConceptsAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        imageView = findViewById(R.id.imageView);
        imageURI = null;

        Bundle b = this.getIntent().getExtras();

        try {
            allergies = b.getStringArrayList("allergies");
            allergyNums = b.getIntegerArrayList("allergyNums");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        danger = new HashMap<>();
        Log.e("UPLOAD", "CAMERAINTENT IS INTIIALIZED");
        cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override protected void onStart() {
        super.onStart();

        resultsList = findViewById(R.id.resultsList);
        resultsList.setLayoutManager(new LinearLayoutManager(this));
        resultsList.setAdapter(this.adapter);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // requestCode is 1
        // resultCode is -1

        Log.e("ONACTIVITYRESULT", "INSIDE");
        // if x is pressed, resultCode == RESULT_CANCELED

        if (resultCode == RESULT_OK){
            switch (requestCode) {
                case CAMERA_REQUEST:

                    final byte[] imageBytes = ClarifaiUtil.retrieveSelectedImage(this, data, false);

                    // sets the view to hold the image
                    image = (Bitmap) data.getExtras().get("data");

                    // you will be sending the snap now
                    imageURI = null;
                    if (image == null) {
                        Log.e("ONACTIVITYRESULT", "IMAGE IS NULL");
                    }
                    imageView.setImageBitmap((Bitmap)data.getExtras().get("data"));

                    if (imageBytes != null) {
                        onImagePicked(imageBytes);
                    }
                    break;

                case GET_FROM_GALLERY:

                    Log.e("ONACTIVITYRESULT", "GETTING FROM GALLERY");
                    final byte[] imageBytesYo = ClarifaiUtil.retrieveSelectedImage(this, data, true);

                    Log.e("ONACTIVITYRESULT", "BEFORE PERMISSIONS");

                    // request run time permission to read external storage
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Log.e("ONACTIVITYRESULT", "PERMISSIONS ARE ABOUT TO BE GRANTED");

                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GET_FROM_GALLERY);

                        Log.e("ONACTIVITYRESULT", "PERMISSIONS HAVE BEEN SUCCESSFULLY GRANTED");
                    }

                    // all good
                    else {

                        try {
                            image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                            imageURI = data.getData();

                            Log.e("ONACTIVITYRESULT", "SUCCESSFULLY PUT IMAGE IN image");
                            imageView.setImageBitmap(image);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (image == null) {
                        Log.e("ONACTIVITYRESULT", "IMAGE IS NULL");
                    }

                    if (imageBytesYo != null) {
                        onImagePicked(imageBytesYo);
                    }

                    Log.e("ONACTIVITYRESULT", "REACHED THE END OF THE ONACTIVITYRESULT");
                    break;

            }
        }


        // get button, create a button click listener and register the listener to the button
        Button btnNextScreen = (Button) findViewById(R.id.amiallergic);
        Upload.ButtonListener btnClickListener = new Upload.ButtonListener();
        btnNextScreen.setOnClickListener(btnClickListener);

        Button btnAnother = (Button) findViewById(R.id.another);
        Upload.ButtonListenerAnother btnClickListenerAnother = new Upload.ButtonListenerAnother();
        btnAnother.setOnClickListener(btnClickListenerAnother);

        Button btnUpload = (Button) findViewById(R.id.uploadBtn);
        Upload.ButtonListenerUpload btnClickListenerUpload = new Upload.ButtonListenerUpload();
        btnUpload.setOnClickListener(btnClickListenerUpload);

        Log.e("ONACTIVITYRESULT", "DOES BTN NEXT SCREEN HAVE ONCLICK LISTENERS" + String.valueOf(btnNextScreen.hasOnClickListeners()));
    }

    // Upload image to Clarifai API
    private void onImagePicked(@NonNull final byte[] imageBytes) {
        Log.e("ONIMAGEPICKED", "inside onImagePicked");
        //setBusy(true); coz imageView is null
        adapter.setData(Collections.<Concept>emptyList());

        class MyTask extends AsyncTask<Void, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>> {
            private WeakReference<Upload> activityReference;

            private MyTask(Upload context){
                activityReference = new WeakReference<>(context);
            }

            @Override protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Void... params) {

                // FOOD MODEL
                App app = new App();
                final ConceptModel foodModel = app.get().clarifaiClient().getDefaultModels().foodModel();
                Log.e("ONIMAGEPICKED", "FOOD MODEL DAWG");

                return foodModel.predict()
                        .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(imageBytes)))
                        .executeSync();
            }

            @Override protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
                Log.e("ONIMAGEPICKED", "inside onPostExecute");
                //setBusy(false);
                if (!response.isSuccessful()) {
                    Log.e("ONIMAGEPICKED", "RESPONSE IS NOT SUCCESSFUL");
                    Toast.makeText(Upload.this, "Permission denied to read your external storage", Toast.LENGTH_SHORT).show();
                }

                final List<ClarifaiOutput<Concept>> predictions = response.get();
                Log.e("ONIMAGEPICKED", "PREDICTIONS = " + String.valueOf(predictions));

                if (predictions.isEmpty()) {
                    return;
                }
                Log.e("ONIMAGEPICKED", "predictions are not empty");
                Log.e("ONIMAGEPICKED", "response is successful");

                // returns RecognizeConceptsAdapter that has the data
                adapter.setData(predictions.get(0).data());
                Log.e("ONIMAGEPICKED", "adapter.setData has run");
                Log.e("ONIMAGEPICKED", String.valueOf(adapter.setData(predictions.get(0).data())));
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                Log.e("ONIMAGEPICKED", "imageView.setImageBitmap has run");

                List<Concept> concepts = adapter.getConcepts();
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    Log.e("ONIMAGEPICKED", "ADAPTER ITEM" + String.valueOf(i) + String.valueOf(concepts.get(i)));
                    Log.e("ONIMAGEPICKED", "ADAPTER ITEM" + String.valueOf(i) + String.valueOf(concepts.get(i).name()));
                }

                // create an array of top 20 tags returned by Clarifai
                String[] tags =  new String[20];
                for (int i = 0; i < 20; i++) {
                    tags[i] = String.valueOf(concepts.get(i).name());
                }

                // name = String.valueOf(concepts.get(i).name())
                // allergy = allergies[0];
                Log.e("ONIMAGEPICKED", "ALLERGIES LENGTH " + String.valueOf(allergies.size()));
                Log.e("ONIMAGEPICKED", "TAG LENGTH " + String.valueOf(tags.length));

                // compare to see if there are any matches, if so add to danger hashmap
                for (int i = 0; i < allergies.size(); i++) {
                    Log.e("ONIMAGEPICKED", "ALLERGIES " + String.valueOf(allergies.get(i)));
                    for (int j = 0; j < tags.length; j++) {
                        Log.e("ONIMAGEPICKED", "TAGS " + String.valueOf(tags[j]));
                        if (allergies.get(i).equals(tags[j])) {
                            danger.put(tags[j], concepts.get(j).value());
                            Log.e("ONIMAGEPICKED", "DANGER ENTRY: " +
                                    String.valueOf(tags[j]) + String.valueOf(danger.get(tags[j])));
                        }
                    }
                }
                Log.e("ONIMAGEPICKED", "TAG AND ALLERGY ARRAYS HAVE BEEN CREATED");
            }
        }

        try {
            new MyTask(this).execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    // takes you to the Results view
    class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.e("CLICKED", "AM I ALLERGIC");
            TextView view = findViewById(R.id.noimage);
            // send danger map to Results activity
            Context context = v.getContext();
            Intent intent = new Intent(context, Results.class);

            // no image selected
            if ((imageURI == null) && (image == null)) {
                view.setVisibility(View.VISIBLE);
                return;
            }

            // it is a screenshot
            else if (imageURI == null) {
                view.setVisibility(View.INVISIBLE);
                intent.putExtra("image", image);
                intent.putExtra("danger", danger);
                intent.putExtra("allergies", allergies);
                intent.putExtra("allergyNums", allergyNums);
            }

            // it is a photo from gallery
            else {
                view.setVisibility(View.INVISIBLE);
                intent.putExtra("imageURI", imageURI.toString());
                intent.putExtra("danger", danger);
                intent.putExtra("allergies", allergies);
                intent.putExtra("allergyNums", allergyNums);
            }

            startActivity(intent);

        }
    }

    // take another photo
    class ButtonListenerAnother implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.e("CLICKED", "ANOTHER");

            // send danger map to Results activity
            Context context = v.getContext();
            Intent intent = new Intent(context, Upload.class);

            intent.putExtra("allergies", allergies);

            startActivity(intent);
        }
    }

    // upload photo from phone gallery
    class ButtonListenerUpload implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.e("CLICKED", "UPLOAD PHOTO FROM GALLERY");

            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
        }
    }
}
