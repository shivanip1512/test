package com.cannontech.amr.rfn.message.read;

import java.io.Serializable;
import java.util.List;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class RfnMeterReadingData implements Serializable {

    private static final long serialVersionUID = 5L;

    private List<ChannelData> channelDataList; // Non timestamped data
    private List<DatedChannelData> datedChannelDataList; // Timestamped data like Peak Demand
    private RfnIdentifier rfnIdentifier;
    private long timeStamp;
    private int recordInterval; // Recording interval length in seconds , only used for interval based readings

    public List<ChannelData> getChannelDataList() {
        return channelDataList;
    }

    public void setChannelDataList(List<ChannelData> channelDataList) {
        this.channelDataList = channelDataList;
    }

    public List<DatedChannelData> getDatedChannelDataList() {
        return datedChannelDataList;
    }
    
    public void setDatedChannelDataList(List<DatedChannelData> datedChannelDataList) {
        this.datedChannelDataList = datedChannelDataList;
    }

    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }

    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    
    public int getRecordInterval() {
        return recordInterval;
    }
    
    public void setRecordInterval(int recordInterval) {
        this.recordInterval = recordInterval;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channelDataList == null) ? 0 : channelDataList.hashCode());
        result =
            prime * result + ((datedChannelDataList == null) ? 0 : datedChannelDataList.hashCode());
        result = prime * result + recordInterval;
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
        RfnMeterReadingData other = (RfnMeterReadingData) obj;
        if (channelDataList == null) {
            if (other.channelDataList != null)
                return false;
        } else if (!channelDataList.equals(other.channelDataList))
            return false;
        if (datedChannelDataList == null) {
            if (other.datedChannelDataList != null)
                return false;
        } else if (!datedChannelDataList.equals(other.datedChannelDataList))
            return false;
        if (recordInterval != other.recordInterval)
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
        return String
            .format("RfnMeterReadingData [rfnIdentifier=%s, channelDataList=%s, datedChannelDataList=%s, timeStamp=%s, recordInterval=%s]",
                    rfnIdentifier,
                    channelDataList,
                    datedChannelDataList,
                    timeStamp,
                    recordInterval);
    }
    
}