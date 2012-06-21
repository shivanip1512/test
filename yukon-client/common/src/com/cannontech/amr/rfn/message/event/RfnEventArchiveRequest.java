package com.cannontech.amr.rfn.message.event;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifyingMessage;
import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * JMS Queue name: yukon.qr.obj.amr.rfn.EventArchiveRequest
 */
public class RfnEventArchiveRequest implements RfnIdentifyingMessage, Serializable {

    private static final long serialVersionUID = 2L;

    protected RfnEvent event;
    protected long dataPointId;

    public RfnEvent getEvent() {
        return event;
    }

    public void setEvent(RfnEvent event) {
        this.event = event;
    }

    public long getDataPointId() {
        return dataPointId;
    }

    public void setDataPointId(long dataPointId) {
        this.dataPointId = dataPointId;
    }

    @Override
    public RfnIdentifier getRfnIdentifier() {
        return event.getRfnIdentifier();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (dataPointId ^ (dataPointId >>> 32));
        result = prime * result + ((event == null) ? 0 : event.hashCode());
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
        RfnEventArchiveRequest other = (RfnEventArchiveRequest) obj;
        if (dataPointId != other.dataPointId)
            return false;
        if (event == null) {
            if (other.event != null)
                return false;
        } else if (!event.equals(other.event))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfnEventArchiveRequest [event=%s, dataPointId=%s]",
                             event,
                             dataPointId);
    }

}