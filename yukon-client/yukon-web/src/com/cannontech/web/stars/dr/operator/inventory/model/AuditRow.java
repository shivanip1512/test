package com.cannontech.web.stars.dr.operator.inventory.model;

import org.joda.time.Duration;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.stars.model.LiteLmHardware;

public class AuditRow implements YukonInventory {
    
    private LiteLmHardware hardware;
    private Duration control;
    
    public LiteLmHardware getHardware() {
        return hardware;
    }
    
    public void setHardware(LiteLmHardware hardware) {
        this.hardware = hardware;
    }
    
    public Duration getControl() {
        return control;
    }
    
    public void setControl(Duration control) {
        this.control = control;
    }
    
    @Override
    public InventoryIdentifier getInventoryIdentifier() {
        return hardware.getInventoryIdentifier();
    }
    
}