package com.microsoft.bot.sample.qnamaker.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrentWeatherResponse {
    private WeatherLocation location;
    private WeatherInfo current;

    public WeatherLocation getLocation() {
        return location;
    }

    public void setLocation(WeatherLocation location) {
        this.location = location;
    }

    public WeatherInfo getCurrent() {
        return current;
    }

    public void setCurrent(WeatherInfo current) {
        this.current = current;
    }

    public static class WeatherInfo {
        @JsonProperty("temp_c")
        private float temperature;

        public float getTemperature() {
            return temperature;
        }

        public void setTemperature(float temperature) {
            this.temperature = temperature;
        }
    }
}
