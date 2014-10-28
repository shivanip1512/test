package com.cannontech.tools.email;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

public class MockEmailService implements EmailService {
    private List<EmailMessage> sentMessages = new ArrayList<EmailMessage>();
    
    public List<EmailMessage> getSentMessages() {
        return sentMessages;
    }
    
    @Override
    public void sendMessage(EmailMessage message) throws MessagingException {
        sentMessages.add(message);
    }

}