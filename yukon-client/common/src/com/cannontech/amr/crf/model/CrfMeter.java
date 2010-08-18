package com.cannontech.amr.crf.model;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;

public class CrfMeter implements YukonDevice, DisplayablePao {
    private PaoIdentifier paoIdentifier;
    private CrfMeterIdentifier meterIdentifier;
    private String meterNumber;
    private String paoName;

    public CrfMeter(YukonPao pao, CrfMeterIdentifier meterIdentifier) {
        this.paoIdentifier = pao.getPaoIdentifier();
        this.meterIdentifier = meterIdentifier;
    }

    public CrfMeterIdentifier getMeterIdentifier() {
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
        return String.format("CrfMeter [paoIdentifier=%s, meterIdentifier=%s, paoName=%s, meterNumber=%s]",
                             paoIdentifier,
                             meterIdentifier,
                             paoName,
                             meterNumber);
    }

    
    
}
