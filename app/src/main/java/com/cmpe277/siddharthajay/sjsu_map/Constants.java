package com.cmpe277.siddharthajay.sjsu_map;

import android.Manifest;

/**
 * Created by siddharthgupta on 10/29/16.
 */

public class Constants {
    public static final int CURRENT_LOCATION_SCALE_CORRECTER = 700;
    public static final double STATIC_PIXEL_DISTANCE = 2.1679;
    public static final int INT_XAXIS_PLOT_OFFSET = -20;
    public static final int INT_YAXIS_PLOT_OFFSET = 600;
    public static final String[] LOC_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    public static final int REQ_CODE = 1337;
    public static final int LOC_REQ_CODE = REQ_CODE;
    public static final int LOC_MIN_TIME = 30000;
    public static final int LOC_MIN_DISTANCE = 10;
    public static String strSjsuMapTopLeft = "37.335802,-121.885910";
    public static String strSjsuMapTopRight = "37.338877,-121.879668";
    public static String strSjsuMapBottomLeft = "37.331626,-121.882812";
    public static String strSjsuMapBottomRight = "37.334603,-121.876557";
    public static final int BUILDING_COUNT = 6;
    public static final String[] GEOCOORDINATES = new String[]{
            "37.337359,-121.881909",
            "37.335716,-121.885213",
            "37.333492,-121.883756",
            "37.336361,-121.881282",
            "37.336530,-121.878717",
            "37.333385,-121.880264"
    };
    public static final float[][] coordinates = new float[][]{
            //           TLX  TRY  TRX  BRY
            new float[]{749, 529, 960, 720}, //ENGR BUILDING
            new float[]{193, 493, 312, 690}, //KING LIBRARY
            new float[]{107, 974, 319, 1146}, //YOSHIHIRO HALL
            new float[]{745, 758, 1046, 881}, //STUDENT UNION
            new float[]{1160, 880, 1333, 990}, //bbc
            new float[]{458, 1332, 708, 1504} //SOUTH PARKING
    };

    public static final String[] LOCATIONS = new String[]{
            "Engineering Building",
            "King Library",
            "Yoshihiro Uchida Hall",
            "Student Union",
            "bbc",
            "South Parking Garage"
    };
    public static final String[] ADDRESSES = new String[]{
            "Charles W. Davidson College of Engineering, 1 Washington Square, San Jose, CA 95112",
            "Dr. Martin Luther King, Jr. Library, 150 East San Fernando Street, San Jose, CA 95112",
            "Yoshihiro Uchida Hall, San Jose, CA 95112",
            "Student Union Building, San Jose, CA 95112",
            "Boccardo Business Complex, San Jose, CA 95112",
            "San Jose State University South Garage, 330 South 7th Street, San Jose, CA 95112"
    };
    public static String strHardCodedCurrentLocation = "37.333492,-121.883756";

    public static final String STR_GOOG_API_BASE_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
    public static final String STR_GOOG_API_DEST_URL = "&destinations=";
    public static final String STR_GOOG_API_KEY = "&key=AIzaSyApv0T1SWbV7IKFWFDYl9o8T4VxZgX2fxc";
    public static final String STR_GOOG_API_MODE = "&mode=walking";
    public static String STR_BUILDING_COORDS_STREETVIEW = "46.414382,10.013988";
    public static int requestCodeStreetView = 1333;


    public static final int TOP_LEFT_X = 0;
    public static final int TOP_RIGHT_Y = 1;
    public static final int TOP_RIGHT_X = 2;
    public static final int BOTTOM_RIGHT_Y = 3;

}
