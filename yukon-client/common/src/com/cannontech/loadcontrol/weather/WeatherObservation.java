package com.cannontech.loadcontrol.weather;

import org.joda.time.Instant;

public final class WeatherObservation {

    private final String stationId;
    private final Double temperature;
    private final Double humidity;
    private final Instant timestamp;

    public WeatherObservation(String stationId, Double temperature,
                              Double humidity, Instant timestamp) {
        this.stationId = stationId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.timestamp = timestamp;
    }

    /**
     * Return value will be temperature data in fahrenheit (as received by NOAA)
     * If we received a weather observation with no temperature this will be null.
     *
     * @return temperature (Fahrenheit) or null
     */
    public Double getTemperature() {
        return temperature;
    }

    /**
     * Return value will be relative humidity in percent (as received by NOAA)
     * If we received a weather observation with no humidity this will be null.
     *
     * @return humidity (relative percentage) or null
     */
    public Double getHumidity() {
        return humidity;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getStationId() {
        return stationId;
    }
}
