package com.cannontech.web.bulk.ada.model;

import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;

public class AdaDevice implements YukonPao {
    
    private DeviceArchiveData data;
    private String name;
    private String meterNumber;
    private int missingIntervals;
    
    public DeviceArchiveData getData() {
        return data;
    }
    
    public void setData(DeviceArchiveData data) {
        this.data = data;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getMeterNumber() {
        return meterNumber;
    }
    
    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }
    
    public int getMissingIntervals() {
        return missingIntervals;
    }
    
    public void setMissingIntervals(int missingIntervals) {
        this.missingIntervals = missingIntervals;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return data.getPaoIdentifier();
    }
    
}