package com.cannontech.common.bulk.model;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * This object holds a single row of FDR translation data for export.
 * Note that the header row is stored in a different format.
 */
public class FdrExportDataRow {
    private String deviceName;
    private String deviceType;
    private String pointName;
    private String direction;
    private List<String> values = Lists.newArrayList();
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    
    public void setPointName(String pointName) {
        this.pointName = pointName;
    }
    
    public void setDirection(String direction) {
        this.direction = direction;
    }
    
    public void addInterfaceValue(String value) {
        values.add(value);
    }
    
    /**
     * Outputs the data row as an array of Strings. Default values
     * are output first, in order of indices (see FdrCsvHeader.getIndex()),
     * followed by interfaces-specific items.
     */
    public String[] asArray() { 
        List<String> tempList = Lists.newArrayList();
        tempList.add(deviceName);
        tempList.add(deviceType);
        tempList.add(pointName);
        tempList.add(direction);
        tempList.addAll(values);
                
        return tempList.toArray(new String[tempList.size()]);
    }
}