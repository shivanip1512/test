package com.cannontech.common.pao;

import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;

public enum PaoClass {
    TRANSMITTER(DeviceClasses.TRANSMITTER),
    RTU(DeviceClasses.RTU),
    IED(DeviceClasses.IED),
    METER(DeviceClasses.METER),
    CARRIER(DeviceClasses.CARRIER),
    GROUP(DeviceClasses.GROUP),
    VIRTUAL(DeviceClasses.VIRTUAL),
    LOADMANAGEMENT(DeviceClasses.LOADMANAGEMENT),
    SYSTEM(DeviceClasses.SYSTEM),
    GRID(DeviceClasses.GRID),
    ROUTE(PAOGroups.CLASS_ROUTE),
    PORT(PAOGroups.CLASS_PORT),
    CUSTOMER(PAOGroups.CLASS_CUSTOMER),
    CAPCONTROL(PAOGroups.CLASS_CAPCONTROL),
    // Schedule doesn't seem have a constant already defined anywhere.
    SCHEDULE(0),
    ;

    // legacy class id
    final int paoClassId;

    private PaoClass(int paoClassId) {
        this.paoClassId = paoClassId;
    }

    public int getPaoClassId() {
        return paoClassId;
    }
}
