package com.cannontech.amr.rfn.message.event;

import java.io.Serializable;
import java.util.Map;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class RfnEvent implements Serializable {

    private static final long serialVersionUID = 2L;

    protected RfnConditionType type;
    protected RfnIdentifier rfnIdentifier;
    protected long timeStamp;
    protected Map<RfnConditionDataType, Object> eventData;

    public RfnConditionType getType() {
        return type;
    }

    public void setType(RfnConditionType type) {
        this.type = type;
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

    public Map<RfnConditionDataType, Object> getEventData() {
        return eventData;
    }

    public void setEventData(Map<RfnConditionDataType, Object> eventData) {
        this.eventData = eventData;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((eventData == null) ? 0 : eventData.hashCode());
        result =
            prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
        result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
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
        RfnEvent other = (RfnEvent) obj;
        if (eventData == null) {
            if (other.eventData != null)
                return false;
        } else if (!eventData.equals(other.eventData))
            return false;
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null)
                return false;
        } else if (!rfnIdentifier.equals(other.rfnIdentifier))
            return false;
        if (timeStamp != other.timeStamp)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
            .format("RfnEvent [type=%s, rfnIdentifier=%s, timeStamp=%tc, eventData=%s]",
                    type,
                    rfnIdentifier,
                    timeStamp,
                    eventData);
    }

}