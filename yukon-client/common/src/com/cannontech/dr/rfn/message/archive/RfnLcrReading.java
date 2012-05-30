package com.cannontech.dr.rfn.message.archive;

import java.io.Serializable;
import java.util.Arrays;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.YukonRfn;

public class RfnLcrReading implements Serializable, YukonRfn {

    private static final long serialVersionUID = 1L;
    
    private RfnIdentifier rfnIdentifier;
    private byte[] payload;
    private long timeStamp;
    
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }
    
    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }
    
    public byte[] getPayload() {
        return payload;
    }
    
    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
    
    public long getTimeStamp() {
        return timeStamp;
    }
    
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(payload);
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
        RfnLcrReading other = (RfnLcrReading) obj;
        if (!Arrays.equals(payload, other.payload))
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
        return String.format("RfnLcrData [rfnIdentifier=%s, payload=%s, timeStamp=%s]", rfnIdentifier, Arrays.toString(payload), timeStamp);
    }

}