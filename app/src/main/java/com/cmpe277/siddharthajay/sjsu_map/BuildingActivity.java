package com.cmpe277.siddharthajay.sjsu_map;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BuildingActivity extends AppCompatActivity {

    //All statics for Google apis


    TextView building_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        //building toolbar
        Toolbar map_toolbar = (Toolbar) findViewById(R.id.map_toolbar);
        setSupportActionBar(map_toolbar);

        //status bar
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorGreen));

        //building data
        String[] buildingdetails = getIntent().getStringArrayExtra("BUILDING_DETAILS");
        String user_coordinates = getIntent().getStringExtra("COORDINATES");
        String building_coordinates = getIntent().getStringExtra("BLDG_COORDINATES");

        //Set up basic info about the building
        building_info = (TextView) findViewById(R.id.bldg_textView);
        String str_building_info = "";

        str_building_info = buildingdetails[0].toUpperCase() + "\n\n";
        str_building_info += "ADDRESS: \n" + buildingdetails[1] + "\n\n";
        //DEBUG:
        Constants.STR_BUILDING_COORDS_STREETVIEW = buildingdetails[2];

        building_info.setText(str_building_info);


        //Start AsyncTask here
        String url = Constants.STR_GOOG_API_BASE_URL + user_coordinates + Constants.STR_GOOG_API_DEST_URL + building_coordinates + Constants.STR_GOOG_API_MODE + Constants.STR_GOOG_API_KEY;
        new TimeEstimateTask().execute(url);

    }

    @Override
    protected void onResume() {
        super.onResume();
        int building_image_resource_id = getIntent().getIntExtra("BUILDING_NAME", R.drawable.bbc);
        //Set up building image
        ImageView buildingImageView = (ImageView) findViewById(R.id.bldg_imageView);
        buildingImageView.setImageResource(building_image_resource_id);
    }

    //Trigger street view, with the current building coordinates
    public void btnStreetViewLauncher(View view) {
        String strStreetView = "google.streetview:cbll=" + Constants.STR_BUILDING_COORDS_STREETVIEW;
        Uri gmmIntentUri = Uri.parse(strStreetView);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivityForResult(mapIntent, Constants.requestCodeStreetView);
    }

    //If we don't do this, then hitting back from streetview takes you to the map
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.requestCodeStreetView){
            if(requestCode == RESULT_OK){
                //User is back from street view
            }
        }
    }

    //AsyncTask to retrieve location matrix
    private class TimeEstimateTask extends AsyncTask<String, Integer, String> {

        protected void onPostExecute(String resultJsonString) {

            String TimeToTarget = "";
            String DistanceToTarget = "";

            String text = building_info.getText().toString();
            //Parse json
            try {

                JSONObject Object = new JSONObject(resultJsonString);
                JSONArray Array = Object.getJSONArray("rows");
                JSONObject Rows =  Array.getJSONObject(0);
                JSONArray ElementArray = Rows.getJSONArray("elements");
                //Get distance
                JSONObject ElementObject = ElementArray.getJSONObject(0);
                JSONObject Distance = ElementObject.getJSONObject("distance");
                DistanceToTarget = Distance.getString("text");
                //Get time
                JSONObject Duration = ElementObject.getJSONObject("duration");
                TimeToTarget = Duration.getString("text");

            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            building_info.setText(text + "TIME FROM CURRENT LOCATION: \n" +  TimeToTarget + "\n\n" + "DISTANCE TO LOCATION: \n" + DistanceToTarget);
        }

        @Override
        protected String doInBackground(String... args){

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            String url_weather = args[0];
            HttpURLConnection uConnection = null;
            BufferedReader bufferedReader = null;

            // Will contain the raw JSON response as a string.
            String JsonStr = null;

            try {
                URL url = new URL(url_weather);

                // Create the request to google distance matrix, and open the connection
                uConnection = (HttpURLConnection) url.openConnection();
                uConnection.setRequestMethod("GET");
                uConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = uConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                //Check for null stream
                if (buffer.length() == 0) {
                    return null;
                }
                JsonStr = buffer.toString();

            }
            catch (IOException e) {

                
                return null;
            }
            finally{
                if (uConnection != null) {
                    uConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {

                    }
                }
            }

            return JsonStr;//for success
        }



    }//End of async task


}
