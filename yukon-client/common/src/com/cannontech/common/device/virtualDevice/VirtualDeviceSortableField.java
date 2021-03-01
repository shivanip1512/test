package com.cannontech.common.device.virtualDevice;

import java.util.Comparator;

import com.cannontech.common.pao.PaoType;

public enum VirtualDeviceSortableField {
    PAO_NAME("PAOName", (VirtualDeviceBaseModel m1, VirtualDeviceBaseModel m2) -> {
        return m1.getName().compareToIgnoreCase(m2.getName());
    }),
    DISABLE_FLAG("DisableFlag", (VirtualDeviceBaseModel m1, VirtualDeviceBaseModel m2) -> {
        return m2.getEnable().compareTo(m1.getEnable());
        // This comparison is reversed on purpose to maintain consistency
    }),
    METER_NUMBER("MeterNumber", (VirtualDeviceBaseModel m1, VirtualDeviceBaseModel m2) -> {
        if (m1.getType() == PaoType.VIRTUAL_METER && m2.getType() == PaoType.VIRTUAL_METER) {
            VirtualMeterModel mm1 = (VirtualMeterModel) m1;
            VirtualMeterModel mm2 = (VirtualMeterModel) m2;
            return mm1.getMeterNumber().compareTo(mm2.getMeterNumber());
        } else if (m1.getType() == PaoType.VIRTUAL_METER && m2.getType() != PaoType.VIRTUAL_METER) {
            return 1; // Virtual Meters are greater than non virtual meters
        } else if (m1.getType() != PaoType.VIRTUAL_METER && m2.getType() == PaoType.VIRTUAL_METER) {
            return -1;
        } else {
            return 0;
        }
    });
    
    private final String dbString;
    private final Comparator<VirtualDeviceBaseModel> comparator;
    
    private VirtualDeviceSortableField(String dbString, Comparator<VirtualDeviceBaseModel> comparator) {
        this.dbString = dbString;
        this.comparator = comparator;
    }
    
    public String getDbString() {
        return this.dbString;
    }
    
    public Comparator<VirtualDeviceBaseModel> getComparator() {
        return this.comparator;
    }

}
