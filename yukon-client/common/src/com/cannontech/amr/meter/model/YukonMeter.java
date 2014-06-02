package com.cannontech.amr.meter.model;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;

public abstract class YukonMeter extends SimpleMeter implements DisplayablePao {
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
     * Return the field representing the communication address or sensor serial number.
     */
    public abstract String getSerialOrAddress();

    /**
     * Return the communication routeName for Meter objects. If no route, overwrite and return empty string.
     */
    public abstract String getRoute();

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("meter", super.toString());
        tsc.append("name", getName());
        tsc.append("disabled", isDisabled());
        return tsc.toString();
    }
}
