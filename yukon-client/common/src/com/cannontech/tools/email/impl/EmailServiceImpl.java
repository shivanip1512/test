package com.cannontech.tools.email.impl;

import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.core.dao.RoleDao;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.tools.email.EmailMessageHolder;
import com.cannontech.tools.email.EmailService;

public class EmailServiceImpl implements EmailService {
    private RoleDao roleDao;

    public void sendMessage(EmailMessageHolder holder) throws MessagingException {
        java.util.Properties systemProps = System.getProperties();
        
        //a property used internally by the JavaMail API
        String smtpServer = roleDao.getGlobalPropertyValue( SystemRole.SMTP_HOST );
        if( smtpServer == null ) {
            throw new MessagingException("No SMTP_HOST server defined in SystemRole.");
        }
        systemProps.put("mail.smtp.host", smtpServer);

        Session session = Session.getInstance(systemProps);
        
        MimeMessage _message = new MimeMessage(session);
        _message.setHeader("X-Mailer", "CannontechEmail");
        
        String from = roleDao.getGlobalPropertyValue( SystemRole.MAIL_FROM_ADDRESS );
        if (from == null) {
            throw new MessagingException("No MAIL_FROM_ADDRESS defined in SystemRole.");
        }
        _message.setFrom(new InternetAddress(from));
        
        InternetAddress addr = new InternetAddress(holder.getRecipient());
        _message.setRecipient(Message.RecipientType.TO, addr);
        
        _message.setSubject(holder.getSubject());
        _message.setText(holder.getBody());
        
        _message.setSentDate(new Date());
        Transport.send(_message);

    }

    @Required
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

}
