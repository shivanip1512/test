package com.cannontech.amr.rfn.message.status.type;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class MeterInfoStatus implements RfnStatus<MeterInfo> {

    private static final long serialVersionUID = 1L;

    private RfnIdentifier rfnIdentifier;
    private long timeStamp;
    private MeterInfo data;

    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }

    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }

    @Override
    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public MeterInfo getData() {
        return data;
    }

    public void setData(MeterInfo data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
        result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
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
        MeterInfoStatus other = (MeterInfoStatus) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null)
                return false;
        } else if (!rfnIdentifier.equals(other.rfnIdentifier))
            return false;
        if (timeStamp != other.timeStamp)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("MeterInfoStatus [rfnIdentifier=%s, timeStamp=%s, data=%s]",
                             rfnIdentifier,
                             timeStamp,
                             data);
    }
}
