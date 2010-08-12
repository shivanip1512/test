package com.cannontech.amr.crf.message;

import java.io.Serializable;

/**
 * JMS Queue name: yukon.rr.obj.amr.crf.MeterReadingArchiveRequest
 */
public class CrfMeterReadingArchiveRequest implements Serializable {

    private static final long serialVersionUID = 2L;

    private CrfMeterReadingData data;
    private CrfMeterReadingType readingType;
    private long dataPointId;

    public CrfMeterReadingType getReadingType() {
        return readingType;
    }

    public void setReadingType(CrfMeterReadingType readingType) {
        this.readingType = readingType;
    }

    public long getDataPointId() {
        return dataPointId;
    }

    public void setDataPointId(long dataPointId) {
        this.dataPointId = dataPointId;
    }

    public void setData(CrfMeterReadingData data) {
        this.data = data;
    }

    public CrfMeterReadingData getData() {
        return data;
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
        CrfMeterReadingArchiveRequest other = (CrfMeterReadingArchiveRequest) obj;
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
        return String.format("CrfMeterReadingArchiveRequest [data=%s, dataPointId=%s, readingType=%s]",
                             data,
                             dataPointId,
                             readingType);
    }

}