package com.cmpe277.siddharthajay.sjsu_map;


public class Building {

    public String building_name;
    public String address;
    public String coordinates;
    public String image_name;
    public int image_resource_name;


    //Building pixel data set
    public static final int TOP_LEFT_X = 0;
    public static final int TOP_RIGHT_Y = 1;
    public static final int TOP_RIGHT_X = 2;
    public static final int BOTTOM_RIGHT_Y = 3;
    public float[] pixel_coordinates;

    Building(String name, float[] coordinates) {

        building_name = name;
        pixel_coordinates = new float[4];
        pixel_coordinates[TOP_LEFT_X] = coordinates[TOP_LEFT_X];
        pixel_coordinates[TOP_RIGHT_Y] = coordinates[TOP_RIGHT_Y];
        pixel_coordinates[TOP_RIGHT_X] = coordinates[TOP_RIGHT_X];
        pixel_coordinates[BOTTOM_RIGHT_Y] = coordinates[BOTTOM_RIGHT_Y];

    }

    //Calculate if within bounds
    public boolean IsWithinPixelBounds(float x, float y) {

        if (x > pixel_coordinates[TOP_LEFT_X] && x < pixel_coordinates[TOP_RIGHT_X]) {
            if (y > pixel_coordinates[TOP_RIGHT_Y] && y < pixel_coordinates[BOTTOM_RIGHT_Y]) {
                return true;
            }
        }

        return false;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float[] getPixel_coordinates() {
        return pixel_coordinates;
    }

    public void setPixel_coordinates(float[] pixel_coordinates) {
        this.pixel_coordinates = pixel_coordinates;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }


    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public int getImage_resource_name() {
        return image_resource_name;
    }

    public void setImage_resource_name(int image_resource_name) {
        this.image_resource_name = image_resource_name;
    }
}
