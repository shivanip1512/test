package com.cannontech.notif.handler;

import java.util.Arrays;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.logging.log4j.Logger;
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
        if (emailMessage.getTo() != null) {
            String addresses = Arrays.stream(emailMessage.getTo())
                    .map(InternetAddress::getAddress)
                    .reduce("", (s1,s2) -> s1 + "," + s2);
            log.debug("Sending email to " + addresses);
        }
        if (emailMessage.getBcc() != null) {
            String bccRecipients = Arrays.stream(emailMessage.getBcc())
                    .map(InternetAddress::getAddress)
                    .reduce("", (s1,s2) -> s1 + "," + s2);
            log.debug("Sending email bcc recipients " + bccRecipients);    
        }
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
