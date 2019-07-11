package com.cannontech.common.dr.program.setup.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Notification {

    private Integer programStartInMinutes;
    private Integer programStopInMinutes;
    private Boolean notifyOnAdjust;
    private Boolean enableOnSchedule;
    @JsonInclude(Include.NON_EMPTY)
    private List<NotificationGroup> assignedNotificationGroups;

    public Integer getProgramStartInMinutes() {
        return programStartInMinutes;
    }

    public void setProgramStartInMinutes(Integer programStartInMinutes) {
        this.programStartInMinutes = programStartInMinutes;
    }

    public Integer getProgramStopInMinutes() {
        return programStopInMinutes;
    }

    public void setProgramStopInMinutes(Integer programStopInMinutes) {
        this.programStopInMinutes = programStopInMinutes;
    }

    public Boolean getNotifyOnAdjust() {
        return notifyOnAdjust;
    }

    public void setNotifyOnAdjust(Boolean notifyOnAdjust) {
        this.notifyOnAdjust = notifyOnAdjust;
    }

    public Boolean getEnableOnSchedule() {
        return enableOnSchedule;
    }

    public void setEnableOnSchedule(Boolean enableOnSchedule) {
        this.enableOnSchedule = enableOnSchedule;
    }

    public List<NotificationGroup> getAssignedNotificationGroups() {
        return assignedNotificationGroups;
    }

    public void setAssignedNotificationGroups(List<NotificationGroup> assignedNotificationGroups) {
        this.assignedNotificationGroups = assignedNotificationGroups;
    }

}
