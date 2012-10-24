package com.cannontech.dr.dao;

import java.io.Serializable;

public class SepReportedAddress extends LmReportedAddress implements Serializable {

    private int utilityEnrollmentGroup;
    private int randomStartTimeMinutes;
    private int randomStopTimeMinutes;
    private int deviceClass;
    
    public int getUtilityEnrollmentGroup() {
        return utilityEnrollmentGroup;
    }
    
    public void setUtilityEnrollmentGroup(int utilityEnrollmentGroup) {
        this.utilityEnrollmentGroup = utilityEnrollmentGroup;
    }
    
    public int getRandomStartTimeMinutes() {
        return randomStartTimeMinutes;
    }
    
    public void setRandomStartTimeMinutes(int randomStartTimeMinutes) {
        this.randomStartTimeMinutes = randomStartTimeMinutes;
    }
    
    public int getRandomStopTimeMinutes() {
        return randomStopTimeMinutes;
    }
    
    public void setRandomStopTimeMinutes(int randomStopTimeMinutes) {
        this.randomStopTimeMinutes = randomStopTimeMinutes;
    }
    
    public int getDeviceClass() {
        return deviceClass;
    }
    
    public void setDeviceClass(int deviceClass) {
        this.deviceClass = deviceClass;
    }
    
    /**
     * Used to decide if the currently stored address is the same
     * as this one.  Addresses should only be persisted to the db
     * when they have changed since this will get reported every 
     * interval.  
     * 
     *  The main difference between this and {@link #equals(Object)}
     *  is that it ignores {@link #changeId} and {@link #timestamp}.
     */
    public boolean isEquivalent(SepReportedAddress other) {
        if (deviceClass != other.deviceClass)
            return false;
        if (deviceId != other.deviceId)
            return false;
        if (randomStartTimeMinutes != other.randomStartTimeMinutes)
            return false;
        if (randomStopTimeMinutes != other.randomStopTimeMinutes)
            return false;
        if (utilityEnrollmentGroup != other.utilityEnrollmentGroup)
            return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + changeId;
        result = prime * result + deviceClass;
        result = prime * result + deviceId;
        result = prime * result + randomStartTimeMinutes;
        result = prime * result + randomStopTimeMinutes;
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        result = prime * result + utilityEnrollmentGroup;
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
        SepReportedAddress other = (SepReportedAddress) obj;
        if (changeId != other.changeId)
            return false;
        if (deviceClass != other.deviceClass)
            return false;
        if (deviceId != other.deviceId)
            return false;
        if (randomStartTimeMinutes != other.randomStartTimeMinutes)
            return false;
        if (randomStopTimeMinutes != other.randomStopTimeMinutes)
            return false;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        if (utilityEnrollmentGroup != other.utilityEnrollmentGroup)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
            .format("SepReportedAddress [changeId=%s, deviceId=%s, timestamp=%s, utilityEnrollmentGroup=%s, randomStartTimeMinutes=%s, randomStopTimeMinutes=%s, deviceClass=%s]",
                    changeId,
                    deviceId,
                    timestamp,
                    utilityEnrollmentGroup,
                    randomStartTimeMinutes,
                    randomStopTimeMinutes,
                    deviceClass);
    }
    
}