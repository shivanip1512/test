package com.cannontech.common.weather;

public final class WeatherStation {

    private final String stationId;
    private final String stationDesc;
    private final String noaaUrl;
    private final GeographicCoordinate geoCoordinate;

    public WeatherStation(String stationId, String stationDesc,
                          GeographicCoordinate geoCoordinate, String noaaUrl) {
        this.stationId = stationId;
        this.stationDesc = stationDesc;
        this.noaaUrl = noaaUrl;
        this.geoCoordinate = geoCoordinate;
    }

    public String getStationId() {
        return this.stationId;
    }

    public String getStationDesc() {
        return this.stationDesc;
    }

    public String getNoaaUrl() {
        return this.noaaUrl;
    }

    public GeographicCoordinate getGeoCoordinate() {
        return geoCoordinate;
    }
}
