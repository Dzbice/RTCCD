package com.csgs;

public class Coordinates {
    private  double latitude;
    private double longitude;
    private double delta;
    public Coordinates(double latitude, double longitude, double delta) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.delta = delta;
    }

    public Coordinates(double latitude, double longitude) {
        this(latitude, longitude, 0.1);
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public double[] boundingBox() {
        double latMin = latitude - delta;
        double latMax = latitude + delta;
        double lonMin = longitude - delta;
        double lonMax = longitude + delta;
        return new double[]{latMin, lonMin, latMax, lonMax};
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }


}
