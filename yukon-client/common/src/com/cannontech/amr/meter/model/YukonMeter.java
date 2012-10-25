package com.cannontech.amr.meter.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;

public class YukonMeter implements YukonDevice {
    private PaoIdentifier paoIdentifier;
    private String meterNumber;
    private String name;
    private boolean disabled;
    
    public YukonMeter() {
    }
    
    public YukonMeter(PaoIdentifier paoIdentifier, String meterNumber, String name, boolean disabled) {
        this.paoIdentifier = paoIdentifier;
        this.meterNumber = meterNumber;
        this.name = name;
        this.disabled = disabled;
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
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

}
