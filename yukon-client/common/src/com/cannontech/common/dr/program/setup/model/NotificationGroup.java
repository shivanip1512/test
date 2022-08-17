package com.cannontech.common.dr.program.setup.model;

public class NotificationGroup {

    private Integer notificationGrpID;
    private String notificationGrpName;

    public NotificationGroup() {
    }

    public NotificationGroup(Integer notificationGrpID, String notificationGrpName) {
        this.notificationGrpID = notificationGrpID;
        this.notificationGrpName = notificationGrpName;
    }

    public Integer getNotificationGrpID() {
        return notificationGrpID;
    }

    public void setNotificationGrpID(Integer notificationGrpID) {
        this.notificationGrpID = notificationGrpID;
    }

    public String getNotificationGrpName() {
        return notificationGrpName;
    }

    public void setNotificationGrpName(String notificationGrpName) {
        this.notificationGrpName = notificationGrpName;
    }

}
