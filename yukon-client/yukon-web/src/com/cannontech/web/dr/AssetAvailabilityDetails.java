package com.cannontech.web.dr;

import org.joda.time.Instant;

import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;

public class AssetAvailabilityDetails {

    private String serialNumber;
    private String type;
    private Instant lastComm;
    private Instant lastRun;
    private String appliances;
    private AssetAvailabilityCombinedStatus availability;
    
    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Instant getLastComm() {
        return lastComm;
    }
    public void setLastComm(Instant lastComm) {
        this.lastComm = lastComm;
    }
    public Instant getLastRun() {
        return lastRun;
    }
    public void setLastRun(Instant lastRun) {
        this.lastRun = lastRun;
    }
    public String getAppliances() {
        return appliances;
    }
    public void setAppliances(String appliances) {
        this.appliances = appliances;
    }
    public AssetAvailabilityCombinedStatus getAvailability() {
        return availability;
    }
    public void setAvailability(AssetAvailabilityCombinedStatus availability) {
        this.availability = availability;
    }
    
}
