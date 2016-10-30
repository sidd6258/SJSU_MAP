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
    public static final String STR_GOOG_API_BASE_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
    public static final String STR_GOOG_API_DEST_URL = "&destinations=";
    public static final String STR_GOOG_API_KEY = "&key=AIzaSyApv0T1SWbV7IKFWFDYl9o8T4VxZgX2fxc";
    public static final String STR_GOOG_API_MODE = "&mode=walking";
    public static String STR_BUILDING_COORDS_STREETVIEW = "46.414382,10.013988";
    public static int requestCodeStreetView = 1333;

    TextView building_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        //Set up building toolbar
        Toolbar map_toolbar = (Toolbar) findViewById(R.id.map_toolbar);
        setSupportActionBar(map_toolbar);
        /*final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
*/
        //Set up status bar
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorGreen));

        //Retrieve building data
        String[] building_details = getIntent().getStringArrayExtra("BUILDING_DETAILS");
        String last_known_user_coordinates = getIntent().getStringExtra("COORDINATES");
        String building_map_coordinates = getIntent().getStringExtra("BLDG_COORDINATES");

        //Set up basic info about the building
        building_info = (TextView) findViewById(R.id.bldg_textView);
        String str_building_info_joined_string = "";

        str_building_info_joined_string = building_details[0].toUpperCase() + "\n\n";
        str_building_info_joined_string += "ADDRESS: \n" + building_details[1] + "\n\n";
        //DEBUG:
        STR_BUILDING_COORDS_STREETVIEW = building_details[2];

        building_info.setText(str_building_info_joined_string);


        //Start AsyncTask here
        String url = STR_GOOG_API_BASE_URL + last_known_user_coordinates + STR_GOOG_API_DEST_URL + building_map_coordinates + STR_GOOG_API_MODE + STR_GOOG_API_KEY;
        new TimeEstimateTask().execute(url);

    }

    @Override
    protected void onResume() {
        super.onResume();
        int building_image_resource_id = getIntent().getIntExtra("BUILDING_IMAGE_NAME", R.drawable.bbc);
        //Set up building image
        ImageView buildingImageView = (ImageView) findViewById(R.id.bldg_imageView);
        buildingImageView.setImageResource(building_image_resource_id);
    }

    //Trigger street view, with the current building coordinates
    public void btnStreetViewLauncher(View view) {
        String strStreetView = "google.streetview:cbll=" + STR_BUILDING_COORDS_STREETVIEW;
        Uri gmmIntentUri = Uri.parse(strStreetView);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivityForResult(mapIntent, requestCodeStreetView);
    }

    //If we don't do this, then hitting back from streetview takes you to the map
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == requestCodeStreetView){
            if(requestCode == RESULT_OK){
                //User is back from street view
            }
        }
    }

    //Write out AsyncTask to retrieve location distance and time
    private class TimeEstimateTask extends AsyncTask<String, Integer, String> {

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

        protected void onPostExecute(String resultJsonString) {

            String TimeToTarget = "";
            String DistanceToTarget = "";

            String text = building_info.getText().toString();
            //Parse json object here
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

    }//End of async task


}
