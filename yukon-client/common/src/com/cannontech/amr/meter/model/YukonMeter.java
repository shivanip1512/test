package com.cannontech.amr.meter.model;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;

public abstract class YukonMeter extends SimpleMeter implements DisplayablePao{
    
    private String name;
    private boolean disabled;
    
    protected YukonMeter(PaoIdentifier paoIdentifier, String meterNumber, String name, boolean disabled) {
        super(paoIdentifier, meterNumber);
        this.name = name;
        this.disabled = disabled;
    }

    @Override
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

    public int getDeviceId() {
        return getPaoIdentifier().getPaoId();
    }

    public PaoType getPaoType() {
        return getPaoIdentifier().getPaoType();
    }
    
    /**
     * Returns either address (PLC) or serialnumber (RFN) depending on type.
     * This is a helper method to limit repetition of this instanceof checks 
     */
    public String getSerialOrAddress() {
        if (this instanceof PlcMeter) {
            return ((PlcMeter)this).getAddress();
        } else if (this instanceof RfnMeter) {
            return ((RfnMeter)this).getRfnIdentifier().getSensorSerialNumber();
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the (plc) routeName for Meter objects, for RfnMeters, returns empty string.
     * This is a helper method to limit repetition of this instanceof checks 
     */
    public String getRoute() {
        if (this instanceof PlcMeter) {
            return ((PlcMeter)this).getRoute();
        } else if (this instanceof RfnMeter) {
            return "";
        }
        throw new UnsupportedOperationException();

    }
    
    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("meter", super.toString());
        tsc.append("name", getName());
        tsc.append("disabled", isDisabled());
        return tsc.toString();
    }
}
