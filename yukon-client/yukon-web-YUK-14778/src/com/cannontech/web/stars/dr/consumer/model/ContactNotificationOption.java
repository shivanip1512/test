package com.cannontech.web.stars.dr.consumer.model;

import com.cannontech.i18n.YukonMessageSourceResolvable;

/**
 * Class which represents a contact notification drop down option
 */
public class ContactNotificationOption {

    private int notificationId;
    private YukonMessageSourceResolvable optionText;

    public ContactNotificationOption(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public YukonMessageSourceResolvable getOptionText() {
        return optionText;
    }

    public void setOptionText(YukonMessageSourceResolvable optionText) {
        this.optionText = optionText;
    }

}
