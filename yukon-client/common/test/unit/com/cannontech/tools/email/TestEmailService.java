package com.cannontech.tools.email;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;


public class TestEmailService implements EmailService {
    private List<TestMessage> sentMessages = new ArrayList<TestMessage>();
    
    public final class TestMessage implements EmailMessageHolder {
        public String body;
        public String address;
        public String subject;

        public void setBody(String body) throws MessagingException {
            this.body = body;
        }

        public void setRecipient(String address) throws MessagingException {
            this.address = address;
        }

        public void setSubject(String subject) throws MessagingException {
            this.subject = subject;
        }
    }

    public EmailMessageHolder createMessage() {
        return new TestMessage();
    }

    public void sendMessage(EmailMessageHolder message) throws MessagingException {
        sentMessages.add((TestMessage)message);
    }

    public List<TestMessage> getSentMessages() {
        return sentMessages;
    }

}
