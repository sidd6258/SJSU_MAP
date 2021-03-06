package com.cmpe277.siddharthajay.sjsu_map;


public class Building {

    public String building_name;
    public String address;
    public String coordinates;
    public int image_resource_name;


    public float[] pixel_coordinates;

    Building(String name, float[] coordinates) {

        building_name = name;
        pixel_coordinates = new float[4];
        pixel_coordinates[Constants.TOP_LEFT_X] = coordinates[Constants.TOP_LEFT_X];
        pixel_coordinates[Constants.TOP_RIGHT_Y] = coordinates[Constants.TOP_RIGHT_Y];
        pixel_coordinates[Constants.TOP_RIGHT_X] = coordinates[Constants.TOP_RIGHT_X];
        pixel_coordinates[Constants.BOTTOM_RIGHT_Y] = coordinates[Constants.BOTTOM_RIGHT_Y];

    }

    //function to find out if in bounds
    public boolean IsWithinBounds(float x, float y) {

        if (x > pixel_coordinates[Constants.TOP_LEFT_X] && x < pixel_coordinates[Constants.TOP_RIGHT_X]) {
            if (y > pixel_coordinates[Constants.TOP_RIGHT_Y] && y < pixel_coordinates[Constants.BOTTOM_RIGHT_Y]) {
                return true;
            }
        }

        return false;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float[] getPixel_coordinates() {
        return pixel_coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public int getImage_resource_name() {
        return image_resource_name;
    }

    public void setImage_resource_name(int image_resource_name) {
        this.image_resource_name = image_resource_name;
    }
}
