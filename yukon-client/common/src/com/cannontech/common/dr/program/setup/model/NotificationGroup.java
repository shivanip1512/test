package com.cannontech.common.dr.program.setup.model;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.db.device.lm.LMDirectNotificationGroupList;

public class NotificationGroup implements DBPersistentConverter<LMDirectNotificationGroupList> {

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

    @Override
    public void buildModel(LMDirectNotificationGroupList lmDirectNotificationGroupList) {
        setNotificationGrpID(lmDirectNotificationGroupList.getNotificationGroupID());
    }

    @Override
    public void buildDBPersistent(LMDirectNotificationGroupList lmDirectNotificationGroupList) {
        lmDirectNotificationGroupList.setNotificationGrpID(getNotificationGrpID());
    }

}
