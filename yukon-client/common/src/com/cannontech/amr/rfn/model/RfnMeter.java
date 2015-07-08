package com.cannontech.amr.rfn.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.YukonRfn;

public class RfnMeter extends YukonMeter implements YukonRfn, Serializable {
    private static final long serialVersionUID = 2L;

    private RfnIdentifier meterIdentifier;

    public RfnMeter() {
        super();
    }

    public RfnMeter(YukonPao pao, RfnIdentifier meterIdentifier, String meterNumber, String paoName, boolean disabled) {
        super(pao.getPaoIdentifier(), meterNumber, paoName, disabled);
        this.meterIdentifier = meterIdentifier;
    }

    @Override
    public RfnIdentifier getRfnIdentifier() {
        return meterIdentifier;
    }

    @Override
    public String getSerialOrAddress() {
        return getRfnIdentifier().getSensorSerialNumber();
    }


    /** RFN meters do not have a "route" */
    @Override
    public String getRoute() {
        return "";
    }
    
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.appendSuper(super.toString());
        tsb.append("rfnId", meterIdentifier);
        return tsb.toString();
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