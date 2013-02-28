package com.cannontech.tools.email;

import javax.mail.MessagingException;

public interface EmailService {
    public void sendMessage(EmailMessageHolder message) throws MessagingException;
    public void sendAttachmentMessage(EmailAttachmentMessageHolder message) throws MessagingException;
    public void sendHTMLMessage(EmailMessageHolder message) throws MessagingException;
    
    /**
     * Send a message using a timestamp of now. 
     * @param data the {@link EmailServiceMessage} object containing the message information.
     */
    public void sendMessage(EmailServiceMessage data) throws MessagingException;
    
    public void send(EmailMessage emailMessage) throws MessagingException;
}
