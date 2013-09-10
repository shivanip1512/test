package com.cannontech.loadcontrol.weather;

import org.joda.time.Duration;
import org.joda.time.Instant;

public final class WeatherObservation {

    private final String stationId;
    private final Double temperature;
    private final Instant temperatureTimestamp;
    private final Double humidity;
    private final Instant humidityTimestamp;

    public WeatherObservation(String stationId,
                              Double temperature, Instant temperatureTimestamp,
                              Double humidity, Instant humidityTimestamp) {
        this.stationId = stationId;
        this.temperature = temperature;
        this.temperatureTimestamp = temperatureTimestamp;
        this.humidity = humidity;
        this.humidityTimestamp = humidityTimestamp;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Instant getTemperatureTimestamp() {
        return temperatureTimestamp;
    }

    public Double getHumidity() {
        return humidity;
    }

    public Instant getHumidityTimestamp() {
        return humidityTimestamp;
    }

    public String getStationId() {
        return stationId;
    }

    public boolean isTempCurrent() {
        // Is data older than an hour?
        return temperatureTimestamp.isAfter(Instant.now().minus(Duration.standardHours(1)));
    }

    public boolean isHumidityCurrent() {
        // Is data older than an hour?
        return humidityTimestamp.isAfter(Instant.now().minus(Duration.standardHours(1)));
    }
}
