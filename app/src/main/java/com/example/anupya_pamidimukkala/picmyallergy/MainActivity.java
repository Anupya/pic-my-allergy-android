package com.example.anupya_pamidimukkala.picmyallergy;

/*
 * Copyright (c) 2015. Thomas Haertel
 * Multispinner code only. Rest of the code is my own.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create spinner list elements
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);

        // JSON parse
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

        /*
        adapter.add("Item1");
        adapter.add("Item2");
        adapter.add("Item3");
        adapter.add("Item4");
        adapter.add("Item5");
        */

        // get spinner and set adapter
        MultiSpinner spinner = (MultiSpinner) findViewById(R.id.spinnerMulti);
        spinner.setAdapter(adapter, false, onSelectedListener);

        // set initial selection
        boolean[] selectedItems = new boolean[adapter.getCount()];
        selectedItems[1] = true; // select second item
        spinner.setSelected(selectedItems);
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


    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {

        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items

            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    builder.append(adapter.getItem(i)).append(" ");
                }
            }

            Toast.makeText(MainActivity.this, builder.toString(), Toast.LENGTH_SHORT).show();
        }
    };
}

/*

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                    /*
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

}
*/
