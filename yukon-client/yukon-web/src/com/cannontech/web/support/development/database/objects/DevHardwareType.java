package com.cannontech.web.support.development.database.objects;

import com.cannontech.common.inventory.HardwareType;

public class DevHardwareType {
    private boolean create = false;
    private HardwareType hardwareType;

    public DevHardwareType(HardwareType hardwareType) {
        this.hardwareType = hardwareType;
    }
    public boolean isCreate() {
        return create;
    }
    public void setCreate(boolean create) {
        this.create = create;
    }
    public HardwareType getHardwareType() {
        return hardwareType;
    }
    public void setHardwareType(HardwareType hardwareType) {
        this.hardwareType = hardwareType;
    }
}
