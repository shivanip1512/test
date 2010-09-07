package com.cannontech.amr.rfn.message.disconnect;

import java.io.Serializable;

import com.cannontech.amr.rfn.model.RfnMeterIdentifier;

public class RfnMeterDisconnectRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private RfnMeterIdentifier rfnMeterIdentifier;
    private RfnMeterDisconnectStatusType action;

    public RfnMeterDisconnectRequest(RfnMeterIdentifier meter, RfnMeterDisconnectStatusType action) {
        setRfnMeterIdentifier(meter);
        setAction(action);
    }

    public RfnMeterIdentifier getRfnMeterIdentifier() {
        return rfnMeterIdentifier;
    }

    public void setRfnMeterIdentifier(RfnMeterIdentifier rfnMeterIdentifier) {
        this.rfnMeterIdentifier = rfnMeterIdentifier;
    }

    public void setAction(RfnMeterDisconnectStatusType action) {
        this.action = action;
    }

    public RfnMeterDisconnectStatusType getAction() {
        return action;
    }

    @Override
    public String toString() {
        return String.format("RfnMeterDisconnectRequest [rfnMeterIdentifier=%s]", rfnMeterIdentifier);
    }

}