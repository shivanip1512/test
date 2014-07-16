package com.cannontech.web.stars.gateway.model;

import org.joda.time.Instant;

//just a mockup model object
public class Gateway {
    
    private int id = 42;
    private String name;
    private String serialNumber;
    private String ipaddress;
    private String model;
    private String firmware;
    private LastComm lastComm;
    private Instant lastCommTimestamp;
    private double dataCollection;
    private ConnectStatus connectStatus;
    
    public Gateway(String name, String serialNumber, String ipaddress, String model, String firmware, LastComm lastComm, 
            double dataCollection, ConnectStatus connectStatus) {
        this.name = name;
        this.serialNumber = serialNumber;
        this.ipaddress = ipaddress;
        this.model = model;
        this.firmware = firmware;
        this.lastComm = lastComm;
        this.dataCollection = dataCollection;
        this.connectStatus = connectStatus;
        
        lastCommTimestamp = Instant.now();
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getIpaddress() {
        return ipaddress;
    }
    
    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getFirmware() {
        return firmware;
    }
    
    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }
    
    public LastComm getLastComm() {
        return lastComm;
    }
    
    public void setLastComm(LastComm lastComm) {
        this.lastComm = lastComm;
    }
    
    public Instant getLastCommTimestamp() {
        return lastCommTimestamp;
    }
    
    public void setLastCommTimestamp(Instant lastCommTimestamp) {
        this.lastCommTimestamp = lastCommTimestamp;
    }
    
    public double getDataCollection() {
        return dataCollection;
    }
    
    public void setDataCollection(double dataCollection) {
        this.dataCollection = dataCollection;
    }
    
    public boolean isDataWarning() {
        return dataCollection >= 70.0 && dataCollection < 90.0;
    }
    
    public boolean isDataError() {
        return dataCollection < 70.0;
    }
    
    public boolean isCommFailed() {
        return lastComm == LastComm.FAILURE;
    }
    
    public boolean isCommMissed() {
        return lastComm == LastComm.MISSED;
    }
    
    public boolean isCommUnknown() {
        return lastComm == LastComm.UNKNOWN;
    }
    
    public ConnectStatus getConnectStatus() {
        return connectStatus;
    }
    
    public void setConnectStatus(ConnectStatus connectStatus) {
        this.connectStatus = connectStatus;
    }
    
    public boolean isConnected() {
        return connectStatus == ConnectStatus.CONNECTED;
    }
}