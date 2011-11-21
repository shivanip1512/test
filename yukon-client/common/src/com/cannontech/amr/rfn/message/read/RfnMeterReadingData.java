package com.cannontech.amr.rfn.message.read;

import java.io.Serializable;
import java.util.List;

import com.cannontech.amr.rfn.model.RfnMeterIdentifier;

public class RfnMeterReadingData implements Serializable {

    private static final long serialVersionUID = 3L;

    private List<ChannelData> channelDataList; // Non timestamped data
    private List<DatedChannelData> datedChannelDataList; // Timestamped data like Peak Demand
    private RfnMeterIdentifier rfnMeterIdentifier;
    private long timeStamp;

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

    public RfnMeterIdentifier getRfnMeterIdentifier() {
        return rfnMeterIdentifier;
    }

    public void setRfnMeterIdentifier(RfnMeterIdentifier rfnMeterIdentifier) {
        this.rfnMeterIdentifier = rfnMeterIdentifier;
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
        result = prime * result + ((channelDataList == null) ? 0 : channelDataList.hashCode());
        result =
            prime * result + ((datedChannelDataList == null) ? 0 : datedChannelDataList.hashCode());
        result =
            prime * result + ((rfnMeterIdentifier == null) ? 0 : rfnMeterIdentifier.hashCode());
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
        if (rfnMeterIdentifier == null) {
            if (other.rfnMeterIdentifier != null)
                return false;
        } else if (!rfnMeterIdentifier.equals(other.rfnMeterIdentifier))
            return false;
        if (timeStamp != other.timeStamp)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
            .format("RfnMeterReadingData [channelDataList=%s, datedChannelDataList=%s, rfnMeterIdentifier=%s, timeStamp=%s]",
                    channelDataList,
                    datedChannelDataList,
                    rfnMeterIdentifier,
                    timeStamp);
    }

}