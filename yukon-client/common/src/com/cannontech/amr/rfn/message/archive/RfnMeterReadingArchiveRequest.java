package com.cannontech.amr.rfn.message.archive;

import java.io.Serializable;

import com.cannontech.amr.rfn.message.read.RfnMeterReadingData;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.common.rfn.message.RfnIdentifingMessage;
import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * JMS Queue name: yukon.qr.obj.amr.rfn.MeterReadingArchiveRequest
 */
public class RfnMeterReadingArchiveRequest implements RfnIdentifingMessage, Serializable {

    private static final long serialVersionUID = 4L;

    private RfnMeterReadingData data;
    private RfnMeterReadingType readingType;
    private long dataPointId;

    public RfnMeterReadingType getReadingType() {
        return readingType;
    }

    public void setReadingType(RfnMeterReadingType readingType) {
        this.readingType = readingType;
    }

    public long getDataPointId() {
        return dataPointId;
    }

    public void setDataPointId(long dataPointId) {
        this.dataPointId = dataPointId;
    }

    public void setData(RfnMeterReadingData data) {
        this.data = data;
    }

    public RfnMeterReadingData getData() {
        return data;
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
        result = prime * result + ((readingType == null) ? 0 : readingType.hashCode());
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
        RfnMeterReadingArchiveRequest other = (RfnMeterReadingArchiveRequest) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        if (dataPointId != other.dataPointId)
            return false;
        if (readingType == null) {
            if (other.readingType != null)
                return false;
        } else if (!readingType.equals(other.readingType))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfnMeterReadingArchiveRequest [data=%s, dataPointId=%s, readingType=%s]",
                             data,
                             dataPointId,
                             readingType);
    }

}