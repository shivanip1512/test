package com.cannontech.da.rfn.message.archive;

import java.io.Serializable;

/**
 * JMS Queue name: yukon.qr.obj.da.rfn.RfDaArchiveResponse
 */
public class RfDaArchiveResponse implements Serializable {

    private final static long serialVersionUID = 1L;

    private long sensorId;

    public long getSensorId() {
        return sensorId;
    }

    public void setSensorId(long sensorId) {
        this.sensorId = sensorId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (sensorId ^ (sensorId >>> 32));
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
        RfDaArchiveResponse other = (RfDaArchiveResponse) obj;
        if (sensorId != other.sensorId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfDaArchiveResponse [sensorId=%s]", sensorId);
    }

}