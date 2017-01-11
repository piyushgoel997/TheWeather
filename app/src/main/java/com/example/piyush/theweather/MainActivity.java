package com.example.piyush.theweather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

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

        setdata();
    }

    private void setdata() {
        Log.d(TAG, "setdata: called");
        new getJSON().execute(OPEN_WEATHER_MAP_URL);
    }

    public class getJSON extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            Log.d(TAG, "doInBackground: called");
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                Log.d(TAG, "doInBackground: "+params[0]);
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                String line = "";
                String jsonString = "";
                while ((line += reader.readLine()) != null) {
                    jsonString += line + "\n";
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
                String city = jsonObject.getString("name");
                locTV.setText(city);

            } catch (JSONException e) {
                Log.d(TAG, "onPostExecute: JSONException");
            }
        }
    }
}
