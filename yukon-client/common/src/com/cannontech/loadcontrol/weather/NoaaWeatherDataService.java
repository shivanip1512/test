package com.cannontech.loadcontrol.weather;

import java.util.List;
import java.util.Map;

public interface NoaaWeatherDataService {

    /**
     * Returns a map of stationId to WeatherStation object.
     * Caches results from NOAA web service so subsequent calls will read from memory
     */
    public Map<String, WeatherStation> getAllWeatherStations();

    /**
     * Returns all weather stations, sorted by distance to the coordinate supplied.
     */
    public WeatherStation getClosestWeatherStation(GeographicCoordinate coordinate);

    /**
     * Scans the weather stations to find the closest to the given lat/lon
     * @return - A list of weather stations sorted by distance, closest to furthest
     */
    public List<WeatherStation> getWeatherStationsByDistance(GeographicCoordinate coordinate);

    /**
     * Requests the hourly weather observations from the given NOAA weather station XML feed. 
     * @return - NOTE: Completeness of observations data may vary from reading to reading.
     * @throws NoaaWeatherDataServiceException 
     */
    public WeatherObservation getCurrentWeatherObservation(WeatherStation station) throws NoaaWeatherDataServiceException;
}
