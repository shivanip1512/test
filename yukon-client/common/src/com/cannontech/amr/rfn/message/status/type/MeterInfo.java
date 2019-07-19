package com.cannontech.amr.rfn.message.status.type;

import java.io.Serializable;

public class MeterInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private MeterDisconnectStatus meterDisconnectStatus;
    private String meterConfigurationID;

    public String getMeterConfigurationID() {
        return meterConfigurationID;
    }

    public void setMeterConfigurationID(String meterConfigurationID) {
        this.meterConfigurationID = meterConfigurationID;
    }

    public MeterDisconnectStatus getMeterDisconnectStatus() {
        return meterDisconnectStatus;
    }

    public void setMeterDisconnectStatus(MeterDisconnectStatus meterDisconnectStatus) {
        this.meterDisconnectStatus = meterDisconnectStatus;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result + ((meterConfigurationID == null) ? 0 : meterConfigurationID.hashCode());
        result = prime * result
            + ((meterDisconnectStatus == null) ? 0 : meterDisconnectStatus.hashCode());
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
        MeterInfo other = (MeterInfo) obj;
        if (meterConfigurationID == null) {
            if (other.meterConfigurationID != null)
                return false;
        } else if (!meterConfigurationID.equals(other.meterConfigurationID))
            return false;
        if (meterDisconnectStatus == null) {
            if (other.meterDisconnectStatus != null)
                return false;
        } else if (!meterDisconnectStatus.equals(other.meterDisconnectStatus))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("MeterInfo [meterDisconnectStatus=%s, meterConfigurationID=%s]",
                             meterDisconnectStatus,
                             meterConfigurationID);
    }
}
