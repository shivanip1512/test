package com.cannontech.amr.rfn.message.alarm;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnArchiveRequest;
import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * JMS Queue name: yukon.qr.obj.amr.rfn.AlarmArchiveRequest
 */
public class RfnAlarmArchiveRequest implements RfnArchiveRequest, Serializable {

    private static final long serialVersionUID = 2L;

    private RfnAlarm alarm;
    private long dataPointId;

    public RfnAlarm getAlarm() {
        return alarm;
    }

    public void setAlarm(RfnAlarm alarm) {
        this.alarm = alarm;
    }

    public long getDataPointId() {
        return dataPointId;
    }

    public void setDataPointId(long dataPointId) {
        this.dataPointId = dataPointId;
    }

    @Override
    public RfnIdentifier getRfnIdentifier() {
        return alarm.getRfnIdentifier();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alarm == null) ? 0 : alarm.hashCode());
        result = prime * result + (int) (dataPointId ^ (dataPointId >>> 32));
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
        RfnAlarmArchiveRequest other = (RfnAlarmArchiveRequest) obj;
        if (alarm == null) {
            if (other.alarm != null)
                return false;
        } else if (!alarm.equals(other.alarm))
            return false;
        if (dataPointId != other.dataPointId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfnAlarmArchiveRequest [alarm=%s, dataPointId=%s]",
                             alarm,
                             dataPointId);
    }

}