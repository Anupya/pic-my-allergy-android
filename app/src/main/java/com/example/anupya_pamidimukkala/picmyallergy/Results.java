package com.example.anupya_pamidimukkala.picmyallergy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Results extends AppCompatActivity {

    HashMap<String, Float> dangerFoods;
    Bitmap foodImage;
    ImageView imageView;
    ArrayList<String> allergies;
    ArrayList<Integer> allergyNums;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("RESULTS ONCREATE", "INSIDE");

        // display layout
        setContentView(R.layout.activity_results);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        try {
            allergies = getIntent().getExtras().getStringArrayList("allergies");
            allergyNums = getIntent().getExtras().getIntegerArrayList("allergyNums");
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (b!=null) {

            for (String key : b.keySet()) {
                Object value = b.get(key);
                Log.e("LISTING THE EXTRAS: ", String.valueOf(key));
            }

            dangerFoods = (HashMap <String, Float>) b.getSerializable("danger");
            TextView textView = findViewById(R.id.results);
            textView.setMovementMethod(new ScrollingMovementMethod());

            // THIS FOOD IS SAFE TO EAT
            if ((dangerFoods == null) || (dangerFoods.size() == 0)) {

                textView.append("There is a very high chance that this food is safe!");
                textView.setBackgroundColor(getResources().getColor(R.color.green));
                Log.e("THIS FOOD IS SAFE", "THIS FOOD IS SAFE TO EAT");
            }

            // THIS FOOD IS BAD FOR YOU
            else {
                Set<String> keys = dangerFoods.keySet();
                List<String> dangerKeys = new ArrayList<>(keys);
                textView.setBackgroundColor(getResources().getColor(R.color.red));

                SpannableStringBuilder sb = new SpannableStringBuilder();
                sb.append("<b>");

                for (int j = 0; j < dangerKeys.size(); j++) {
                    float percent = dangerFoods.get(dangerKeys.get(j));
                    percent*=100;
                    sb.append(String.valueOf(Math.round(percent)));
                    sb.append("%</b> chance that this food has <b>");
                    sb.append(dangerKeys.get(j));
                    sb.append("</b>\n<br><b>");
                }
                textView.setText(Html.fromHtml(String.valueOf(sb)));

                /*textView.append("There is a: \n<b>");
                for (int j = 0; j < dangerKeys.size(); j++) {

                    float percent = dangerFoods.get(dangerKeys.get(j));
                    percent*=100;
                    textView.append(String.valueOf(Math.round(percent)));
                    textView.append("%</b> chance that this food has <b>");
                    textView.append(dangerKeys.get(j));
                    textView.append("</b>\n<b>");
                }
                */
                Log.e("RESULTS ONCREATE", "HASHMAP" + dangerKeys.get(0));
            }
        }

        if (b.getParcelable("image") != null) {
            foodImage = b.getParcelable("image");
            if (imageView == null) {
                Log.e("RESULTS ONCREATE", "IMAGEVIEW IS NULL");
            }
            imageView = findViewById(R.id.imageView);
            if (imageView == null) {
                Log.e("RESULTS ONCREATE", "IMAGEVIEW IS NULL");
            }
            imageView.setImageBitmap(foodImage);
            Log.e("RESULTS ONCREATE", "IMAGE IS NOT NULL AND HAS BEEN RECEIVED");
        }
        else {
            Log.e("RESULTS ONCREATE", "IMAGE IS NULL");
        }

        // Create listeners for the buttons
        // get button, create a button click listener and register the listener to the button
        Button btnAllergies = (Button) findViewById(R.id.allergies);
        Results.ButtonListenerAllergies btnClickListenerAllergies = new Results.ButtonListenerAllergies();
        btnAllergies.setOnClickListener(btnClickListenerAllergies);

        Button btnAnother = (Button) findViewById(R.id.another);
        Results.ButtonListenerAnother btnClickListenerAnother = new Results.ButtonListenerAnother();
        btnAnother.setOnClickListener(btnClickListenerAnother);

    }

    // Upload
    class ButtonListenerAnother implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.e("CLICKED", "INSIDE");

            Context context = v.getContext();
            Intent intent = new Intent(context, Upload.class);

            intent.putExtra("allergies", allergies);
            startActivity(intent);
        }
    }

    // Main Activity
    class ButtonListenerAllergies implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.e("CLICKED", "INSIDE");

            Context context = v.getContext();
            Intent intent = new Intent(context, MainActivity.class);

            intent.putExtra("allergyNums", allergyNums);
            startActivity(intent);
        }
    }


}
