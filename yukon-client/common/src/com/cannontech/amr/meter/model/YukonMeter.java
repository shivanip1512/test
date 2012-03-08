package com.cannontech.amr.meter.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;

public class YukonMeter implements YukonDevice {
    private PaoIdentifier paoIdentifier;
    private String meterNumber;

    public YukonMeter() {
    }
    
    public YukonMeter(PaoIdentifier paoIdentifier, String meterNumber) {
        this.paoIdentifier = paoIdentifier;
        this.meterNumber = meterNumber;
    }
    
    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
    
    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier;
    }

    public String getMeterNumber() {
        return meterNumber;
    }
    
    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }
}
