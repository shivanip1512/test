package com.cannontech.amr.moveInMoveOut.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.core.dynamic.PointValueHolder;

public class MoveOutResultObj{
    private Date moveOutDate;
    private PointValueHolder calculatedReading;
    private PointValueHolder currentReading;
    private PointValueHolder calculatedDifference;
    private Meter previousMeter;
    private List<DeviceErrorDescription> errors = new ArrayList<DeviceErrorDescription>(); 
    private List<DeviceGroup> deviceGroupsAdded = new ArrayList<DeviceGroup>();
    private String emailAddress;
    private String submissionType;
    private boolean scheduled = false;
    
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
    public List<DeviceErrorDescription> getErrors() {
        return errors;
    }
    public void setErrors(List<DeviceErrorDescription> errors) {
        this.errors = errors;
    }
    public Meter getPreviousMeter() {
        return previousMeter;
    }
    public void setPreviousMeter(Meter previousMeter) {
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
}