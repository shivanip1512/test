package com.cannontech.amr.rfn.model;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;

public class RfnMeter implements YukonDevice, DisplayablePao {
    private PaoIdentifier paoIdentifier;
    private RfnMeterIdentifier meterIdentifier;
    private String meterNumber;
    private String paoName;

    public RfnMeter(YukonPao pao, RfnMeterIdentifier meterIdentifier) {
        this.paoIdentifier = pao.getPaoIdentifier();
        this.meterIdentifier = meterIdentifier;
    }

    public RfnMeterIdentifier getMeterIdentifier() {
        return meterIdentifier;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
    
    @Override
    public String getName() {
        // arbitrary guess!
        return meterIdentifier.getSensorSerialNumber();
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getPaoName() {
        return paoName;
    }

    public void setPaoName(String paoName) {
        this.paoName = paoName;
    }

    @Override
    public String toString() {
        return String.format("RfnMeter [paoIdentifier=%s, meterIdentifier=%s, paoName=%s, meterNumber=%s]",
                             paoIdentifier,
                             meterIdentifier,
                             paoName,
                             meterNumber);
    }

    
    
}
