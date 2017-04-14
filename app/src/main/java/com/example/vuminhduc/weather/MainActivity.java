package com.example.vuminhduc.weather;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity implements LocationListener{

    private TextView tv_location, tv_time, tv_weather, tv_doAm,
                    tv_sunset, tv_sunrise, tv_celMax, tv_celMin ;
    private ImageView img ;
    static double lat, lng ;

    LocationManager locationManager ;
    private String provider;
    private int MY_PERMISSION = 0;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewByIds();
        initsComponents() ;
        setEvents();
        getCoordinates();
    }

    private void setEvents() {

    }

    private void initsComponents() {

    }

    private void findViewByIds() {

        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_weather = (TextView) findViewById(R.id.weather);
        tv_doAm = (TextView) findViewById(R.id.doAm);
        tv_sunset = (TextView) findViewById(R.id.sunset);
        tv_sunrise = (TextView) findViewById(R.id.sunrise);
        tv_celMax = (TextView) findViewById(R.id.celMax);
        tv_celMin = (TextView) findViewById(R.id.celMin);
        img = (ImageView) findViewById(R.id.img_image);

    }

    public void getCoordinates( ) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false) ;

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE

               }, MY_PERMISSION);

        }

        Location location = locationManager.getLastKnownLocation(provider) ;


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE


            }, MY_PERMISSION);
        }
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE


            }, MY_PERMISSION);
        }
        locationManager.requestLocationUpdates(provider, 400, 1,this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        new Weather().execute(Common.WeatherLink(String.valueOf(lat),String.valueOf(lng)));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private class Weather extends AsyncTask<String, Void, String>  {

        ProgressDialog pr = new ProgressDialog(MainActivity.this) ;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pr.setTitle("Please wait...");
            pr.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.contains("Error: Not found the city")){
                pr.dismiss();
                return;
            }

            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>() {}.getType();
            openWeatherMap = gson.fromJson(s, mType) ;
            pr.dismiss();

            tv_location.setText(String.format("%s, %s", openWeatherMap.getName(),
                    openWeatherMap.getSys().getCountry()));

            tv_time.setText(String.format("Last updated: %s", Common.getDate()));
            tv_weather.setText(String.format("%s",openWeatherMap.getWeather().get(0).getDescription()));
            tv_doAm.setText("Độ ẩm: " + String.format("%d%%", openWeatherMap.getMain().getHumidity()));
            tv_sunrise.setText("Sun Rise: " + String.format("%s", Common.convertTime(openWeatherMap.getSys().
                    getSunrise())));
            tv_sunset.setText("Sun Set: " + String.format("%s", Common.convertTime(openWeatherMap.getSys()
                    .getSunset())));
            tv_celMax.setText(String.format("Nhiệt độ cao nhất: "+ String.format("%.2f °C",
                    openWeatherMap.getMain().getTemp_max() - 273.15)));
            tv_celMin.setText(String.format("Nhiệt độ thấp nhất: "+ String.format("%.2f °C",
                    openWeatherMap.getMain().getTemp_min() - 273.15)));
            Picasso.with(MainActivity.this).load(Common.
                    getImage(openWeatherMap.getWeather().get(0).getIcon()))
                    .into(img);


        }

        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String url = params[0] ;
            httpConnection httpConnection = new httpConnection();
            stream = httpConnection.getURLString(url) ;
            return stream;
        }
    }

}
