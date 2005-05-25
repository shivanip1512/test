package com.cannontech.tools.email;

import java.util.Date;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.roles.yukon.SystemRole;

/**
 * This class is a simple wrapper for the javax mail API.
 * It differes from the EmailMessage class in this class because
 * it doesn't handle attachments or multiple recipients. Instead,
 * it allows the same object to be used over and over by setting
 * a new recipient.
 */
public class SimpleEmailMessage {
    private Message _message;

    public SimpleEmailMessage() throws MessagingException {
        java.util.Properties systemProps = System.getProperties();
        
        //a property used internally by the JavaMail API
        String smtpServer = RoleFuncs.getGlobalPropertyValue( SystemRole.SMTP_HOST );
        if( smtpServer == null ) {
            throw new MessagingException("No SMTP_HOST server defined in SystemRole.");
        }
        systemProps.put("mail.smtp.host", smtpServer);

        Session session = Session.getInstance(systemProps);
        
        _message = new MimeMessage(session);
        _message.setHeader("X-Mailer", "CannontechEmail");
        
        String from = RoleFuncs.getGlobalPropertyValue( SystemRole.MAIL_FROM_ADDRESS );
        if (from == null) {
            throw new MessagingException("No MAIL_FROM_ADDRESS defined in SystemRole.");
        }
        _message.setFrom(new InternetAddress(from));

    }
    
    public void setRecipient(String address) throws MessagingException {
        InternetAddress addr = new InternetAddress(address);
        _message.setRecipient(Message.RecipientType.TO, addr);
    }
    
    public void setSubject(String subject) throws MessagingException {
        _message.setSubject(subject);
    }
    
    public void setBody(String body) throws MessagingException {
        _message.setText(body);
    }
    
    public void send() throws MessagingException {
        _message.setSentDate(new Date());
        Transport.send(_message);
    }
}
