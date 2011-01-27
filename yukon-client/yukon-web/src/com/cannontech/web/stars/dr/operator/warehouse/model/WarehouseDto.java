package com.cannontech.web.stars.dr.operator.warehouse.model;

import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.db.stars.hardware.Warehouse;

public class WarehouseDto {
    private Warehouse warehouse = new Warehouse();
    private LiteAddress address = new LiteAddress();
    
    public WarehouseDto() {}
    
    public WarehouseDto(Warehouse warehouse, LiteAddress address) {
        this.warehouse = warehouse;
        this.address = address;
    }
    
    public Warehouse getWarehouse() {
        return warehouse;
    }
    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
    public LiteAddress getAddress() {
        return address;
    }
    public void setAddress(LiteAddress address) {
        this.address = address;
    }
}
