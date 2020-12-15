package com.bwap.weatherapp.WeatherApp.controller;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WeatherService {
    private OkHttpClient client;
    private Response response;
    //Might have to use id here instead of name
    private String cityName;
    private String unit;
    private String API = "073fb6ff8cb553e58d6fef88ac59efd6";
    private Key key;

    public JSONObject getWeather() throws IOException {
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://api.openweathermap.org/data/2.5/weather?q=" +getCityName()+ "&units=" +getUnit()+ "&appid=" + API)
                .build();

        try {
            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public JSONArray returnWeatherArray() throws IOException, JSONException {
        JSONArray weatherArray = getWeather().getJSONArray("weather");
        return weatherArray;
    }

    public JSONObject returnMain() throws IOException, JSONException {
        JSONObject main = getWeather().getJSONObject("main");
        return main;
    }
    public JSONObject returnWind() throws IOException, JSONException {
        JSONObject wind = getWeather().getJSONObject("wind");
        return wind;
    }
    public JSONObject returnSys() throws IOException, JSONException {
        JSONObject sys = getWeather().getJSONObject("sys");
        return sys;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

}
