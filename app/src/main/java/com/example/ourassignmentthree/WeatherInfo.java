package com.example.ourassignmentthree;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONObject;

public class WeatherInfo {
    private static final String weather_API_KEY = "2d6d25ab6612f49333551ae60271d591";
    private static final String API_BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    public JSONObject getWeatherData(String city){
        OkHttpClient client = new OkHttpClient();
        String url = API_BASE_URL + "?q=" + city + "&appid=" + weather_API_KEY;
        Request request = new Request.Builder().url(url).build();

        try{
            Response response = client.newCall(request).execute();
            String jsonData = response.body().string();
            return new JSONObject(jsonData);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
