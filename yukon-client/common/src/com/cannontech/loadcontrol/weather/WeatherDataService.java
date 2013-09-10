package com.cannontech.loadcontrol.weather;

import java.util.List;

public interface WeatherDataService {

    List<WeatherLocation> getAllWeatherLocations();

    void createWeatherLocation(WeatherLocation weatherLocation);

    void deleteWeatherLocation(int paoId);

    boolean isNameAvailableForWeatherLocation(String name);

    WeatherObservation getCurrentWeatherObservation(WeatherLocation weatherLocation);
}
