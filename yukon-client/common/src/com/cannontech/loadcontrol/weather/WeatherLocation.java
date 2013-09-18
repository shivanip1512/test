package com.cannontech.loadcontrol.weather;

import com.cannontech.database.data.lite.LitePoint;

public final class WeatherLocation {

    private final int paoId;
    private final String name;
    private final String stationId;
    private final GeographicCoordinate geoCoordinate;
    private final LitePoint tempPoint;
    private final LitePoint humidityPoint;

    public WeatherLocation(int paoId, LitePoint tempPoint, LitePoint humidityPoint, String name,
                           String stationId, GeographicCoordinate geoCoordinate) {
        this.paoId = paoId;
        this.tempPoint = tempPoint;
        this.humidityPoint = humidityPoint;
        this.name = name;
        this.stationId = stationId;
        this.geoCoordinate = geoCoordinate;
    }

    public String getStationId() {
        return stationId;
    }

    public String getName() {
        return name;
    }

    public GeographicCoordinate getGeoCoordinate() {
        return geoCoordinate;
    }

    public LitePoint getTempPoint() {
        return tempPoint;
    }

    public LitePoint getHumidityPoint() {
        return humidityPoint;
    }

    public int getPaoId() {
        return paoId;
    }
}
