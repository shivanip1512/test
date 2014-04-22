package com.cannontech.common.bulk.field.impl;

public class YukonDeviceDto {
    
    // DO NOT ADD AN IDENTIFIER TO THIS CLASS
    
    private Integer address;
    private Boolean enable;
    private String meterNumber;
    private String name;
    private String route; 
    private Integer substation;
    private String deviceType;
    private Integer disconnectAddress;
    private String rfnSerialNumber;
    private String rfnManufacturer;
    private String rfnModel;
    private Double latitude;
    private Double longitude;
    
    // SETTER GETTERS
    public Integer getAddress() {
        return address;
    }
    public void setAddress(Integer address) {
        this.address = address;
    }
    public String getMeterNumber() {
        return meterNumber;
    }
    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getSubstation() {
        return substation;
    }
    public void setSubstation(Integer substation) {
        this.substation = substation;
    }
    public Boolean getEnable() {
        return enable;
    }
    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
    public String getDeviceType() {
        return deviceType;
    }
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    public String getRoute() {
        return route;
    }
    public void setRoute(String route) {
        this.route = route;
    }
    public Integer getDisconnectAddress() {
        return disconnectAddress;
    }
    public void setDisconnectAddress(Integer disconnectAddress) {
        this.disconnectAddress = disconnectAddress;
    }
    public String getRfnSerialNumber() {
        return rfnSerialNumber;
    }
    public void setRfnSerialNumber(String rfnSerialNumber) {
        this.rfnSerialNumber = rfnSerialNumber;
    }
    public String getRfnManufacturer() {
        return rfnManufacturer;
    }
    public void setRfnManufacturer(String rfnManufacturer) {
        this.rfnManufacturer = rfnManufacturer;
    }
    public String getRfnModel() {
        return rfnModel;
    }
    public void setRfnModel(String rfnModel) {
        this.rfnModel = rfnModel;
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
