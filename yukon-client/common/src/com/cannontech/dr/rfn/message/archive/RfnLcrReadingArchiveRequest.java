package com.cannontech.dr.rfn.message.archive;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnArchiveRequest;
import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * JMS Queue name: yukon.rr.obj.dr.rfn.LcrReadingArchiveRequest
 */
public class RfnLcrReadingArchiveRequest implements RfnArchiveRequest, Serializable {

    private static final long serialVersionUID = 1L;

    private RfnLcrReading data;
    private RfnLcrReadingType type;
    private long dataPointId;

    public RfnLcrReading getData() {
        return data;
    }

    public void setData(RfnLcrReading data) {
        this.data = data;
    }

    public RfnLcrReadingType getType() {
        return type;
    }

    public void setType(RfnLcrReadingType type) {
        this.type = type;
    }

    public long getDataPointId() {
        return dataPointId;
    }

    public void setDataPointId(long dataPointId) {
        this.dataPointId = dataPointId;
    }
    
    @Override
    public RfnIdentifier getRfnIdentifier() {
        return data.getRfnIdentifier();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + (int) (dataPointId ^ (dataPointId >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        RfnLcrReadingArchiveRequest other = (RfnLcrReadingArchiveRequest) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        if (dataPointId != other.dataPointId)
            return false;
        if (type != other.type)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("RfnLcrDataArchiveRequest [data=%s, type=%s, dataPointId=%s]", data, type, dataPointId);
    }
    
}