package com.cannontech.stars.dr.thirdparty.digi.model;

public class GatewayDto {
    private String serialNumber;
    private String gatewayType;
    private String macAddress;
    private String firmwareVersion;
    private int digiId;
    private int connectionStatusId;
    private int gatewayStatusId;
    
    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getGatewayType() {
        return gatewayType;
    }
    public void setGatewayType(String gatewayType) {
        this.gatewayType = gatewayType;
    }
    
    public String getMacAddress() {
        return macAddress;
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    
    public int getConnectionStatusId() {
        return connectionStatusId;
    }
    public void setConnectionStatusId(int connectionStatusId) {
        this.connectionStatusId = connectionStatusId;
    }
    
    public int getGatewayStatusId() {
        return gatewayStatusId;
    }
    public void setGatewayStatusId(int gatewayStatusId) {
        this.gatewayStatusId = gatewayStatusId;
    }
    
    public void setDigiId(int digiId) {
    	this.digiId = digiId;
    }
    public int getDigiId() {
    	return digiId;
    }
    
    public void setFirmwareVersion(String firmwareVersion) {
    	this.firmwareVersion = firmwareVersion;
    }
    public String getFirmwareVersion() {
    	return firmwareVersion;
    }
}
