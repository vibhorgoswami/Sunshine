package com.govibs.sunshine;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.govibs.sunshine.bean.WeatherInformation;

public class ForecastDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_detail);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        WeatherInformation weatherInformation = (WeatherInformation) getIntent().getSerializableExtra("Details");
        if (savedInstanceState == null) {
            ForecastDetailActivityFragment forecastDetailActivityFragment = ForecastDetailActivityFragment.newInstance(weatherInformation);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, forecastDetailActivityFragment)
                    .commit();
        }

    }

}
