package com.cannontech.tools.email;

import javax.mail.MessagingException;

public interface EmailService {
    public DefaultEmailMessage getEmailer(String email, String subject, String body);
    public void sendMessage(EmailMessageHolder message) throws MessagingException;
    public void sendHTMLMessage(EmailMessageHolder message) throws MessagingException;
}
