package com.csgs.openAQ;

import com.csgs.Coordinates;

public class AQIData {

    private String location;
    private Coordinates coordinates;
    private double pm25;

    public AQIData(String location, Coordinates coordinates, double pm25) {
        this.location = location;
        this.coordinates = coordinates;
        this.pm25 = pm25;
    }

    public double getAQI() {
        double[][] breakpoints = {
            {0.0, 12.0, 0, 50},
            {12.1, 35.4, 51, 100},
            {35.5, 55.4, 101, 150},
            {55.5, 150.4, 151, 200},
            {150.5, 250.4, 201, 300},
            {250.5, 350.4, 301, 400},
            {350.5, 500.4, 401, 500}
        };

        for (double[] bp : breakpoints) {
            if (pm25 >= bp[0] && pm25 <= bp[1]) {
                double aqiLow = bp[2];
                double aqiHigh = bp[3];
                double pm25Low = bp[0];
                double pm25High = bp[1];
                return (int) Math.round(
                        ((aqiHigh - aqiLow) / (pm25High - pm25Low)) * (pm25 - pm25Low) + aqiLow
                );
            }
        }

        return -1; // out of range
    }
    public String getLocation() {
        return location;
    }
    public Coordinates getCoordinates() {
        return coordinates;
    }
    public double getPm25() {
        return pm25;   
    }
    
    @Override
    public String toString() {
        return "AQIData{" +
                "location='" + location + '\'' +
                ", coordinates=" + coordinates.toString() +
                ", pm25=" + pm25 +
                ", AQI=" + getAQI() +
                '}';
    }

}
