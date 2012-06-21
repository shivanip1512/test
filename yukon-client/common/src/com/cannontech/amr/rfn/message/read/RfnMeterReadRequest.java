package com.cannontech.amr.rfn.message.read;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

/**
 * JMS Queue name: yukon.qr.obj.amr.rfn.MeterReadRequest
 */
public class RfnMeterReadRequest implements RfnIdentifyingMessage, Serializable {

    private static final long serialVersionUID = 2L;

    private RfnIdentifier rfnIdentifier;

    public RfnMeterReadRequest(RfnIdentifier meter) {
        setRfnIdentifier(meter);
    }

    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }

    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }

    @Override
    public String toString() {
        return String.format("RfnMeterReadRequest [rfnIdentifier=%s]", rfnIdentifier);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
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
        RfnMeterReadRequest other = (RfnMeterReadRequest) obj;
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null)
                return false;
        } else if (!rfnIdentifier.equals(other.rfnIdentifier))
            return false;
        return true;
    }

}