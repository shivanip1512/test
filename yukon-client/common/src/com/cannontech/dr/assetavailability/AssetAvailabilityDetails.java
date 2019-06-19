package com.cannontech.dr.assetavailability;

import org.joda.time.Instant;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.PaoIdentifier;

/**
 * Contains asset availability details for a single inventory.
 */
public class AssetAvailabilityDetails {
    
    private String primaryGateway;
    private String gatewayName;
    private String serialNumber;
    private HardwareType type;
    private Instant lastComm;
    private Instant lastRun;
    private AssetAvailabilityCombinedStatus availability;
    private Integer inventoryId;
    private Integer deviceId;
    
    public String getPrimaryGateway() {
        return primaryGateway;
    }
    
    public void setPrimaryGateway(String primaryGateway) {
        this.primaryGateway = primaryGateway;
    }
    
    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
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

    public AssetAvailabilityCombinedStatus getAvailability() {
        return availability;
    }

    public void setAvailability(AssetAvailabilityCombinedStatus availability) {
        this.availability = availability;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }
}
