package com.cannontech.tools.email;

import javax.mail.MessagingException;

public interface EmailService {
    public EmailMessageHolder createMessage() throws MessagingException;
    public void sendMessage(EmailMessageHolder message) throws MessagingException;
}
