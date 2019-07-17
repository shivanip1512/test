package com.cannontech.amr.rfn.message.disconnect;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

/**
 * JMS Queue name: yukon.qr.obj.amr.rfn.MeterDisconnectRequest
 */
public class RfnMeterDisconnectRequest implements RfnIdentifyingMessage, Serializable {

    private static final long serialVersionUID = 2L;

    private RfnIdentifier rfnIdentifier;
    private RfnMeterDisconnectCmdType action;

    public RfnMeterDisconnectRequest(RfnIdentifier meter, RfnMeterDisconnectCmdType action) {
        setRfnIdentifier(meter);
        setAction(action);
    }

    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }

    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }

    public void setAction(RfnMeterDisconnectCmdType action) {
        this.action = action;
    }

    public RfnMeterDisconnectCmdType getAction() {
        return action;
    }

    @Override
    public String toString() {
        return String.format("RfnMeterDisconnectRequest [rfnIdentifier=%s]:%s", rfnIdentifier, action);
    }

}