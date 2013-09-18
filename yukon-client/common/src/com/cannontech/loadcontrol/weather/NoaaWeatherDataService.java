package com.cannontech.loadcontrol.weather;

import java.util.List;
import java.util.Map;


public interface NoaaWeatherDataService {

    /**
     * Returns a map of stationId to WeatherStation object.
     * Caches results from NOAA web service so subsequent calls will read from memory
     */
    Map<String, WeatherStation> getAllWeatherStations();

    /**
     * Scans the weather stations to find the closest to the given lat/lon
     * @return - A list of weather stations sorted by distance, closest to farthest
     */
    List<WeatherStation> getWeatherStationsByDistance(GeographicCoordinate coordinate);

    /**
     * Requests the hourly weather observations from the given NOAA weather station XML feed. 
     * @return - NOTE: Completeness of observations data may vary from reading to reading.
     * @throws NoaaWeatherDataServiceException 
     */
    WeatherObservation getCurrentWeatherObservation(WeatherStation station) throws NoaaWeatherDataServiceException;
}
