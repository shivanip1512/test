package com.cannontech.amr.meter.model;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.PaoIdentifier;

public class VirtualMeter extends YukonMeter {

    public VirtualMeter() {
    }

    public VirtualMeter(PaoIdentifier paoIdentifier, String meterNumber, String name, boolean disabled) {
        super(paoIdentifier, meterNumber, name, disabled);
    }

    /**
     * Not implemented for this object returns null.
     */
    @Override
    public String getSerialOrAddress() {
        return null;
    }

    /**
     * Not implemented for this object returns null.
     */
    @Override
    public String getRoute() {
        return null;
    }
}
