package com.cannontech.common.pao.model;

public class GPS {

    private Double latitude;
    private Double longitude;

    public GPS(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
