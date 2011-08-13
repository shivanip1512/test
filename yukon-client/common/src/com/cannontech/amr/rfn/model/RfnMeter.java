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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((meterIdentifier == null) ? 0 : meterIdentifier.hashCode());
        result = prime * result + ((paoIdentifier == null) ? 0 : paoIdentifier.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RfnMeter other = (RfnMeter) obj;
        if (meterIdentifier == null) {
            if (other.meterIdentifier != null)
                return false;
        } else if (!meterIdentifier.equals(other.meterIdentifier))
            return false;
        if (paoIdentifier == null) {
            if (other.paoIdentifier != null)
                return false;
        } else if (!paoIdentifier.equals(other.paoIdentifier))
            return false;
        return true;
    }

    
    
}
