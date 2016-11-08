package com.govibs.sunshine.bean;

import java.io.Serializable;

/**
 * Weather information bean.
 * Created by Vibhor on 10/30/16.
 */

public class WeatherInformation implements Serializable {

    private String forecastString;
    private double max, min;

    public String getForecastString() {
        return forecastString;
    }

    public void setForecastString(String forecastString) {
        this.forecastString = forecastString;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }
}
