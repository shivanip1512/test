package com.cannontech.tools.email.impl;

import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.system.YukonSetting;
import com.cannontech.system.dao.YukonSettingsDao;
import com.cannontech.tools.email.EmailAttachmentMessageHolder;
import com.cannontech.tools.email.EmailMessageHolder;
import com.cannontech.tools.email.EmailService;

public class EmailServiceImpl implements EmailService {
    @Autowired private YukonSettingsDao YukonSettingsDao;

    public void sendMessage(EmailMessageHolder holder) throws MessagingException {
        
        MimeMessage _message = prepareMessage(holder);
        _message.setText(holder.getBody());
        send(_message);
    }
    
    public void sendHTMLMessage(EmailMessageHolder holder) throws MessagingException {
        
        MimeMessage _message = prepareMessage(holder);
        
        Multipart mp = new MimeMultipart();
        
        MimeBodyPart plain_part = new MimeBodyPart();
        plain_part.setContent(holder.getBody(), "text/plain");
        
        MimeBodyPart html_part = new MimeBodyPart();
        html_part.setContent(holder.getHtmlBody(), "text/html");
        
        mp.addBodyPart(html_part);
        mp.addBodyPart(plain_part);
        
        
        _message.setContent(mp);
        
        send(_message);
    }
    
    @Override
    public void sendAttachmentMessage(EmailAttachmentMessageHolder holder)
        throws MessagingException {
        MimeMessage _message = prepareMessage(holder);
        
        Multipart mp = new MimeMultipart();
        
        MimeBodyPart plain_part = new MimeBodyPart();
        plain_part.setContent(holder.getBody(), "text/plain");
        
        String htmlBody = holder.getHtmlBody();
        if (htmlBody != null) {
            MimeBodyPart html_part = new MimeBodyPart();
            html_part.setContent(htmlBody, "text/html");
            mp.addBodyPart(html_part);
        }
        
        mp.addBodyPart(plain_part);
        
        for (DataSource dataSource : holder.getAttachments()) {
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(new DataHandler(dataSource));
            messageBodyPart.setFileName(dataSource.getName());
            mp.addBodyPart(messageBodyPart);
        }
        
        _message.setContent(mp);
        
        send(_message);
        
    }
    
    private void send(MimeMessage _message) throws MessagingException{
        Transport.send(_message);
    }
    
    private MimeMessage prepareMessage(EmailMessageHolder holder) throws MessagingException {
        java.util.Properties systemProps = System.getProperties();
        
        //a property used internally by the JavaMail API
        String smtpServer = YukonSettingsDao.getSettingStringValue(YukonSetting.SMTP_HOST);
        if( smtpServer == null ) {
            throw new MessagingException("No SMTP_HOST server defined in SystemRole.");
        }
        systemProps.put("mail.smtp.host", smtpServer);

        Session session = Session.getInstance(systemProps);
        
        MimeMessage _message = new MimeMessage(session);
        _message.setHeader("X-Mailer", "CannontechEmail");
        String from = YukonSettingsDao.getSettingStringValue(YukonSetting.MAIL_FROM_ADDRESS);

        if (from == null) {
            throw new MessagingException("No MAIL_FROM_ADDRESS defined in SystemRole.");
        }
        _message.setFrom(new InternetAddress(from));
        
        InternetAddress addr = new InternetAddress(holder.getRecipient());
        _message.setRecipient(Message.RecipientType.TO, addr);
        
        _message.setSubject(holder.getSubject());
        
        _message.setSentDate(new Date());
        
        return _message;
    }
}
