package com.cannontech.amr.rfn.model;

import java.io.Serializable;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;

public class RfnMeter implements YukonDevice, DisplayablePao, Serializable {
    private static final long serialVersionUID = 1L;
    private PaoIdentifier paoIdentifier;
    private RfnMeterIdentifier meterIdentifier;

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

    @Override
    public String toString() {
        return String.format("RfnMeter [paoIdentifier=%s, meterIdentifier=%s]",
                             paoIdentifier,
                             meterIdentifier);
    }

    
    
}
