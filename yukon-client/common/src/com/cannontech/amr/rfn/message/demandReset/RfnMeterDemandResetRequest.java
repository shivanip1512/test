package com.cannontech.amr.rfn.message.demandReset;

import java.io.Serializable;
import java.util.Set;

import com.cannontech.amr.rfn.model.RfnMeterIdentifier;

/**
 * JMS Queue name: yukon.rr.obj.amr.rfn.MeterDemandResetRequest
 */
public class RfnMeterDemandResetRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Set<RfnMeterIdentifier> rfnMeterIdentifiers;

    public RfnMeterDemandResetRequest(Set<RfnMeterIdentifier> rfnMeterIdentifiers) {
        this.rfnMeterIdentifiers = rfnMeterIdentifiers;
    }

    public Set<RfnMeterIdentifier> getRfnMeterIdentifiers() {
        return rfnMeterIdentifiers;
    }

    public void setRfnMeterIdentifiers(Set<RfnMeterIdentifier> rfnMeterIdentifiers) {
        this.rfnMeterIdentifiers = rfnMeterIdentifiers;
    }

    @Override
    public String toString() {
        return String.format("RfnMeterDemandResetRequest [rfnMeterIdentifier=%s]",
                             rfnMeterIdentifiers);
    }
}
