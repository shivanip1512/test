package com.cannontech.tools.email.impl;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.tools.email.EmailMessageHolder;
import com.cannontech.tools.email.EmailService;
import com.cannontech.tools.email.SimpleEmailMessage;

public class EmailServiceImpl implements EmailService {
    private RoleDao roleDao;

    public EmailMessageHolder createMessage() throws MessagingException {
        java.util.Properties systemProps = System.getProperties();
        
        //a property used internally by the JavaMail API
        String smtpServer = DaoFactory.getRoleDao().getGlobalPropertyValue( SystemRole.SMTP_HOST );
        if( smtpServer == null ) {
            throw new MessagingException("No SMTP_HOST server defined in SystemRole.");
        }
        systemProps.put("mail.smtp.host", smtpServer);

        Session session = Session.getInstance(systemProps);
        
        MimeMessage _message = new MimeMessage(session);
        _message.setHeader("X-Mailer", "CannontechEmail");
        
        String from = DaoFactory.getRoleDao().getGlobalPropertyValue( SystemRole.MAIL_FROM_ADDRESS );
        if (from == null) {
            throw new MessagingException("No MAIL_FROM_ADDRESS defined in SystemRole.");
        }
        _message.setFrom(new InternetAddress(from));
        
        return new SimpleEmailMessage(_message);
    }

    public void sendMessage(EmailMessageHolder message) throws MessagingException {
        if (message instanceof SimpleEmailMessage) {
            SimpleEmailMessage email = (SimpleEmailMessage) message;
            email.send();
        }
    }

    @Required
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

}
