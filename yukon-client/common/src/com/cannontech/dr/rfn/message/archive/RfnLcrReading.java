package com.cannontech.dr.rfn.message.archive;

import java.io.Serializable;
import java.util.Arrays;

public class RfnLcrReading implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private byte[] payload;
    private long timeStamp;
    
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
        if (timeStamp != other.timeStamp)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfnLcrReading [payload=%s, timeStamp=%s]", Arrays.toString(payload), timeStamp);
    }

}