package com.cannontech.amr.rfn.message.alarm;

import java.io.Serializable;

/**
 * JMS Queue name: yukon.rr.obj.amr.rfn.AlarmArchiveResponse
 */
public class RfnAlarmArchiveResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private long dataPointId;
    
    public long getDataPointId() {
        return dataPointId;
    }
    
    public void setDataPointId(long dataPointId) {
        this.dataPointId = dataPointId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (dataPointId ^ (dataPointId >>> 32));
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
        RfnAlarmArchiveResponse other = (RfnAlarmArchiveResponse) obj;
        if (dataPointId != other.dataPointId)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("RfnAlarmArchiveResponse [dataPointId=%s]", dataPointId);
    }
    
}