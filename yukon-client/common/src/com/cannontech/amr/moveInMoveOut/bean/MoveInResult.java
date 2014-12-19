package com.cannontech.amr.moveInMoveOut.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.core.dynamic.PointValueHolder;

public class MoveInResult{

    private Date moveInDate;
    private PointValueHolder currentReading;
    private PointValueHolder calculatedPreviousReading;
    private PointValueHolder calculatedDifference;
    private PlcMeter previousMeter;
    private PlcMeter newMeter;
    private Set<SpecificDeviceErrorDescription> errors = new HashSet<SpecificDeviceErrorDescription>();  //contains device read errors 
    private List<DeviceGroup> deviceGroupsRemoved = new ArrayList<DeviceGroup>();
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
    public PointValueHolder getCalculatedPreviousReading() {
        return calculatedPreviousReading;
    }
    public void setCalculatedPreviousReading(
            PointValueHolder calculatedPreviousReading) {
        this.calculatedPreviousReading = calculatedPreviousReading;
    }
    public PointValueHolder getCurrentReading() {
        return currentReading;
    }
    public void setCurrentReading(PointValueHolder currentReading) {
        this.currentReading = currentReading;
    }
    public PlcMeter getNewMeter() {
        return newMeter;
    }
    public void setNewMeter(PlcMeter newMeter) {
        this.newMeter = newMeter;
    }
    public PlcMeter getPreviousMeter() {
        return previousMeter;
    }
    public void setPreviousMeter(PlcMeter previousMeter) {
        this.previousMeter = previousMeter;
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
    public Set<SpecificDeviceErrorDescription> getErrors() {
        return errors;
    }
    public void setErrors(Set<SpecificDeviceErrorDescription> errors) {
        this.errors = errors;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public List<DeviceGroup> getDeviceGroupsRemoved() {
        return deviceGroupsRemoved;
    }
    public void setDeviceGroupsRemoved(List<DeviceGroup> deviceGroupsRemoved) {
        this.deviceGroupsRemoved = deviceGroupsRemoved;
    }
    public Date getMoveInDate() {
        return moveInDate;
    }
    public void setMoveInDate(Date moveInDate) {
        this.moveInDate = moveInDate;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}