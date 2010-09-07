package com.cannontech.amr.rfn.message.read;

import java.io.Serializable;

import com.cannontech.amr.rfn.model.RfnMeterIdentifier;

/**
 * JMS Queue name: yukon.rr.obj.amr.rfn.MeterReadRequest
 */
public class RfnMeterReadRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private RfnMeterIdentifier rfnMeterIdentifier;

    public RfnMeterReadRequest(RfnMeterIdentifier meter) {
        setRfnMeterIdentifier(meter);
    }

    public RfnMeterIdentifier getRfnMeterIdentifier() {
        return rfnMeterIdentifier;
    }

    public void setRfnMeterIdentifier(RfnMeterIdentifier rfnMeterIdentifier) {
        this.rfnMeterIdentifier = rfnMeterIdentifier;
    }

    @Override
    public String toString() {
        return String.format("RfnMeterReadRequest [rfnMeterIdentifier=%s]", rfnMeterIdentifier);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rfnMeterIdentifier == null) ? 0 : rfnMeterIdentifier.hashCode());
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
        if (rfnMeterIdentifier == null) {
            if (other.rfnMeterIdentifier != null)
                return false;
        } else if (!rfnMeterIdentifier.equals(other.rfnMeterIdentifier))
            return false;
        return true;
    }

}