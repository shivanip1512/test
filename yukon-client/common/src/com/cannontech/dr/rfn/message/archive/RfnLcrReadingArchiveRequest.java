package com.cannontech.dr.rfn.message.archive;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifyingMessage;
import com.cannontech.dr.rfn.message.RfnLcrDataMessage;

/**
 * JMS Queue name: yukon.qr.obj.dr.rfn.LcrReadingArchiveRequest
 */
public class RfnLcrReadingArchiveRequest extends RfnLcrArchiveRequest implements RfnIdentifyingMessage, Serializable, RfnLcrDataMessage {

    private static final long serialVersionUID = 1L;

    private RfnLcrReading data;
    private RfnLcrReadingType type;
    private long dataPointId;

    @Override
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
        return String.format("RfnLcrReadingArchiveRequest [rfnIdentifier=%s, data=%s, type=%s, dataPointId=%s]", getRfnIdentifier(), data, type, dataPointId);
    }
    
}