package com.cmpe277.siddharthajay.sjsu_map;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;

import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MapActivity extends AppCompatActivity {
    
    public static final String LocationProvider = LocationManager.NETWORK_PROVIDER;

    public static CircleMarkerView circlemarker;
    public static AutoCompleteTextView sjsumap_search_bar;
    public static MarkerView marker;
    public static Matrix mapMatrix = new Matrix();
    public static ImageView sjsumapImageView;
    /*public final static double OneEightyDeg = 180.0d;
    public static double ImageSizeW = 1407, ImageSizeH = 1486.0;*/
    public static Location locCurrLocation;
    public static Location locCurrHardCodedLocation;
    public static String strCurrUserLatitude = "";
    public static String strCurrUserLongitude = "";
    public static String strCurrUserLoc = "";
    public static Location locSjsuMapTopLeft, locSjsuMapTopRight, locSjsuMapBottomLeft, locSjsuMapBottomRight;
    private static LocationManager mLocationManager;
    private static final int[] BUILDING_RESOURCE_NAMES = new int[]{
            R.drawable.engineering_building,
            R.drawable.king,
            R.drawable.yoshihiro,
            R.drawable.student_union,
            R.drawable.bbc,
            R.drawable.south_garage
    };
    public Building[] map_buildings = new Building[Constants.BUILDING_COUNT];




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Intializing
        marker = new MarkerView(this);

        //Get lat long
        locSjsuMapTopLeft = GetLocationFromStrings(Constants.strSjsuMapTopLeft);
        locSjsuMapTopRight = GetLocationFromStrings(Constants.strSjsuMapTopRight);
        locSjsuMapBottomLeft = GetLocationFromStrings(Constants.strSjsuMapBottomLeft);
        locSjsuMapBottomRight = GetLocationFromStrings(Constants.strSjsuMapBottomRight);
        locCurrHardCodedLocation = ConvertStringToLatLng(Constants.strHardCodedCurrentLocation);


        for (int i = 0; i < Constants.BUILDING_COUNT; i++) {
            map_buildings[i] = new Building(Constants.LOCATIONS[i], Constants.coordinates[i]);
            map_buildings[i].setAddress(Constants.ADDRESSES[i]);
            map_buildings[i].setCoordinates(Constants.GEOCOORDINATES[i]);
            map_buildings[i].setImage_resource_name(BUILDING_RESOURCE_NAMES[i]);
        }

        //image view
        sjsumapImageView = (ImageView) findViewById(R.id.sjsumapImageView);

        //search bar
        sjsumap_search_bar = (AutoCompleteTextView) findViewById(R.id.sjsumap_search_bar);

        //map toolbar
        Toolbar map_toolbar = (Toolbar) findViewById(R.id.sjsumap_toolbar);
        setSupportActionBar(map_toolbar);

        //status bar
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorGreen));

        //autocomplete
        ArrayAdapter<String> searchArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, Constants.LOCATIONS);
        sjsumap_search_bar.setAdapter(searchArrayAdapter);
        sjsumap_search_bar.setThreshold(0);

        //touch
        sjsumapImageView.setOnTouchListener(map_touch_listener);

        //center the map
        allignImage();
        System.out.println("in");
        //Test location code
        getUserLocation(this);



    }

    public void updateCurrentUserLocationOnMap() {

        ExecutePlotCurrentUserOnMap(locSjsuMapTopLeft, locSjsuMapTopRight, locSjsuMapBottomLeft, locSjsuMapBottomRight, locCurrLocation);

    }


    //Get text from AutoComplete
    //Get coordinates
    public void btnSearchHandler(View v) {

        AutoCompleteTextView auto_text = (AutoCompleteTextView) findViewById(R.id.sjsumap_search_bar);
        String search_text = auto_text.getText().toString();

        if (search_text.isEmpty() == true || search_text == "" || search_text == null) {
            return;
        }

        int building_count = -1;
        search_text = search_text.toLowerCase();
        for (int i = 0; i < Constants.LOCATIONS.length; i++) {
            if (search_text.equals(Constants.LOCATIONS[i].toLowerCase())) {
                building_count = i;
                break;
            }
        }

        if (building_count == -1) {
            return;
        }

        if (marker != null) {
            RelativeLayout map_layout = (RelativeLayout) findViewById(R.id.activity_map);
            map_layout.removeView(marker);
        }

        float[] building_pixel_coordinates = map_buildings[building_count].getPixel_coordinates();

        float pixelTopRightX = building_pixel_coordinates[2];
        float pixelTopRightY = building_pixel_coordinates[1];
        float pixelBottomRightY = building_pixel_coordinates[3];
        float pixelTopLeftX = building_pixel_coordinates[0];

        float plotPixelX = (pixelTopLeftX + pixelTopRightX) / 2;
        float plotPixelY = (pixelTopRightY + pixelBottomRightY) / 2;

        //Plot
        Plot(this, plotPixelX, plotPixelY);

    }

    @Override
    protected void onResume() {
        super.onResume();

        final AutoCompleteTextView auto_text_view = (AutoCompleteTextView) findViewById(R.id.sjsumap_search_bar);
        auto_text_view.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {



                if (auto_text_view.getText().toString().equals("") || auto_text_view.getText().toString() == null) {
                    if (marker != null) {
                        RelativeLayout map = (RelativeLayout) findViewById(R.id.activity_map);
                        map.removeView(marker);
                    }
                }
            }
        });
    }

    private void ProcessTouch(View v, float x, float y) {

        for (int i = 0; i < Constants.BUILDING_COUNT; i++) {
            if (map_buildings[i].IsWithinBounds(x, y)) {

                Intent bldgIntent = new Intent(this, BuildingActivity.class);
                bldgIntent.putExtra("BUILDING_DETAILS", new String[]{
                        map_buildings[i].building_name,
                        map_buildings[i].address,
                        map_buildings[i].coordinates
                });

                bldgIntent.putExtra("BUILDING_NAME", map_buildings[i].getImage_resource_name());
                System.out.println("loc:"+strCurrUserLoc);
                bldgIntent.putExtra("COORDINATES", strCurrUserLoc);
                bldgIntent.putExtra("BLDG_COORDINATES", map_buildings[i].coordinates);
                startActivity(bldgIntent);


            }

        }

    }

    private Location GetLocationFromStrings(String strMap) {
        Location loc = ConvertStringToLatLng(strMap);
        return loc;
    }

    public Location ConvertStringToLatLng(String strCoord) {
        String[] ltlg = strCoord.split(",");
        double lat = Double.parseDouble(ltlg[0]);
        double lon = Double.parseDouble(ltlg[1]);


        Location location = new Location("dummyprovider");
        location.setLatitude(lat);
        location.setLongitude(lon);

        return location;
    }

    //function to plot
    private void Plot(Context context, float x, float y) {
        //Normalize
        x = x + Constants.INT_XAXIS_PLOT_OFFSET;
        y = y + Constants.INT_YAXIS_PLOT_OFFSET;

        RelativeLayout map = (RelativeLayout) findViewById(R.id.activity_map);
        marker = new MarkerView(context);
        marker.set_x_y_coord(x, y);
        map.addView(marker);
    }



    private void ExecutePlotCurrentUserOnMap(Location locMapTopLeft, Location locMapTopRight,
                                             Location locMapBottomLeft, Location locMapBottomRight,
                                             Location locCurrentUserLocation) {

        //calculate deviation of earth to get correct map location
        double angle = 10.0;
        double r = Math.toRadians(angle);

        double bearingTLtoTR = locMapTopLeft.bearingTo(locMapTopRight);


        double bearingTLtoCL = locMapTopLeft.bearingTo(locCurrentUserLocation);
        double diffAngle = (bearingTLtoCL - bearingTLtoTR);

        double hypot_current_loc = locMapTopLeft.distanceTo(locCurrentUserLocation);
        double current_x_dist = hypot_current_loc * Math.cos(Math.toRadians(diffAngle));
        double current_y_dist = hypot_current_loc * Math.sin(Math.toRadians(diffAngle));


        double current_x_pixels = Constants.STATIC_PIXEL_DISTANCE * current_x_dist;
        double current_y_pixels = Constants.STATIC_PIXEL_DISTANCE * current_y_dist;

        //Get the old points
        float current_X = (float) current_x_pixels;
        float current_Y = (float) current_y_pixels;


        float center_X = 13;
        float center_Y = 445;

        float new_X = (float) (center_X + ((current_X - center_X) * Math.cos(r) + (current_Y - center_Y) * Math.sin(r)));
        float new_Y = (float) (center_Y + ((current_X - center_X) * -1 * (Math.sin(r)) + (current_Y - center_Y) * Math.cos(r)));

        PlotCircle(this, new_X, new_Y);

    }

    private void PlotCircle(Context context, float x, float y) {
        //Normalize
        x = x + Constants.INT_XAXIS_PLOT_OFFSET;
        y = y + Constants.INT_YAXIS_PLOT_OFFSET + Constants.CURRENT_LOCATION_SCALE_CORRECTER;

        RelativeLayout map = (RelativeLayout) findViewById(R.id.activity_map);

        if (circlemarker != null) {
            map.removeView(circlemarker);
        }

        circlemarker = new CircleMarkerView(context);
        circlemarker.set_x_y_coord(x, y);
        map.addView(circlemarker);
    }


    private void allignImage() {
        //image dimensions
        Drawable map = sjsumapImageView.getDrawable();
        float imageWidth = map.getIntrinsicWidth();
        float imageHeight = map.getIntrinsicHeight();

        //Screen dimensions
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        //center the image
        RectF mapDrawableRect = new RectF(0, 0, imageWidth, imageHeight);
        RectF viewImageRect = new RectF(0, 0, screenWidth, screenHeight);
        mapMatrix.setRectToRect(mapDrawableRect, viewImageRect, Matrix.ScaleToFit.CENTER);
        sjsumapImageView.setImageMatrix(mapMatrix);


    }

    private class MarkerView extends View {
        private float x_coor = 1000, y_coor = 1000;

        public MarkerView(Context context) {
            super(context);
        }

        public void set_x_y_coord(float x, float y) {
            x_coor = x;
            y_coor = y;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Bitmap marker = BitmapFactory.decodeResource(getResources(), R.drawable.pin);
            canvas.drawBitmap(marker, x_coor, y_coor, null);
        }
    }

    private class CircleMarkerView extends View {
        private float x_coor = 1000, y_coor = 1000;

        public CircleMarkerView(Context context) {
            super(context);
        }

        public void set_x_y_coord(float x, float y) {
            x_coor = x;
            y_coor = y;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Bitmap marker = BitmapFactory.decodeResource(getResources(), R.drawable.current_small);
            canvas.drawBitmap(marker, x_coor, y_coor, null);
        }
    }


    // current location
    @TargetApi(Build.VERSION_CODES.M)
    public void getUserLocation(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        System.out.println("in");
        Location loc;
        boolean isEnabled = mLocationManager.isProviderEnabled(LocationProvider);

        if (isEnabled) {
            System.out.println("in1");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                System.out.println("in");
                return;
            }
            loc = mLocationManager.getLastKnownLocation(LocationProvider);
            System.out.println("in"+loc);
            mLocationManager.requestLocationUpdates(LocationProvider, Constants.LOC_MIN_TIME, Constants.LOC_MIN_DISTANCE, mLocationListener);

            if (loc != null) {
                System.out.println("in");
                strCurrUserLatitude = Location.convert(loc.getLatitude(), Location.FORMAT_DEGREES);
                System.out.println("in +"+strCurrUserLatitude);
                strCurrUserLongitude = Location.convert(loc.getLongitude(), Location.FORMAT_DEGREES);
                strCurrUserLoc = strCurrUserLatitude + "," + strCurrUserLongitude;
                locCurrLocation = loc;

                updateCurrentUserLocationOnMap(); //when app starts up
                return;
            }
        }

        requestPermissions(Constants.LOC_PERMS, Constants.LOC_REQ_CODE);
    }

   //handle user location
    public final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

            strCurrUserLatitude = Location.convert(location.getLatitude(), Location.FORMAT_DEGREES);
            strCurrUserLongitude = Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);
            strCurrUserLoc = strCurrUserLatitude + "," + strCurrUserLongitude;


            //Update user location
            strCurrUserLoc = strCurrUserLatitude + "," + strCurrUserLongitude;
            locCurrLocation = location;

            //plot user continuously on the map
            updateCurrentUserLocationOnMap();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    //handle after getting permissions:
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.LOC_REQ_CODE) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationManager.requestLocationUpdates(LocationProvider, Constants.LOC_MIN_TIME, Constants.LOC_MIN_DISTANCE, mLocationListener);

            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private View.OnTouchListener map_touch_listener =
            new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        float screenX = event.getX();
                        float screenY = event.getY();
                        float viewX = screenX - v.getLeft();
                        float viewY = screenY - v.getTop();



                        ProcessTouch(v, viewX, viewY);
                        return true;
                    }
                    return false;
                }
            };


}
