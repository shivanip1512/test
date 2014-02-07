package com.cannontech.tools.email;

import javax.mail.MessagingException;

public interface EmailService {
    /**
     * Send a message using a timestamp of now. 
     * @param data the {@link EmailServiceMessage} object containing the message information.
     */
    public void sendMessage(EmailServiceMessage data) throws MessagingException;
}