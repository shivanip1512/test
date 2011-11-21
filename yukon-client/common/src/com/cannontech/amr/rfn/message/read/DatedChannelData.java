package com.cannontech.amr.rfn.message.read;

import java.io.Serializable;

public class DatedChannelData extends ChannelData implements Serializable {

    private static final long serialVersionUID = 1L;

    private long timeStamp;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
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
        return true;
    }

    @Override
    public String toString() {
        return String
            .format("DatedChannelData [channelNumber=%s, unitOfMeasure=%s, unitOfMeasureModifiers=%s, value=%s, timestamp=%s, status=%s]",
                    getChannelNumber(),
                    getUnitOfMeasure(),
                    getUnitOfMeasureModifiers(),
                    getValue(),
                    getTimeStamp(),
                    getStatus());
    }


}
