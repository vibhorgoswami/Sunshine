package com.govibs.sunshine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.govibs.sunshine.bean.WeatherInformation;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastDetailActivityFragment extends Fragment {

    private static final String ARG_FORECAST_DETAIL = "forecast_details";

    public static ForecastDetailActivityFragment newInstance(WeatherInformation weatherInformation) {
        ForecastDetailActivityFragment forecastDetailActivityFragment = new ForecastDetailActivityFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_FORECAST_DETAIL, weatherInformation);
        forecastDetailActivityFragment.setArguments(bundle);
        return forecastDetailActivityFragment;
    }

    public ForecastDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        WeatherInformation weatherInformation = (WeatherInformation) getArguments().getSerializable(ARG_FORECAST_DETAIL);
        View view = inflater.inflate(R.layout.fragment_forecast_detail, container, false);
        if (weatherInformation != null) {
            TextView tvForecastDetail = (TextView) view.findViewById(R.id.tvForecastDetail);
            tvForecastDetail.setText(weatherInformation.getForecastString());
        }
        return view;
    }
}
