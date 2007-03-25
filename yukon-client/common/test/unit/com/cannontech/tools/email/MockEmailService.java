package com.cannontech.tools.email;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;


public class MockEmailService implements EmailService {
    private List<EmailMessageHolder> sentMessages = new ArrayList<EmailMessageHolder>();
    
    public void sendMessage(EmailMessageHolder message) throws MessagingException {
        sentMessages.add(message);
    }

    public List<EmailMessageHolder> getSentMessages() {
        return sentMessages;
    }

}
