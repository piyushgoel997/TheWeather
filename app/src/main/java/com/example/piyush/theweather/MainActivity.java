package com.example.piyush.theweather;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ImageView ivCondition;
    private TextView weatherConditionTV;
    private TextView locTV,latTV,longTV,currTV,maxTV,minTV,humTV,preTV, wsTV;

    public static final String OPEN_WEATHER_MAP_URL = "http://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=d97032aed691a5908653a4429329aadd";
    public static final String OPEN_WEATHER_ICON_URL = "http://openweathermap.org/img/w/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivCondition = (ImageView) findViewById(R.id.iv_weather_condition);
        weatherConditionTV = (TextView) findViewById(R.id.tv_weather_condition);
        locTV = (TextView) findViewById(R.id.tv_loc);
        latTV = (TextView) findViewById(R.id.tv_latitude);
        longTV = (TextView) findViewById(R.id.tv_longitude);
        currTV = (TextView) findViewById(R.id.tv_current_temp);
        maxTV = (TextView) findViewById(R.id.tv_max_temp);
        minTV = (TextView) findViewById(R.id.tv_min_temp);
        maxTV = (TextView) findViewById(R.id.tv_max_temp);
        humTV = (TextView) findViewById(R.id.tv_humidity);
        preTV = (TextView) findViewById(R.id.tv_pressure);
        wsTV = (TextView) findViewById(R.id.tv_wind_speed);

        setData();
    }

    String weatherIconCode;

    private void setData() {
        Log.d(TAG, "setData: called");
        new getJSON().execute(OPEN_WEATHER_MAP_URL);
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
                weatherIconCode = weather.getString("icon");
                String city = jsonObject.getString("name");
                String weatherDescription = weather.getString("description");
                double tempCurr = main.getDouble("temp");
                double tempMax = main.getDouble("temp_max");
                double tempMin = main.getDouble("temp_min");
                double humidity = main.getDouble("humidity");
                locTV.setText(city);
                currTV.setText("" + tempCurr);
                maxTV.setText("" + tempMax);
                minTV.setText("" + tempMin);
                humTV.setText("" + humidity);
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
