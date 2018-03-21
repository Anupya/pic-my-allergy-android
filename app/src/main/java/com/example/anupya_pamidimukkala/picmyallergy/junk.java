package com.example.anupya_pamidimukkala.picmyallergy;


/* Initial code

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



/*  Writing to a file gives java.lang.io.FileNotFoundException

    // write to allergies.json
    try
    {
        // Create a new JSONArray object
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        // Add the values to the jsonObject
        for (int i = 0; i < 1; i++) {

            // Create a new JSONObject
            jsonObject = new JSONObject();

            // Add fields + values to the JSONObject
            jsonObject.put("Allergies", "rutabaga");

            // Add values to the jsonArray
            jsonArray.put(jsonObject);
        }

        String text = "Hello world";
        BufferedWriter output = null;
        try {
            File file = new File("allergies.json");
            output = new BufferedWriter(new FileWriter(file));
            output.write(text);
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( output != null ) {
                output.close();
            }
        }

        System.out.println("JSON Object Successfully written to the file!!");

    } catch (Exception e)
    {
        e.printStackTrace();
    }
*/


/*
String[] allergies = new String[checked.size()];


for (int i = 0; i < checked.size(); i++) {
    int position = checked.keyAt(i);
    if (checked.valueAt(i)) {
        allergies.add(adapter.getItem(position));
    }
}

Log.e("SELECTEDITEMS[0]= ", selectedItems[0]);
*/


/*
// pass all selected allergies to next view
MultiSpinner spinner = (MultiSpinner) findViewById(R.id.spinnerMulti);
if (spinner.getAllText() == null) {
    Log.e("GET ALL TEXT IS NULL", "GET ALL TEXT IS NULL");
}
if (spinner.getDefaultText() == null) {
    Log.e("GET DEFAULT TEXT NULL", "GET DEFAULT TEXT IS NULL");
}
Log.e("spinnerText: ", spinner.getSelected().toString());
*/