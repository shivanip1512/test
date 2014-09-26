package com.cannontech.amr.moveInMoveOut.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.core.dynamic.PointValueHolder;

public class MoveOutResult{
    private Date moveOutDate;
    private PointValueHolder calculatedReading;
    private PointValueHolder currentReading;
    private PointValueHolder calculatedDifference;
    private PlcMeter previousMeter;
    private Set<DeviceAttributeReadError> errors = new HashSet<DeviceAttributeReadError>();    //contains device read errors 
    private List<DeviceGroup> deviceGroupsAdded = new ArrayList<DeviceGroup>();
    private String emailAddress;
    private String submissionType;
    private boolean scheduled = false;
    private String errorMessage;    //contains overall results error message
    
    public PointValueHolder getCalculatedDifference() {
        return calculatedDifference;
    }
    public void setCalculatedDifference(PointValueHolder calculatedDifference) {
        this.calculatedDifference = calculatedDifference;
    }
    public PointValueHolder getCalculatedReading() {
        return calculatedReading;
    }
    public void setCalculatedReading(PointValueHolder calculatedReading) {
        this.calculatedReading = calculatedReading;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public Set<DeviceAttributeReadError> getErrors() {
        return errors;
    }
    public void setErrors(Set<DeviceAttributeReadError> errors) {
        this.errors = errors;
    }
    public PlcMeter getPreviousMeter() {
        return previousMeter;
    }
    public void setPreviousMeter(PlcMeter previousMeter) {
        this.previousMeter = previousMeter;
    }
    public PointValueHolder getCurrentReading() {
        return currentReading;
    }
    public void setCurrentReading(PointValueHolder currentReading) {
        this.currentReading = currentReading;
    }
    public boolean isScheduled() {
        return scheduled;
    }
    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }
    public String getSubmissionType() {
        return submissionType;
    }
    public void setSubmissionType(String submissionType) {
        this.submissionType = submissionType;
    }
    public List<DeviceGroup> getDeviceGroupsAdded() {
        return deviceGroupsAdded;
    }
    public void setDeviceGroupsAdded(List<DeviceGroup> deviceGroupsAdded) {
        this.deviceGroupsAdded = deviceGroupsAdded;
    }
    public Date getMoveOutDate() {
        return moveOutDate;
    }
    public void setMoveOutDate(Date moveOutDate) {
        this.moveOutDate = moveOutDate;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}