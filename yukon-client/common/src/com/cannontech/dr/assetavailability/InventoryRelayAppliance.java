package com.cannontech.dr.assetavailability;

public class InventoryRelayAppliance {
    private final int inventoryId;
    private final int relay;
    private final int applianceId;
    private final int applianceCategoryId;
    
    public InventoryRelayAppliance(int inventoryId, int relay, int applianceId, int applianceCategoryId) {
        this.inventoryId = inventoryId;
        this.relay = relay;
        this.applianceId = applianceId;
        this.applianceCategoryId = applianceCategoryId;
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
    
    public int getApplianceCategoryId() {
        return applianceCategoryId;
    }
}
