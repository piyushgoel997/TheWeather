package com.example.piyush.theweather;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ImageView ivCondition;
    private TextView weatherConditionTV;
    private TextView locTV, latTV, lonTV, currTV, humTV, preTV;

    private Location location;
    private LocationManager locationManager;


    public static String OPEN_WEATHER_MAP_URL = "http://api.openweathermap.org/data/2.5/weather?units=metric&APPID=d97032aed691a5908653a4429329aadd";
    public static final String OPEN_WEATHER_ICON_URL = "http://openweathermap.org/img/w/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivCondition = (ImageView) findViewById(R.id.iv_weather_condition);
        weatherConditionTV = (TextView) findViewById(R.id.tv_weather_condition);
        locTV = (TextView) findViewById(R.id.tv_loc);
        latTV = (TextView) findViewById(R.id.tv_latitude);
        lonTV = (TextView) findViewById(R.id.tv_longitude);
        currTV = (TextView) findViewById(R.id.tv_current_temp);
        humTV = (TextView) findViewById(R.id.tv_humidity);
        preTV = (TextView) findViewById(R.id.tv_pressure);

//        final LocationTask locationTask = new LocationTask(this);
//        Location currLoc = locationTask.getLocation();
//        Log.d(TAG, "onCreate: x -> " + currLoc);
////        while (currLoc == null) {
//        Nammu.askForPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION, new PermissionCallback() {
//            @Override
//            public void permissionGranted() {
//                Log.d(TAG, "permissionGranted: " + locationTask.getLocation());
//            }
//
//            @Override
//            public void permissionRefused() {
//            }
//
//        });
//        currLoc = locationTask.getLocation();
//        Log.d(TAG, "onCreate: -> " + currLoc);
//
////        }
//
//        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Log.d(TAG, "onCreate: lm ->" + lm.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        setData();


    }

    String weatherIconCode;

    private void setData() {
        Log.d(TAG, "setData: called");

        PermissionCallback locCallback = new PermissionCallback() {
            @Override
            public void permissionGranted() {
                Log.d(TAG, "permissionGranted: ");
                int result = setLoc();
                if (result == 1 || result == 2) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    OPEN_WEATHER_MAP_URL = OPEN_WEATHER_MAP_URL + "&lat=" + lat + "&lon=" + lon;
                }

                Log.d(TAG, "permissionGranted: " + location);
            }

            @Override
            public void permissionRefused() {
                Log.d(TAG, "permissionRefused: ");
                Toast.makeText(MainActivity.this, "This app won't work unless you grant this permission", Toast.LENGTH_SHORT).show();
            }
        };

        Nammu.askForPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION,locCallback);


        new getJSON().execute(OPEN_WEATHER_MAP_URL);
        Log.d(TAG, "setData: ");
    }

    private int setLoc() {

        //TODO return public static final int in place of 0 1 2 -1.

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return 0;
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return 1;
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            return 2;
        } else {
            Toast.makeText(this, "Please enable GPS or Network", Toast.LENGTH_SHORT).show();
            return -1;
        }
    }

    public class getJSON extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            Log.d(TAG, "doInBackground: called");
            try {
                Log.d(TAG, "doInBackground: "+params[0]);
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String jsonString = "";
                String line = reader.readLine();
                while (line != null) {
                    jsonString += line + "\n";
                    line = reader.readLine();
                }
                Log.d(TAG, "doInBackground: \n"+ jsonString);
                return new JSONObject(jsonString);

            } catch (java.io.IOException e) {
                Log.d(TAG, "doInBackground: IOException");
            } catch (JSONException e) {
                Log.d(TAG, "doInBackground: JSONException");
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject == null) {
                return;
            }
            try {
                JSONArray weatherArray = jsonObject.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);
                JSONObject main = jsonObject.getJSONObject("main");
                JSONObject coord = jsonObject.getJSONObject("coord");
                weatherIconCode = weather.getString("icon");
                String city = jsonObject.getString("name");
                String weatherDescription = weather.getString("description");
                double tempCurr = main.getDouble("temp");
                double humidity = main.getDouble("humidity");
                double pressure = main.getDouble("pressure");
                double lat = coord.getDouble("lat");
                double lon = coord.getDouble("lon");
                locTV.setText(city);
                latTV.setText("" + lat);
                lonTV.setText("" + lon);
                currTV.setText("" + tempCurr+" C");
                humTV.setText("" + humidity + " %");
                preTV.setText("" + pressure + " hpa");
                weatherConditionTV.setText(weatherDescription);
                Log.d(TAG, "onPostExecute: " + weatherIconCode);



            } catch (JSONException e) {
                Log.d(TAG, "onPostExecute: JSONException");
            }

            new getIcon().execute(OPEN_WEATHER_ICON_URL + weatherIconCode + ".png");

        }
    }

    public class getIcon extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            URL imageURL = null;
            try {
                imageURL = new URL(params[0]);
                Log.d(TAG, "doInBackground: " + imageURL);
                InputStream is = (InputStream) imageURL.getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                return bitmap;
            } catch (java.io.IOException e) {
                Log.e(TAG, "doInBackground: ", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ivCondition.setImageBitmap(bitmap);
        }
    }

}
