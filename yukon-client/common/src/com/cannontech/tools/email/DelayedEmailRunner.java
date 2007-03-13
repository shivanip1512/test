package com.cannontech.tools.email;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

public class DelayedEmailRunner implements Runnable, EmailMessageHolder {
    private final Logger log = YukonLogManager.getLogger(DelayedEmailRunner.class);
    private final EmailService emailService;
    private final EmailMessageHolder message;
    private String address;
    
    public DelayedEmailRunner(EmailService emailService) throws MessagingException {
        this.emailService = emailService;
        this.message = emailService.createMessage();
    }

    public void run() {
        // send message to emailAddress
        try {
            emailService.sendMessage(message);
        } catch (MessagingException e) {
            log.error(e);
        }
    }

    public void setBody(String body) throws MessagingException {
        message.setBody(body);
    }

    public void setRecipient(String address) throws MessagingException {
        this.address = address;
        message.setRecipient(address);
    }

    public void setSubject(String subject) throws MessagingException {
        message.setSubject(subject);
    }
    
    @Override
    public String toString() {
        return address;
    }
}