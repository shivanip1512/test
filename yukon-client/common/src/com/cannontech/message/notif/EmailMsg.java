package com.cannontech.message.notif;

import com.cannontech.message.util.Message;
import com.cannontech.tools.email.EmailMessage;

/**
 * A simple, generic wrapper for passing an EmailMessage to the Notification Server.
 */
public class EmailMsg extends Message {
    private EmailMessage message;
    
    public void setMessage(EmailMessage message) {
        this.message = message;
    }
    
    public EmailMessage getMessage() {
        return message;
    }
}
