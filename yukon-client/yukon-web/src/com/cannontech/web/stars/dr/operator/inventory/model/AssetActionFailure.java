package com.cannontech.web.stars.dr.operator.inventory.model;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;

public class AssetActionFailure {
    
    private YukonMessageSourceResolvable reason;
    private InventoryIdentifier identifier;
    private DisplayableLmHardware lmHardware;
    
    public AssetActionFailure(InventoryIdentifier identifier, DisplayableLmHardware lmHardware, 
            YukonMessageSourceResolvable reason) {
        this.identifier = identifier;
        this.lmHardware = lmHardware;
        this.reason = reason;
    }
    
    public YukonMessageSourceResolvable getReason() {
        return reason;
    }
    
    public void setReason(YukonMessageSourceResolvable reason) {
        this.reason = reason;
    }
    
    public InventoryIdentifier getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(InventoryIdentifier identifier) {
        this.identifier = identifier;
    }
    
    public DisplayableLmHardware getLmHardware() {
        return lmHardware;
    }
    
    public void setLmHardware(DisplayableLmHardware lmHardware) {
        this.lmHardware = lmHardware;
    }
    
}