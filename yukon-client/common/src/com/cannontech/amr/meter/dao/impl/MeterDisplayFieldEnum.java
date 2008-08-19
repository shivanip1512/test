package com.cannontech.amr.meter.dao.impl;

import com.cannontech.amr.csr.model.CsrSearchField;
import com.cannontech.amr.meter.dao.MeterDisplayFieldAccessor;
import com.cannontech.amr.meter.model.Meter;

public enum MeterDisplayFieldEnum {

    DEVICE_NAME("Device Name", "deviceName", CsrSearchField.PAONAME, new MeterDisplayFieldAccessor() {
        public String getField(Meter meter) {
            return meter.getName();
        }
    }),
    
    METER_NUMBER("Meter Number", "meterNumber", CsrSearchField.METERNUMBER, new MeterDisplayFieldAccessor() {
        public String getField(Meter meter) {
            String meterNumber = meter.getMeterNumber();
            if (meterNumber == null) {
                return "n/a";
            }
            return meterNumber;
        }
    }),
    
    ADDRESS("Address", "address", CsrSearchField.ADDRESS, new MeterDisplayFieldAccessor() {
        public String getField(Meter meter) {
            return meter.getAddress();
        }
    }),
    
    ROUTE("Route", "route", CsrSearchField.ROUTE, new MeterDisplayFieldAccessor() {
        public String getField(Meter meter) {
            return meter.getRoute();
        }
    }),
    
    DEVICE_TYPE("Device Type", "deviceType",CsrSearchField.TYPE, new MeterDisplayFieldAccessor() {
        public String getField(Meter meter) {
            return meter.getTypeStr();
        }
    }),
    
    ID("Device ID", "id", null, new MeterDisplayFieldAccessor() {
        public String getField(Meter meter) {
            return ((Integer)meter.getDeviceId()).toString();
        }
    });
    
    private String label;
    private String id;
    private CsrSearchField searchField;
    private MeterDisplayFieldAccessor meterDisplayFieldAccessor;
    
    MeterDisplayFieldEnum(String label, String id, CsrSearchField searchField,
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
    
    public CsrSearchField getSearchField() {
        return searchField;
    }
    
    public String getField(Meter meter) {
        return this.meterDisplayFieldAccessor.getField(meter);
    }
}
