package com.cannontech.amr.rfn.message.status.type;

import java.io.Serializable;

public class MeterDisconnectStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    RfnMeterDisconnectMeterMode meterMode;
    RfnMeterDisconnectStateType relayStatus;

    public RfnMeterDisconnectMeterMode getMeterMode() {
        return meterMode;
    }

    public void setMeterMode(RfnMeterDisconnectMeterMode meterMode) {
        this.meterMode = meterMode;
    }

    public RfnMeterDisconnectStateType getRelayStatus() {
        return relayStatus;
    }

    public void setRelayStatus(RfnMeterDisconnectStateType relayStatus) {
        this.relayStatus = relayStatus;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((meterMode == null) ? 0 : meterMode.hashCode());
        result = prime * result + ((relayStatus == null) ? 0 : relayStatus.hashCode());
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
        MeterDisconnectStatus other = (MeterDisconnectStatus) obj;
        if (meterMode != other.meterMode)
            return false;
        if (relayStatus != other.relayStatus)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
            .format("MeterDisconnectStatus [meterMode=%s, relayStatus=%s]", meterMode, relayStatus);
    }
}
