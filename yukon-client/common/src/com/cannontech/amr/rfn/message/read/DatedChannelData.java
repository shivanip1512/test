package com.cannontech.amr.rfn.message.read;

import java.io.Serializable;

public class DatedChannelData extends ChannelData implements Serializable {

    private static final long serialVersionUID = 1L;

    private long timeStamp;
    
    /*
     * The CD of the event causing the coincidental measurement.
     * This would be the Peak Demand CD if this object was the Var CD recorded as a result of the Peak Demand event.
     * Will be null if not used.
     */
    private ChannelData baseChannelData;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    
    public ChannelData getBaseChannelData() {
        return baseChannelData;
    }
    
    public void setBaseChannelData(ChannelData baseChannelData) {
        this.baseChannelData = baseChannelData;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
        result = 
            prime * result + ((baseChannelData == null) ? 0 : baseChannelData.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DatedChannelData other = (DatedChannelData) obj;
        if (timeStamp != other.timeStamp) {
            return false;
        }
        if (baseChannelData == null) {
            if (other.baseChannelData != null)
                return false;
        } else if (!baseChannelData.equals(other.baseChannelData)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("DatedChannelData [channelNumber=%s, unitOfMeasure=%s, unitOfMeasureModifiers=%s, value=%s, timestamp=%s, status=%s, baseChannelData=%s]",
            getChannelNumber(),
            getUnitOfMeasure(),
            getUnitOfMeasureModifiers(),
            getValue(),
            getTimeStamp(),
            getStatus(),
            getBaseChannelData());
    }
    
}