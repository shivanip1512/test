package com.cannontech.loadcontrol.weather;

import java.util.List;

public interface WeatherDataService {

    List<WeatherLocation> getAllWeatherLocations();

    WeatherLocation getWeatherLocationForStationId(String weatherStationId);

    void createWeatherLocation(WeatherLocation weatherLocation);

    void deleteWeatherLocation(int paoId);

    boolean isNameAvailableForWeatherLocation(String name);

    WeatherObservation getCurrentWeatherObservation(String weatherStationId);
}
