package com.cannontech.stars.dr.hardware.service.impl;

import com.cannontech.common.inventory.InventoryIdentifier;

public class NotSupportedException extends RuntimeException {
    
    private InventoryIdentifier inventoryIdentifier;
    public NotSupportedException(InventoryIdentifier inv) {
        this.inventoryIdentifier = inv;
    }

    public InventoryIdentifier getInventoryIdentifier() {
        return inventoryIdentifier;
    }
    
}