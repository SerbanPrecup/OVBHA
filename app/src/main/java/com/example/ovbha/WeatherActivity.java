package com.example.ovbha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.Manifest;

public class WeatherActivity extends AppCompatActivity {

    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNametxt, temperatureTxt, conditionTxt;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView backIV, iconIV, searchIV;

    private ArrayList<WeatherTVModal> weatherTVModalArrayList;
    private WeatherRVAdapter weatherRVAdapter;

    private LocationManager locationManager;

    private int PERMISSION_CODE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        setContentView(R.layout.activity_weather);

        String cityName;

        homeRL = findViewById(R.id.RLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        cityNametxt = findViewById(R.id.txtCityName);
        temperatureTxt = findViewById(R.id.TVTemperature);
        conditionTxt = findViewById(R.id.TVCondition);
        weatherRV = findViewById(R.id.RvWeather);
        cityEdt = findViewById(R.id.EdtCity);
        iconIV = findViewById(R.id.IVicon);
        backIV = findViewById(R.id.IVBack);
        searchIV = findViewById(R.id.TvSearch);
        weatherTVModalArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherTVModalArrayList);
        weatherRV.setAdapter(weatherRVAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(WeatherActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            cityName = getCityName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(cityName);
        } else {
            cityName = "London";
            getWeatherInfo(cityName);
        }

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEdt.getText().toString();
                if (city.isEmpty()) {
                    Toast.makeText(WeatherActivity.this, "Please enter a city Name", Toast.LENGTH_SHORT).show();

                } else {
                    cityNametxt.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude) {
        String cityName2 = "Not found";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude,1);
            if (addresses != null) {
                Address address = addresses.get(0);
                String addressString = address.getAddressLine(0);
                String city = address.getLocality();
                if (city != null && !city.equals("")) {
                    cityName2 = city;
                } else {
                    Log.d("TAG", "CITY NOT FOUND");
                    Toast.makeText(this, "User City not found", Toast.LENGTH_SHORT).show();
                }
                Log.d("City2",cityName2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName2;
    }

    private void getWeatherInfo(String cityName) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=dc2a52e2b6324296824122858242503&q="+cityName+"&days=1&aqi=yes&alerts=yes";
        System.out.println(url);

        Log.d("City",cityName);
        cityNametxt.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        loadingPB.setVisibility(View.GONE);
                        homeRL.setVisibility(View.VISIBLE);
                        weatherTVModalArrayList.clear();

                        try {
                            String temperature = response.getJSONObject("current").getString("temp_c");
                            temperatureTxt.setText(temperature + "°C");
                            int isDay = response.getJSONObject("current").getInt("is_day");
                            String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                            String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                            Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                            conditionTxt.setText(condition);
                            if (isDay == 1) {
                                //morning
                                Picasso.get().load("https://i.pinimg.com/736x/35/d3/48/35d348602fdc62d3cefcb93be0235d5d.jpg").into(backIV);
                            } else {
                                //night
                                Picasso.get().load("https://images.rawpixel.com/image_800/cHJpdmF0ZS9sci9pbWFnZXMvd2Vic2l0ZS8yMDIzLTExL3Jhd3BpeGVsb2ZmaWNlMTBfYV9mYW50YXN5X21vb25fYmFja2dyb3VuZF9oaW50X29mX3ByZWNpc2lvbmlzbV84ZGUyMmZmMC0zNGE1LTQ0N2EtOWVmNS05N2NiYmRjNmJjMGVfMS5qcGc.jpg").into(backIV);
                            }
                            JSONObject forecastObj = response.getJSONObject("forecast");
                            JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                            JSONArray hourArray = forecast0.getJSONArray("hour");

                            for (int i = 0; i < hourArray.length(); i++) {
                                JSONObject hourObj = hourArray.getJSONObject(i);
                                String time = hourObj.getString("time");
                                String temper = hourObj.getString("temp_c");
                                String img = hourObj.getJSONObject("condition").getString("icon");
                                String wind = hourObj.getString("wind_kph");
                                weatherTVModalArrayList.add(new WeatherTVModal(time, temper, img, wind));
                                Log.d("String",time+temper+img+wind);
                            }
                            weatherRVAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", "Error occurred: " + error.getMessage());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}