package com.cannontech.dr.assetavailability;

public class InventoryRelayAppliance {
    private final int inventoryId;
    private final int relay;
    private final int applianceId;
    
    public InventoryRelayAppliance(int inventoryId, int relay, int applianceId) {
        this.inventoryId = inventoryId;
        this.relay = relay;
        this.applianceId = applianceId;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public int getRelay() {
        return relay;
    }

    public int getApplianceId() {
        return applianceId;
    }
}
