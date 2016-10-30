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

}
