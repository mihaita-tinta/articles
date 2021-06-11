package com.microsoft.bot.sample.qnamaker.weather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${weather.key}")
    private String key;

    public WeatherService() {
        this.restTemplate = new RestTemplate();
    }

    public CurrentWeatherResponse getWeather(String location, String time) {
        // TODO add time
        String currentUrl
                = "http://api.weatherapi.com/v1/current.json?key=%s&q=%s&aqi=no";
        ResponseEntity<CurrentWeatherResponse> response
                = restTemplate.getForEntity(String.format(currentUrl, key, location), CurrentWeatherResponse.class);

        return response.getBody();
    }
}
