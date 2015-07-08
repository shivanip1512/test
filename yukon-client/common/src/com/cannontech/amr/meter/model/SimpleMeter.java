package com.cannontech.amr.meter.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;

public class SimpleMeter implements YukonDevice {

    private PaoIdentifier paoIdentifier;
    private String meterNumber;
    
    public SimpleMeter() {
        super();
    }

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
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append("paoIdentifier", getPaoIdentifier());
        tsb.append("meterNumber", getMeterNumber());
        return tsb.toString();
    }
}
