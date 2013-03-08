package com.cannontech.tools.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;

import com.google.common.collect.Lists;

public class MockEmailService implements EmailService {
    private List<EmailMessageHolder> sentMessages = new ArrayList<EmailMessageHolder>();
    
    @Override
    public void sendMessage(EmailMessageHolder message) throws MessagingException {
        sentMessages.add(message);
    }
    
    @Override
    public void sendHTMLMessage(EmailMessageHolder message) throws MessagingException {
        sentMessages.add(message);
    }

    public List<EmailMessageHolder> getSentMessages() {
        return sentMessages;
    }
    
    private String getRecipients(List<InternetAddress> recipients) {
        StringBuilder result = new StringBuilder();
        boolean isFirst = true;
        for (InternetAddress recipient : recipients) {
            if (isFirst) {
                isFirst = false;
            } else {
                result.append(",");
            }
            result.append(recipient.getAddress());
        }

        return result.toString();
    }

    @Override
    public void sendMessage(EmailServiceMessage data) throws MessagingException {
        List<InternetAddress> allAddresses = Lists.newArrayList(data.getTo());
        allAddresses.addAll(Lists.newArrayList(data.getCc()));
        allAddresses.addAll(Lists.newArrayList(data.getBcc()));
        
        String allRecipients = getRecipients(allAddresses);
        
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < data.getBody().getCount(); i++) {
            MimeBodyPart bodyPart = (MimeBodyPart) data.getBody().getBodyPart(i);
            try {
                builder.append(bodyPart.getContent());
            } catch (IOException e) {
                // it failed, I guess.
                System.out.println("Exception caught in sendMessage(EmailServiceMessage data)");
            }
        }
        
        EmailMessageHolder message = new DefaultEmailMessage(allRecipients, data.getSubject(), builder.toString());
        sentMessages.add(message);
    }
    
    @Override
    public void sendAttachmentMessage(EmailAttachmentMessageHolder message) throws MessagingException {
        sentMessages.add(message);
    }

    @Override
    public void send(EmailMessage emailMessage) throws MessagingException {
        EmailMessageHolder message = 
            new DefaultEmailMessage(
                emailMessage.getTo(), 
                emailMessage.getSubject(), 
                emailMessage.getBody());
        
        sentMessages.add(message);
    }

}
