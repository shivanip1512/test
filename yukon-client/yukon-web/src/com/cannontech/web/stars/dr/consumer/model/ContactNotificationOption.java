package com.cannontech.web.stars.dr.consumer.model;

import com.cannontech.i18n.YukonMessageSourceResovable;

/**
 * Class which represents a contact notification drop down option
 */
public class ContactNotificationOption {

    private int notificationId;
    private YukonMessageSourceResovable optionText;

    public ContactNotificationOption(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public YukonMessageSourceResovable getOptionText() {
        return optionText;
    }

    public void setOptionText(YukonMessageSourceResovable optionText) {
        this.optionText = optionText;
    }

}
