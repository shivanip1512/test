package com.cannontech.messaging.message.notif;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.messaging.message.BaseMessage;

/**
 * This is a replacement for the old CtiEmailMsg and should go away someday. Don't use this for new code! Use the base
 * classes message string to give a description of the error.
 */
public class CustomerEmailMessage extends BaseMessage {

    // if notifGroupId != CtiUtilities.NONE_ZERO_ID, then all valid values for the group is used in the email
    private int customerId = CtiUtilities.NONE_ZERO_ID;

    private String subject = ""; // email subject text
    private String body = ""; // email body text

    public String getBody() {
        return body;
    }

    public String getSubject() {
        return subject;
    }

    public void setBody(String string) {
        body = string;
    }

    public void setSubject(String string) {
        subject = string;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

}
