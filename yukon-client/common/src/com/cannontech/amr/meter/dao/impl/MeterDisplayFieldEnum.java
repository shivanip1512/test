package com.cannontech.amr.meter.dao.impl;

import com.cannontech.amr.meter.dao.MeterDisplayFieldAccessor;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.meter.search.model.MeterSearchField;

public enum MeterDisplayFieldEnum {

    DEVICE_NAME("Device Name", "deviceName", MeterSearchField.PAONAME, new MeterDisplayFieldAccessor() {
        @Override
        public String getField(YukonMeter meter) {
            return meter.getName();
        }
    }),
    
    METER_NUMBER("Meter Number", "meterNumber", MeterSearchField.METERNUMBER, new MeterDisplayFieldAccessor() {
        @Override
        public String getField(YukonMeter meter) {
            String meterNumber = meter.getMeterNumber();
            if (meterNumber == null) {
                return "n/a";
            }
            return meterNumber;
        }
    }),
    
    ADDRESS("Address", "address", MeterSearchField.ADDRESS, new MeterDisplayFieldAccessor() {
        @Override
        public String getField(YukonMeter meter) {
            return meter.getSerialOrAddress();
        }
    }),
    
    /**
     * Not an explictly supported "Device Display Template" role property value.
     * Used for meter search selection page only
     */
    ROUTE("Route", "route", MeterSearchField.ROUTE, new MeterDisplayFieldAccessor() {
        @Override
        public String getField(YukonMeter meter) {
            return meter.getRoute();
        }
    }),
    
    /**
     * Not an explictly supported "Device Display Template" role property value.
     * Used for meter search selection page only
     */
    DEVICE_TYPE("Device Type", "deviceType",MeterSearchField.TYPE, new MeterDisplayFieldAccessor() {
        @Override
        public String getField(YukonMeter meter) {
            return meter.getPaoType().getPaoTypeName();
        }
    }),
    
    ID("Device ID", "id", null, new MeterDisplayFieldAccessor() {
        @Override
        public String getField(YukonMeter meter) {
            return ((Integer)meter.getDeviceId()).toString();
        }
    });
    
    private final String label;
    private final String id;
    private final MeterSearchField searchField;
    private final MeterDisplayFieldAccessor meterDisplayFieldAccessor;
    
    MeterDisplayFieldEnum(String label, String id, MeterSearchField searchField,
            MeterDisplayFieldAccessor meterDisplayFieldAccessor) {
        this.label = label;
        this.id = id;
        this.searchField = searchField;
        this.meterDisplayFieldAccessor = meterDisplayFieldAccessor;
    }
    
    public String getId() {
        return id;
    }
    
    public String getLabel() {
        return label;
    }
    
    public MeterSearchField getSearchField() {
        return searchField;
    }
    
    public String getField(YukonMeter meter) {
        return meterDisplayFieldAccessor.getField(meter);
    }
}
