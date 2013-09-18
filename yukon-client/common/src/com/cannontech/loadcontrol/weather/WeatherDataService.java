package com.cannontech.loadcontrol.weather;

import java.util.List;

public interface WeatherDataService {

    List<WeatherLocation> getAllWeatherLocations();

    /**
     * Saves this weatherLocation to the database.
     * 
     * This includes lat/lon and stationId information in StaticPaoInfo.
     */
    void createWeatherLocation(WeatherLocation weatherLocation);

    void deleteWeatherLocation(int paoId);

    boolean isNameAvailableForWeatherLocation(String name);

    /**
     * Returns the most current weather observation for this weather location.
     * 
     * This requests data from dispatch. If the point value is uninitialized (noaa may be missing this data),
     * the value will be returned null. It is possible both temperature & humidity will be null.
     */
    WeatherObservation getCurrentWeatherObservation(WeatherLocation weatherLocation);

    /**
     * Returns a WeatherLocation object if paoId is a valid WeatherLocation pao
     * 
     * Returns null if this paoId doesn't represent a WeatherLocation
     */
    WeatherLocation getWeatherLocationForPao(int paoId);
}
