package com.cannontech.common.pao.model;

import com.cannontech.common.rfn.message.location.Origin;

public class PaoLocationDetails {
    private String paoName;
    private String meterNumber;
    private String latitude;
    private String longitude;
    private Origin origin;
    private String lastChangedDate;
    
    public PaoLocationDetails(String paoName, String meterNumber, String latitude, String longitude, Origin origin,
            String lastChangedDate) {
        this.paoName = paoName;
        this.meterNumber = meterNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.origin = origin;
        this.lastChangedDate = lastChangedDate;
    }
    
    public String getPaoName() {
        return paoName;
    }
    public void setPaoName(String paoName) {
        this.paoName = paoName;
    }
    public String getMeterNumber() {
        return meterNumber;
    }
    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public Origin getOrigin() {
        return origin;
    }
    public void setOrigin(Origin origin) {
        this.origin = origin;
    }
    public String getLastChangedDate() {
        return lastChangedDate;
    }
    public void setLastChangedDate(String lastChangedDate) {
        this.lastChangedDate = lastChangedDate;
    }

}
