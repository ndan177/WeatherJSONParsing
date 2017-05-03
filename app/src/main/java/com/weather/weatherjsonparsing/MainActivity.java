package com.weather.weatherjsonparsing;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;

    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);

        new GetContacts().execute();
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://api.openweathermap.org/data/2.5/forecast/daily?q=bucharest&appid=93635fe72bd1c0937079a9a1653fa2db";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                /* Lista cu main(tempMin,tempMax,pressure,humidity)
                    weather(id,main,description,icon)
                    wind(speed)
                    rain
                    dt_txt--data
                 */

                    JSONObject city = jsonObj.getJSONObject("city");
                    String name = city.getString("name");
                    String country = city.getString("country");

                    JSONArray list = jsonObj.getJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject objList = list.getJSONObject(i);
                        JSONObject temp = objList.getJSONObject("temp");

                        int day = (int) (temp.getDouble("day") - 273.16);
                        String dayTemperature = String.valueOf(day);

                        int min = (int) ( temp.getDouble("min") - 273.16);
                        String minTemperature = String.valueOf(min);

                        int max = (int) ( temp.getDouble("max") - 273.16);
                        String maxTemperature = String.valueOf(max);

                        int night = (int) (temp.getDouble("night") - 273.16);
                        String nightTemperature = String.valueOf(night);

                        int eve = (int) (temp.getDouble("eve") - 273.16);
                        String eveTemperature = String.valueOf(eve);

                        int morn = (int) ( temp.getDouble("morn") - 273.16);
                        String mornTemperature = String.valueOf(morn);

                        String pressure = String.valueOf(objList.getDouble("pressure"));
                        String humidity = String.valueOf(objList.getDouble("humidity"));

                        JSONArray weather = objList.getJSONArray("weather");
                        JSONObject weatherInfo = weather.getJSONObject(0);
                        String mainWeather = weatherInfo.getString("main");
                        String description = weatherInfo.getString("description");
                        String icon = weatherInfo.getString("icon");

                        String speed = String.valueOf(objList.getDouble("speed"));

                        HashMap<String, String> contact = new HashMap<>();
                        contact.put("name", name);
                        contact.put("dayTemp", dayTemperature);
                        contact.put("minTemp", minTemperature);
                        contact.put("maxTemp", maxTemperature);
                        contact.put("pressure", pressure);
                        contact.put("humidity", humidity);
                        contact.put("main", mainWeather);
                        contact.put("description", description);
                        contact.put("iconName", icon);
                        contact.put("windSpeed", speed);

                        // adding contact to contact list
                        contactList.add(contact);
                    }


                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } else

            {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter =
                    new SimpleAdapter(MainActivity.this, contactList, R.layout.list_daily_item,
                            new String[]{"name", "dayTemp", "maxTemp", "pressure", "humidity", "main", "description", "iconName", "windSpeed"},
                            new int[]{R.id.name, R.id.dayTemp, R.id.maxTemp, R.id.pressure, R.id.humidity, R.id.main, R.id.description, R.id.icon, R.id.windSpeed});
            lv.setAdapter(adapter);
        }
    }
}
