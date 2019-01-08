package com.cannontech.common.rfn.message.alarm;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifyingMessage;
import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * JMS Queue name: com.eaton.eas.yukon.networkmanager.alarm.notification
 */
public class AlarmArchiveRequest implements RfnIdentifyingMessage, Serializable {

    private static final long serialVersionUID = 1L;

    private AlarmCategory alarmCategory;
    private long dataPointId;
    private AlarmData alarmData;
    
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

    public AlarmData getAlarmData() {
        return alarmData;
    }

    public void setAlarmData(AlarmData alarmData) {
        this.alarmData = alarmData;
    }

    @Override
    public RfnIdentifier getRfnIdentifier() {
        return alarmData.getRaisedBy();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alarmCategory == null) ? 0 : alarmCategory.hashCode());
        result = prime * result + ((alarmData == null) ? 0 : alarmData.hashCode());
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
        AlarmArchiveRequest other = (AlarmArchiveRequest) obj;
        if (alarmCategory != other.alarmCategory)
            return false;
        if (alarmData == null) {
            if (other.alarmData != null)
                return false;
        } else if (!alarmData.equals(other.alarmData))
            return false;
        if (dataPointId != other.dataPointId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("AlarmArchiveRequest [alarmCategory=%s, dataPointId=%s, alarmData=%s]", alarmCategory,
            dataPointId, alarmData);
    }
}