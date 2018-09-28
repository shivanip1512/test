package com.cannontech.common.weather;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.database.data.lite.LitePoint;

public final class WeatherLocation {

    private final PaoIdentifier paoIdentifier;
    private final String name;
    private final String stationId;
    private final GeographicCoordinate geoCoordinate;
    private final LitePoint tempPoint;
    private final LitePoint humidityPoint;
    private final boolean primary;

    public WeatherLocation(PaoIdentifier paoIdentifier, LitePoint tempPoint, LitePoint humidityPoint, String name,
                           String stationId, GeographicCoordinate geoCoordinate, boolean primary) {
        this.paoIdentifier = paoIdentifier;
        this.tempPoint = tempPoint;
        this.humidityPoint = humidityPoint;
        this.name = name;
        this.stationId = stationId;
        this.geoCoordinate = geoCoordinate;
        this.primary = primary;
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

    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }

    public boolean isPrimary() {
        return primary;
    }

}
