package com.cannontech.dr.assetavailability;

import org.joda.time.Instant;
import com.cannontech.common.inventory.HardwareType;

public class ApplianceAssetAvailabilityDetails {

    private String primaryGateway;
    private String gatewayName;
    private String serialNumber;
    private HardwareType type;
    private Instant lastComm;
    private Instant lastRun;
    private String appliances;
    private AssetAvailabilityCombinedStatus availability;
    
    public String getGatewayName() {
        return gatewayName;
    }
    
    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }
  
    public String getPrimaryGateway() {
        return primaryGateway;
    }
    
    public void setPrimaryGateway(String primaryGateway) {
        this.primaryGateway = primaryGateway;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public HardwareType getType() {
        return type;
    }

    public void setType(HardwareType type) {
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
