package com.cannontech.tools.email;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CompletionCallback;

public class EmailCompletionCallback implements CompletionCallback {
    private Logger log = YukonLogManager.getLogger(EmailCompletionCallback.class);
    private EmailService emailService;
    private EmailMessageHolder successMessage = new DefaultEmailMessage();
    private EmailMessageHolder failureMessage = new DefaultEmailMessage();

    public EmailCompletionCallback(EmailService emailService) {
        this.emailService = emailService;
    }
    
    public void setSuccessMessage(EmailMessageHolder message) {
        this.successMessage = message;
    }
    
    public void setFailureMessage(EmailMessageHolder message) {
        this.failureMessage = message;
    }

    public void onFailure() {
        try {
            emailService.sendMessage(failureMessage);
        } catch (MessagingException e) {
            log.error("Unable to send onFailure message",e);
        }
    }

    public void onSuccess() {
        try {
            emailService.sendMessage(successMessage);
        } catch (MessagingException e) {
            log.error("Unable to send onSuccess message",e);
        }
    }
    
    public String toString(){
        return successMessage.getRecipient();
    }

}
