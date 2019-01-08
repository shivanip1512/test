package com.cannontech.common.rfn.message.alarm;

import java.io.Serializable;

/**
 * JMS Queue name: com.eaton.eas.yukon.networkmanager.alarm.confirm
 */
public class AlarmArchiveResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private AlarmCategory alarmCategory;
    private long dataPointId;

    public AlarmCategory getAlarmCategory() {
        return alarmCategory;
    }

    public void setAlarmCategory(AlarmCategory alarmCategory) {
        this.alarmCategory = alarmCategory;
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
        result = prime * result + ((alarmCategory == null) ? 0 : alarmCategory.hashCode());
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
        AlarmArchiveResponse other = (AlarmArchiveResponse) obj;
        if (alarmCategory != other.alarmCategory)
            return false;
        if (dataPointId != other.dataPointId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("AlarmArchiveResponse [alarmCategory=%s, dataPointId=%s]", alarmCategory, dataPointId);
    }
}