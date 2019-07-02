package com.cannontech.common.dr.program.setup.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Notification {

    private Integer notifyActiveOffset;
    private Integer notifyInactiveOffset;
    private Boolean notifyAdjust;
    private Boolean enableSchedule;
    @JsonInclude(Include.NON_EMPTY)
    private List<NotificationGroup> assignedNotificationGroups;

    public Integer getNotifyActiveOffset() {
        return notifyActiveOffset;
    }

    public void setNotifyActiveOffset(Integer notifyActiveOffset) {
        this.notifyActiveOffset = notifyActiveOffset;
    }

    public Integer getNotifyInactiveOffset() {
        return notifyInactiveOffset;
    }

    public void setNotifyInactiveOffset(Integer notifyInactiveOffset) {
        this.notifyInactiveOffset = notifyInactiveOffset;
    }

    public Boolean getNotifyAdjust() {
        return notifyAdjust;
    }

    public void setNotifyAdjust(Boolean notifyAdjust) {
        this.notifyAdjust = notifyAdjust;
    }

    public Boolean getEnableSchedule() {
        return enableSchedule;
    }

    public void setEnableSchedule(Boolean enableSchedule) {
        this.enableSchedule = enableSchedule;
    }

    public List<NotificationGroup> getAssignedNotificationGroups() {
        return assignedNotificationGroups;
    }

    public void setAssignedNotificationGroups(List<NotificationGroup> assignedNotificationGroups) {
        this.assignedNotificationGroups = assignedNotificationGroups;
    }

}
