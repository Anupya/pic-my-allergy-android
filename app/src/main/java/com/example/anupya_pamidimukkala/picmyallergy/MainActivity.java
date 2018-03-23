package com.example.anupya_pamidimukkala.picmyallergy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SpinnerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import android.util.SparseBooleanArray;
import android.widget.Toast;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import static java.lang.System.out;
import android.os.Message;


public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create spinner list elements
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);

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

        try {
            JSONArray jArray = new JSONArray(json);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jo_inside = jArray.getJSONObject(i);
                adapter.add(jo_inside.getString("foods"));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // get button, create a button click listener and register the listener to the button
        Button btnNextScreen = (Button) findViewById(R.id.btnNextScreen);
        ButtonListener btnClickListener = new ButtonListener();
        btnNextScreen.setOnClickListener(btnClickListener);

        // get spinner and set adapter
        MultiSpinner spinner = (MultiSpinner) findViewById(R.id.spinnerMulti);
        spinner.setAdapter(adapter, false, onSelectedListener);

        // set initial selection
        boolean[] selectedItems = new boolean[adapter.getCount()];
        selectedItems[0] = true; // select first item
        selectedItems[1] = true; // select second item
        spinner.setSelected(selectedItems);

        /*
        if (spinner.getAllText() == null) {
            Log.e("GET ALL TEXT IS NULL", "GET ALL TEXT IS NULL");
        }
        if (spinner.getDefaultText() == null) {
            Log.e("GET DEFAULT TEXT NULL", "GET DEFAULT TEXT IS NULL");
        }
        Log.e("spinnerText: ", spinner.getSelected().toString());
        */

    }

    // takes you to the Camera (Upload) view
    class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {


            // retrieve all the allergies and store them in String[]
            MultiSpinner spinner = (MultiSpinner) findViewById(R.id.spinnerMulti);
            SpinnerAdapter adapter = spinner.getAdapter();

            boolean[] checked = spinner.getSelected();

            ArrayList<String> allergies = new ArrayList<>();

            int counter = 0;
            for (int i = 0; i < checked.length; i++)
            {

                // if item is selected
                if (checked[i]) {
                    Log.e("MAINACTIVITY", "CHECKED[I] IS TRUE");
                    allergies.add(adapter.getItem(i).toString());

                    Log.e("ENTRY #", String.valueOf(i));
                    Log.e("ENTRY IN CHECKED: ", String.valueOf(checked[i]));
                    Log.e("ENTRY IN CHECKED: ", String.valueOf(adapter.getItem(i)));
                    Log.e("ENTRY IN ALLERGIES: ", String.valueOf(allergies.get(counter)));
                    counter++;
                }

            }

            // go to Upload activity
            Bundle b = new Bundle();
            b.putStringArrayList("allergies", allergies);
            Context context = v.getContext();
            Intent intent = new Intent(context, Upload.class);
            intent.putExtras(b);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // when OK is pressed
    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {

        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items

            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    builder.append(adapter.getItem(i)).append(" ");
                }
            }

            Log.e("In multispinner: ", "ONSELECTED LISTENER IS BEING PRINTED");
            // if you want to show a Toast
            //Toast.makeText(MainActivity.this, builder.toString(), Toast.LENGTH_SHORT).show();
        }
    };
}
