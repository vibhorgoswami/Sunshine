package com.govibs.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.govibs.sunshine.bean.WeatherInformation;
import com.govibs.sunshine.parser.WeatherDataParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * Main Fragment for loading in the main activity.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> mForecastAdapter;
    private ArrayList<WeatherInformation> mWeatherInformationArrayList = new ArrayList<>();
    private ListView mListViewForecast;
    private TextView tvTodayTitle, tvTempMax, tvTempMin;
    public ForecastFragment() {
        // Required empty public constructor
    }

    /**
     * Create new instance of Main Fragment
     *
     * @return A new instance of fragment ForecastFragment.
     */
    public static ForecastFragment newInstance() {
        return new ForecastFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mListViewForecast = (ListView) rootView.findViewById(R.id.listview_forecast);
        mListViewForecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), mForecastAdapter.getItem(i), Toast.LENGTH_SHORT).show();
                /*Intent intent = new Intent(getActivity(), ForecastDetailActivity.class);
                intent.putExtra("Details", mWeatherInformationArrayList.get(i));
                startActivityForResult(intent, 1);*/
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container,
                                ForecastDetailActivityFragment.newInstance(mWeatherInformationArrayList.get(i))).addToBackStack("Details").commit();
            }
        });
        tvTempMax = (TextView) rootView.findViewById(R.id.tvTempMax);
        tvTempMin = (TextView) rootView.findViewById(R.id.tvTempMin);
        tvTodayTitle = (TextView) rootView.findViewById(R.id.tvTodayTitle);
        new FetchWeatherTask().execute("94043");
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                new FetchWeatherTask().execute("94043");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class FetchWeatherTask extends AsyncTask<String, Void, ArrayList<WeatherInformation>> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();


        @Override
        protected ArrayList<WeatherInformation> doInBackground(String... args) {
            final String postalCode = args[0];
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                /*URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=" + postalCode
                        + "&mode=json&units=metric&cnt=7&APPID=bd94b677e4673cb6a0fc398cc824325d");*/
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNIT_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String API_PARAM = "APPID";
                Uri uri = Uri.parse(FORECAST_BASE_URL);
                Uri.Builder builder = uri.buildUpon()
                        .appendQueryParameter(QUERY_PARAM, postalCode).appendQueryParameter(FORMAT_PARAM, "json")
                        .appendQueryParameter(UNIT_PARAM, "metric").appendQueryParameter(DAYS_PARAM, "7")
                        .appendQueryParameter(API_PARAM, "bd94b677e4673cb6a0fc398cc824325d");
                 URL url = new URL(builder.build().toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return new ArrayList<>();
                }
                forecastJsonStr = buffer.toString();
                //Log.v(LOG_TAG, "Max = " + WeatherDataParser.getMaxTemperatureForDay(forecastJsonStr, 1));
                return WeatherDataParser.getWeatherDataFromJson(forecastJsonStr, 7);
            } catch (Exception e) {
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return new ArrayList<>();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(ArrayList<WeatherInformation> weatherInformationArrayList) {
            ArrayList<String> forecastStrings = new ArrayList<>();
            mWeatherInformationArrayList = weatherInformationArrayList;
            if (mWeatherInformationArrayList != null && weatherInformationArrayList.size() > 0) {
                for (WeatherInformation weatherInfo : weatherInformationArrayList) {
                    forecastStrings.add(weatherInfo.getForecastString());
                }
                mForecastAdapter = new ArrayAdapter<>(getActivity(), R.layout.layout_list_forecast, forecastStrings);
                mListViewForecast.setAdapter(mForecastAdapter);
                tvTodayTitle.setText(getString(R.string.text_title_today,
                        String.valueOf(weatherInformationArrayList.get(0).getMax()),
                        String.valueOf(weatherInformationArrayList.get(0).getMin())));
                tvTempMax.setText(getString(R.string.text_temp,
                        String.valueOf(weatherInformationArrayList.get(0).getMax())));
                tvTempMin.setText(getString(R.string.text_temp,
                        String.valueOf(weatherInformationArrayList.get(0).getMin())));
            }
        }
    }
}
