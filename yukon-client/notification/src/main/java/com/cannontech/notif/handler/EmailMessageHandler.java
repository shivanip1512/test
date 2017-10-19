package com.cannontech.notif.handler;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.message.notif.EmailMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.server.NotifServerConnection;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.tools.email.EmailService;

/**
 * Handler for generic plain-text emails.
 */
public class EmailMessageHandler implements MessageHandler<EmailMsg> {
    private static final Logger log = YukonLogManager.getLogger(EmailMessageHandler.class);
    @Autowired EmailService emailService;
    
    @Override
    public void handleMessage(NotifServerConnection connection, Message message) {
        EmailMsg msg = (EmailMsg) message;
        EmailMessage emailMessage = msg.getMessage();
        
        try {
            emailService.sendMessage(emailMessage);
        } catch (MessagingException e) {
            log.error("Unable to send email message: ", e);
        }
    }

    @Override
    public Class<EmailMsg> getSupportedMessageType() {
        return EmailMsg.class;
    }
    
}
