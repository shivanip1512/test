package com.cannontech.tools.email;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import com.cannontech.user.YukonUserContext;

public class MockEmailService implements EmailService {
    private List<EmailMessage> sentMessages = new ArrayList<EmailMessage>();
    
    public List<EmailMessage> getSentMessages() {
        return sentMessages;
    }
    
    @Override
    public void sendMessage(EmailMessage message) throws MessagingException {
        sentMessages.add(message);
    }

    @Override
    public String getUserEmail(YukonUserContext userContext) {
        return null;
    }

    @Override
    public boolean isSmtpConfigured() {
        return false;
    }
}