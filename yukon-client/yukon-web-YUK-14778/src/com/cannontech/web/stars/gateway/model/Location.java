package com.cannontech.web.stars.gateway.model;

public class Location {
    
    private int paoId;
    private Double latitude;
    private Double longitude;
    
    public int getPaoId() {
        return paoId;
    }
    
    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
}