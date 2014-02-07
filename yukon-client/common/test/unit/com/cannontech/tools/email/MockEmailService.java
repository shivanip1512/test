package com.cannontech.tools.email;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

public class MockEmailService implements EmailService {
    private List<EmailServiceMessage> sentMessages = new ArrayList<EmailServiceMessage>();
    
    public List<EmailServiceMessage> getSentMessages() {
        return sentMessages;
    }
    
    @Override
    public void sendMessage(EmailServiceMessage message) throws MessagingException {
        sentMessages.add(message);
    }
}