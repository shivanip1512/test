package com.cannontech.amr.rfn.model;

import java.io.Serializable;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;

public class RfnMeter extends YukonMeter implements DisplayablePao, Serializable {
    private static final long serialVersionUID = 1L;

    private RfnMeterIdentifier meterIdentifier;

    public RfnMeter(YukonPao pao, RfnMeterIdentifier meterIdentifier) {
        super.setPaoIdentifier(pao.getPaoIdentifier());
        this.meterIdentifier = meterIdentifier;
    }

    public RfnMeterIdentifier getMeterIdentifier() {
        return meterIdentifier;
    }
    
    @Override
    public String getName() {
        // arbitrary guess!
        return meterIdentifier.getSensorSerialNumber();
    }

    @Override
    public String toString() {
        return String.format("RfnMeter [paoIdentifier=%s, meterIdentifier=%s]",
                             getPaoIdentifier(),
                             meterIdentifier);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((meterIdentifier == null) ? 0 : meterIdentifier.hashCode());
        result = prime * result + ((getPaoIdentifier() == null) ? 0 : getPaoIdentifier().hashCode());
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
        if (getPaoIdentifier() == null) {
            if (other.getPaoIdentifier() != null)
                return false;
        } else if (!getPaoIdentifier().equals(other.getPaoIdentifier()))
            return false;
        return true;
    }
}