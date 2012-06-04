package com.cannontech.amr.rfn.message.demandReset;

import java.io.Serializable;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * JMS Queue name: yukon.qr.obj.amr.rfn.MeterDemandResetRequest
 */
public class RfnMeterDemandResetRequest implements Serializable {
    private static final long serialVersionUID = 2L;

    private Set<RfnIdentifier> rfnMeterIdentifiers;

    public RfnMeterDemandResetRequest(Set<RfnIdentifier> rfnMeterIdentifiers) {
        this.rfnMeterIdentifiers = rfnMeterIdentifiers;
    }

    public Set<RfnIdentifier> getRfnMeterIdentifiers() {
        return rfnMeterIdentifiers;
    }

    public void setRfnMeterIdentifiers(Set<RfnIdentifier> rfnMeterIdentifiers) {
        this.rfnMeterIdentifiers = rfnMeterIdentifiers;
    }

    @Override
    public String toString() {
        return String.format("RfnMeterDemandResetRequest [rfnMeterIdentifier=%s]",
                             rfnMeterIdentifiers);
    }
}
