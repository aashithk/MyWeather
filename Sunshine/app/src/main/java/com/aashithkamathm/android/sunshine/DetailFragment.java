package com.aashithkamathm.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aashithkamathm.android.sunshine.data.WeatherContract;

/**
 * Created by aashith on 9/7/2014.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOCATION_KEY = "location";

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    public static final String DATE_KEY = "forecast_date";

    private ShareActionProvider mShareActionProvider;
    private String mLocation;
    private String mForecast;


     TextView mHumidityView ;
    TextView mpressureView;
    TextView mWindView ;
    TextView mDayView;
    TextView mHighview;
    TextView mLowView;
    TextView mDateView;
    TextView mforecastView ;
    ImageView miconView;

    private static final int DETAIL_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID

    };

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(LOCATION_KEY, mLocation);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocation != null &&
                !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=  inflater.inflate(R.layout.fragment_detail, container, false);
         mHumidityView = (TextView)rootView.findViewById(R.id.detail_humidity);
         mpressureView=(TextView)rootView.findViewById(R.id.detail_pressure);
          mWindView =(TextView) rootView.findViewById(R.id.detail_wind);
         mDayView=(TextView) rootView.findViewById(R.id.detail_day);
         mHighview=(TextView) rootView.findViewById(R.id.detail_high);
         mLowView=(TextView) rootView.findViewById(R.id.detail_low);
         mDateView= (TextView) rootView.findViewById(R.id.detail_date);
         mforecastView = (TextView) rootView.findViewById(R.id.detail_forecast);
         miconView=(ImageView)  rootView.findViewById(R.id.list_item_icon);




        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(LOG_TAG, "in onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.fragment_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mForecast != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null || !intent.hasExtra(DATE_KEY)) {
            return null;
        }
        String forecastDate = intent.getStringExtra(DATE_KEY);

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                mLocation, forecastDate);
        Log.v(LOG_TAG, weatherForLocationUri.toString());

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) { return; }



        String dateString = Utility.getFormattedMonthDay(getActivity().getApplicationContext(),
                data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT)));
        mDateView.setText(dateString);




        String weatherDescription =
                data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
        mforecastView.setText(weatherDescription);

        boolean isMetric = Utility.isMetric(getActivity());

        String high = Utility.formatTemperature(getActivity().getApplicationContext(),
                data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)), isMetric);
        mHighview.setText(high);

        String low = Utility.formatTemperature(getActivity().getApplicationContext(),
                data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)), isMetric);
        mLowView.setText(low);

        String wind = Utility.getFormattedWind(
                getActivity().getApplicationContext(),
                data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED)),
                data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES)));
        mWindView.setText(wind);

        String day = Utility.getDayName(getActivity().getApplicationContext(),
                          data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT)));
        mDayView.setText(day);
        miconView.setImageResource(Utility.getArtResourceForWeatherCondition(data.getInt(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID))));


        String pressure = " Pressure: "+data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE));
        mpressureView.setText(pressure);

        String humidity = " Humidity: "+data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY))+"% HPa";
        mHumidityView.setText(humidity);

        // We still need this for the share intent
        mForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

        Log.v(LOG_TAG, "Forecast String: " + mForecast);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
