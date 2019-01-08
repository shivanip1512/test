package com.cannontech.common.rfn.message.alarm;

import java.io.Serializable;
import java.util.Map;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class AlarmData implements Serializable {

    private static final long serialVersionUID = 1L;

    // Extend RfnIdentifier to cover Network Manager.
    //         Gateway:                GATEWAY.gwSN,   "CPS", "RFGateway" or "RFGateway2"
    // Network Manager: NETWORK_INSTANCE.instanceSN, "EATON", "NetworkManager"
    // For GW alarms, raisedBy and appliedTo are the same so far.
    // For NM alarms, they may be different, for example,
    //     a Gateway certificate expiration alarm is raised by NM but is applied to a particular Gateway
    private RfnIdentifier raisedBy;
    private RfnIdentifier appliedTo;

    // The seqNumber is increased per raisedBy RfnIdentifier.
    // For GW alarms, seqNumber can be recycled. The gwSeqNumber in GBP is uint32.
    // For NM alarms, seqNumber is an ever increased number.
    private long seqNumber;
    
    // The system time in milliseconds since the Unix epoch when the alarm is generated.
    // Since the seqNumber may be recycled for GW alarms, to uniquely identified an alarm,
    //     you need to combine raisedBy, seqNumber and timeStamp.
    // Network Manager will guarantee no duplicates to Yukon.
    private long timeStamp; 

    // alarmCodeID is defined in the alarm template decided by the raisedBy RfnIdentifier.
    // For example, AlarmTemplate_CPS_Gateway2 (Gateway 2.0) defines all six Gateway alarm types;
    // AlarmTemplate_CPS_Gateway (Gateway 1.5) may only support a subset of them;
    // AlarmTemplate_Eaton_NetworkManager will have its own set of NM alarm types.
    // alarmCodeID is only unique within each template.
    private long alarmCodeID;
    
    private AlarmState alarmState; // assert or clear
    
    private String description; // can be null or empty.
  
    // Additional parameters to be added to a specific alarm assert/clear
    //      Map<alarmParamCodeID, alarmParamValue>
    // Current GBP only defines one parameter type: 0 - Description
    private Map<Short, byte[]> additionalAlarmParams;
        
    public RfnIdentifier getRaisedBy() {
        return raisedBy;
    }
    public void setRaisedBy(RfnIdentifier raisedBy) {
        this.raisedBy = raisedBy;
    }
    public long getSeqNumber() {
        return seqNumber;
    }
    public void setSeqNumber(long seqNumber) {
        this.seqNumber = seqNumber;
    }
    public long getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    public long getAlarmCodeID() {
        return alarmCodeID;
    }
    public void setAlarmCodeID(long alarmCodeID) {
        this.alarmCodeID = alarmCodeID;
    }
    public AlarmState getAlarmState() {
        return alarmState;
    }
    public void setAlarmState(AlarmState alarmState) {
        this.alarmState = alarmState;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Map<Short, byte[]> getAdditionalAlarmParams() {
        return additionalAlarmParams;
    }
    public void setAdditionalAlarmParams(Map<Short, byte[]> additionalAlarmParams) {
        this.additionalAlarmParams = additionalAlarmParams;
    }
    public RfnIdentifier getAppliedTo() {
        return appliedTo;
    }
    public void setAppliedTo(RfnIdentifier appliedTo) {
        this.appliedTo = appliedTo;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((additionalAlarmParams == null) ? 0 : additionalAlarmParams.hashCode());
        result = prime * result + (int) (alarmCodeID ^ (alarmCodeID >>> 32));
        result = prime * result + ((alarmState == null) ? 0 : alarmState.hashCode());
        result = prime * result + ((appliedTo == null) ? 0 : appliedTo.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((raisedBy == null) ? 0 : raisedBy.hashCode());
        result = prime * result + (int) (seqNumber ^ (seqNumber >>> 32));
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
        AlarmData other = (AlarmData) obj;
        if (additionalAlarmParams == null) {
            if (other.additionalAlarmParams != null)
                return false;
        } else if (!additionalAlarmParams.equals(other.additionalAlarmParams))
            return false;
        if (alarmCodeID != other.alarmCodeID)
            return false;
        if (alarmState != other.alarmState)
            return false;
        if (appliedTo == null) {
            if (other.appliedTo != null)
                return false;
        } else if (!appliedTo.equals(other.appliedTo))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (raisedBy == null) {
            if (other.raisedBy != null)
                return false;
        } else if (!raisedBy.equals(other.raisedBy))
            return false;
        if (seqNumber != other.seqNumber)
            return false;
        if (timeStamp != other.timeStamp)
            return false;
        return true;
    }
    @Override
    public String toString() {
        return String.format(
            "AlarmData [raisedBy=%s, appliedTo=%s, seqNumber=%s, timeStamp=%s, alarmCodeID=%s, alarmState=%s, description=%s, additionalAlarmParams=%s]",
            raisedBy, appliedTo, seqNumber, timeStamp, alarmCodeID, alarmState, description, additionalAlarmParams);
    }
}
