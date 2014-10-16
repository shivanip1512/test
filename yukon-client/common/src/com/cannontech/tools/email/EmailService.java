package com.cannontech.tools.email;

import javax.mail.MessagingException;

import com.cannontech.user.YukonUserContext;

public interface EmailService {
    /**
     * Send a message using a timestamp of now. 
     * @param data the {@link EmailMessage} object containing the message information.
     */
    public void sendMessage(EmailMessage data) throws MessagingException;
    
    /**
     * Get the default email address of the logged in user. 
     * @param userContext - Logged in user. 
     * @return email address - Logged in users email address.
     */
    String getUserEmail(YukonUserContext userContext);
    
    /**
     * Check if the SMPT HOST is configured.
     * @return isSmtpConfigured - returns true if SMTP HOST is configured else returns false.
     */
    boolean isSmtpConfigured();
}