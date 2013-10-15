package com.cannontech.amr.meter.model;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;

public class SimpleMeter implements YukonDevice {

    private PaoIdentifier paoIdentifier;
    private String meterNumber;
    
    public SimpleMeter(PaoIdentifier paoIdentifier, String meterNumber) {
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
    
    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("paoIdentifier", getPaoIdentifier());
        tsc.append("meterNumber", getMeterNumber());
        return tsc.toString();
    }
}
