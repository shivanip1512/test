package com.cannontech.amr.rfn.message.status;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class RfnStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private RfnIdentifier rfnIdentifier;
    private RfnStatusType type;
    private long timeStamp;
    private String value = null;

    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }

    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }

    public RfnStatusType getType() {
        return type;
    }

    public void setType(RfnStatusType type) {
        this.type = type;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
        result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        RfnStatus other = (RfnStatus) obj;
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null)
                return false;
        } else if (!rfnIdentifier.equals(other.rfnIdentifier))
            return false;
        if (timeStamp != other.timeStamp)
            return false;
        if (type != other.type)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfnStatus [rfnIdentifier=%s, type=%s, timeStamp=%s, value=%s]",
                             rfnIdentifier,
                             type,
                             timeStamp,
                             value);
    }
}
