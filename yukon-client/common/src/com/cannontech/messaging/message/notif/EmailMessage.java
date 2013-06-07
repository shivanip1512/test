package com.cannontech.messaging.message.notif;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.messaging.message.BaseMessage;

/**
 * Use the base classes message string to give a description of the error.
 */
public class EmailMessage extends BaseMessage {

    private String to = ""; // a comma separated string of email addresses.

    // if notifGroupId != CtiUtilities.NONE_ZERO_ID, then all valid values for the group is used in the email
    private int notifGroupId = CtiUtilities.NONE_ZERO_ID; // a notification group id

    private String subject = ""; // email subject text
    private String body = ""; // email body text

    private String to_CC = ""; // a comma separated string of email addresses for CC
    private String to_BCC = ""; // a comma separated string of email addresses for BC

    public String getBody() {
        return body;
    }

    public String getSubject() {
        return subject;
    }

    public String getTo() {
        return to;
    }

    public String getTo_BCC() {
        return to_BCC;
    }

    public String getTo_CC() {
        return to_CC;
    }

    public void setBody(String string) {
        body = string;
    }

    public void setSubject(String string) {
        subject = string;
    }

    public void setTo(String string) {
        to = string;
    }

    public void setTo_BCC(String string) {
        to_BCC = string;
    }

    public void setTo_CC(String string) {
        to_CC = string;
    }

    public int getNotifGroupId() {
        return notifGroupId;
    }

    public void setNotifGroupId(int i) {
        notifGroupId = i;
    }

    @Override
    public String toString() {
        return String.format("NotifEmailMessage [notifGroupId=%s, subject=%s, parent=%s]", notifGroupId, subject,
                             super.toString());
    }
}
