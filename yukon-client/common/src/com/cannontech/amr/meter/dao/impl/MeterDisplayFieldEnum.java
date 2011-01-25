package com.cannontech.amr.meter.dao.impl;

import com.cannontech.amr.meter.dao.MeterDisplayFieldAccessor;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.meter.search.model.MeterSearchField;

public enum MeterDisplayFieldEnum {

    DEVICE_NAME("Device Name", "deviceName", MeterSearchField.PAONAME, new MeterDisplayFieldAccessor() {
        public String getField(Meter meter) {
            return meter.getName();
        }
    }),
    
    METER_NUMBER("Meter Number", "meterNumber", MeterSearchField.METERNUMBER, new MeterDisplayFieldAccessor() {
        public String getField(Meter meter) {
            String meterNumber = meter.getMeterNumber();
            if (meterNumber == null) {
                return "n/a";
            }
            return meterNumber;
        }
    }),
    
    ADDRESS("Address", "address", MeterSearchField.ADDRESS, new MeterDisplayFieldAccessor() {
        public String getField(Meter meter) {
            return meter.getAddress();
        }
    }),
    
    /**
     * Not an explictly supported "Device Display Template" role property value.
     * Used for meter search selection page only
     */
    ROUTE("Route", "route", MeterSearchField.ROUTE, new MeterDisplayFieldAccessor() {
        public String getField(Meter meter) {
            return meter.getRoute();
        }
    }),
    
    /**
     * Not an explictly supported "Device Display Template" role property value.
     * Used for meter search selection page only
     */
    DEVICE_TYPE("Device Type", "deviceType",MeterSearchField.TYPE, new MeterDisplayFieldAccessor() {
        public String getField(Meter meter) {
            return meter.getPaoType().getPaoTypeName();
        }
    }),
    
    ID("Device ID", "id", null, new MeterDisplayFieldAccessor() {
        public String getField(Meter meter) {
            return ((Integer)meter.getDeviceId()).toString();
        }
    });
    
    private String label;
    private String id;
    private MeterSearchField searchField;
    private MeterDisplayFieldAccessor meterDisplayFieldAccessor;
    
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
    
    public String getField(Meter meter) {
        return this.meterDisplayFieldAccessor.getField(meter);
    }
}
