package com.example.anupya_pamidimukkala.picmyallergy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private MultiSpinner spinner;
    private MultiSpinner.MyAdapter adapter;
    private ArrayList <KeyPairBoolData> items;
    private ArrayList <Integer> allergyNums;
    private ArrayList<String> allergies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // hide error messages
        TextView atleast1 = findViewById(R.id.atleast1);
        atleast1.setVisibility(View.INVISIBLE);

        // get button, create a button click listener and register the listener to the button
        Button btnNextScreen = (Button) findViewById(R.id.btnNextScreen);
        ButtonListener btnClickListener = new ButtonListener();
        btnNextScreen.setOnClickListener(btnClickListener);

        // create a spinner
        spinner = findViewById(R.id.spinnerMulti);

        // create an adapter
        adapter = spinner.new MyAdapter(this, new ArrayList<KeyPairBoolData>());
        spinner.setAdapter(adapter);

        // Populate dropdown with all the foods
        String json = null;
        try {
            InputStream is = getAssets().open("foods.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        items = new ArrayList<>();
        try {
            JSONArray jArray = new JSONArray(json);
            Log.e("MAINACTIVITY", "JSON ARRAY CREATED");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jo_inside = jArray.getJSONObject(i);
                KeyPairBoolData obj = new KeyPairBoolData();
                obj.setName(jo_inside.getString("foods"));
                obj.setIndex(i);
                obj.setSelected(false);
                Log.e("MAINACTIVITY", "ADDING FOOD ITEMS TO ADAPTER");
                items.add(obj);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // go through list of allergies and set the  true if found in foods list
        // or set some as default if no prev data
        try {
            Log.e("MAIN ACTIVITY", "TRYING ALLERGYNUMS");
            allergyNums = getIntent().getExtras().getIntegerArrayList("allergyNums");
            Log.e("MAIN ACTIVITY", "ALLERGYNUMS HAVE BEEN RECEIVED");
            for (int i = 0; i < allergyNums.size(); i++) {
                Log.e("MAIN ACTIVITY", String.valueOf(i));
                spinner.setItems(items, allergyNums.get(i), null);
                Log.e("MAIN ACTIVITY", "ALLERGYNUM: " + String.valueOf(i));
            }
        }
        catch (Exception e) {
            Log.e("MAINACTIVITY", "ALLERGY NUMS IS EMPTY");
            allergyNums = new ArrayList<>();
            spinner.setItems(items, 0, null);
            spinner.setItems(items, 2, null);
            allergyNums.add(0);
            allergyNums.add(2);
            Log.e("MAINACTIVITY", "SET DEFAULTS TO ACORN SQUASH AND ALMONDS");
        }

        adapter.notifyDataSetChanged();
    }

    // takes you to the Camera (Upload) view
    class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            // retrieve all the allergies and store them in ArrayList<String>
            List<KeyPairBoolData> checked = spinner.getSelectedItems();

            allergies = new ArrayList<>();
            allergyNums = new ArrayList<>();

            int counter = 0;

            // add each one to separate allergies ArrayList<string>
            for (int i = 0; i < checked.size(); i++)
            {
                Log.e("MAINACTIVITY", "CHECKED[I] IS TRUE");
                allergies.add(checked.get(i).getName());
                allergyNums.add(checked.get(i).getIndex());

                Log.e("ENTRY #", String.valueOf(i));
                Log.e("ENTRY IN CHECKED: ", String.valueOf(checked.get(i)));
                Log.e("ENTRY IN CHECKED: ", String.valueOf(adapter.getItem(i)));
                Log.e("ENTRY IN ALLERGIES: ", String.valueOf(allergies.get(counter)));
                Log.e("INDEX IN ALLERGIES: ", String.valueOf(allergyNums.get(i)));
                counter++;

            }

            if (allergies.size() == 0) {
                TextView atleast1 = findViewById(R.id.atleast1);
                atleast1.setVisibility(View.VISIBLE);
            }

            else {
                // go to Upload activity
                Bundle b = new Bundle();
                b.putStringArrayList("allergies", allergies);
                b.putIntegerArrayList("allergyNums", allergyNums);
                Context context = v.getContext();
                Intent intent = new Intent(context, Upload.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        }
    }
}
