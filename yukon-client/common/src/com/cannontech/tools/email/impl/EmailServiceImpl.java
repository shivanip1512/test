package com.cannontech.tools.email.impl;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.CharArrayDataSource;
import com.cannontech.tools.email.EmailAttachmentMessageHolder;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.tools.email.EmailMessageHolder;
import com.cannontech.tools.email.EmailService;
import com.cannontech.tools.email.EmailServiceMessage;

public class EmailServiceImpl implements EmailService {
    private static final Logger log = YukonLogManager.getLogger(EmailServiceImpl.class);
    
    @Autowired private GlobalSettingDao globalSettingDao;
    
    @Override
    public void sendMessage(EmailMessageHolder holder) throws MessagingException {
        MimeMessage message = prepareMessage(holder);
        message.setText(holder.getBody());
        send(message);
    }
    
    @Override
    public void sendHTMLMessage(EmailMessageHolder holder) throws MessagingException {
        MimeMessage message = prepareMessage(holder);
        
        Multipart mp = new MimeMultipart();
        
        MimeBodyPart plain_part = new MimeBodyPart();
        plain_part.setContent(holder.getBody(), "text/plain");

        MimeBodyPart html_part = new MimeBodyPart();
        html_part.setContent(holder.getHtmlBody(), "text/html");
        
        mp.addBodyPart(html_part);
        mp.addBodyPart(plain_part);
        
        message.setContent(mp);
        
        send(message);
    }
    
    @Override
    public void sendAttachmentMessage(EmailAttachmentMessageHolder holder)
        throws MessagingException {
        MimeMessage message = prepareMessage(holder);
        
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
        
        message.setContent(mp);
        
        send(message);
    }
    
    @Override
    public void sendMessage(EmailServiceMessage data) throws MessagingException {
        MimeMessage message = new MimeMessage(getSession());
        message.setHeader("X-Mailer", "CannontechEmail");
        
        InternetAddress fromAddress = 
                (data.getFrom() == null) ? getGlobalSettingsFromAddress() : data.getFrom();
        message.setFrom(fromAddress);
        
        // Add the recipients.
        if (data.getTo() != null) {
            message.setRecipients(RecipientType.TO, data.getTo());
        }
        
        if (data.getCc() != null) {
            message.setRecipients(RecipientType.CC, data.getCc());
        }
        
        if (data.getBcc() != null) {
            message.setRecipients(RecipientType.BCC, data.getBcc());
        }
        
        String subjectStr = StringUtils.isNotBlank(data.getSubject()) ? data.getSubject() : "(none)";
        message.setSubject(subjectStr);
        
        if (data.getBody() != null) {
            message.setContent(data.getBody());
        } else {
            // Nothing to add to the message? What do we do here?
            message.setText("This message has no content.");
        }
        
        // Set the send date to now.
        message.setSentDate(new Date());
        
        // Ready to go, send the message.
        send(message);
    }
    
    @Override
    public void send(EmailMessage emailMessage) throws MessagingException {
        MimeMultipart multiPart = new MimeMultipart();
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(emailMessage.getBody());
        multiPart.addBodyPart(bodyPart);
        
        for (int i = 0; i < emailMessage.getAttachments().size(); i++) {
            CharArrayDataSource ds = new CharArrayDataSource(emailMessage.getAttachments().get(i), emailMessage.getAttachmentNames().get(i), "");
            
            bodyPart = new MimeBodyPart();
            bodyPart.setDataHandler(new DataHandler(ds));
            bodyPart.setFileName(ds.getName());
            multiPart.addBodyPart(bodyPart);
        }
        
        for (DataSource ds : emailMessage.getDSAttachments()) {
            bodyPart = new MimeBodyPart();
            bodyPart.setDataHandler(new DataHandler(ds));
            bodyPart.setFileName(ds.getName());
            multiPart.addBodyPart(bodyPart);
        }
        
        sendMessage(new EmailServiceMessage(
                        new InternetAddress(emailMessage.getFrom()), 
                        InternetAddress.parse(emailMessage.getTo()), 
                        InternetAddress.parse(emailMessage.getTo_CC()), 
                        InternetAddress.parse(emailMessage.getTo_BCC()), 
                        emailMessage.getSubject(), 
                        multiPart));
    }
    
    /**
     * Attempts to get any authentication information required for the SMTP server and
     * send the email message. No modification is done to the message itself.
     * @param message - The email message being sent.
     * @throws MessagingException if a problem occurs while attempting to connect to the
     * SMTP server or send the message.
     */
    private void send(final MimeMessage message) throws MessagingException {
        SmtpAuthenticator authenticator = new SmtpAuthenticator();
        PasswordAuthentication authentication = authenticator.getPasswordAuthentication();
        
        if (authentication != null) {
            Transport transport = getSession().getTransport("smtps");
            String username = authentication.getUserName();
            String password = authentication.getPassword();

            String host = getRequiredGlobalSettingsString(GlobalSettingType.SMTP_HOST);
            Integer portNum = globalSettingDao.getInteger(GlobalSettingType.SMTP_PORT);
            if (portNum != null) {
                transport.connect(host, portNum, username, password);
            } else {
                transport.connect(host, username, password);
            }
            
            transport.sendMessage(message, message.getAllRecipients());
        } else {
            Transport.send(message);
        }
    }
    
    /**
     * Builds up the session required for sending a message, including the SMTP
     * authentication info.
     * @return the populated Session object.
     * @throws MessagingException if no SMTP host value is defined in GlobalSettings
     */
    private Session getSession() throws MessagingException {
        Properties properties = new Properties();
        
        String smtpHost = getRequiredGlobalSettingsString(GlobalSettingType.SMTP_HOST);
        properties.put("mail.smtp.host", smtpHost);
        
        Integer smtpPort = globalSettingDao.getInteger(GlobalSettingType.SMTP_PORT);
        if (smtpPort != null) {
            properties.put("mail.smtp.port", smtpPort.toString());
        }
        
        SmtpAuthenticator authenticator = new SmtpAuthenticator();
        if (authenticator.getPasswordAuthentication() != null) {
            // Make sure we use authentication.
            properties.put("mail.smtp.auth", "true");
            
            return Session.getInstance(properties, authenticator);
        }
        
        return Session.getInstance(properties);
    }
    
    /**
     * This class grabs the SMTP username and password from GlobalSettings and holds
     * onto them for use in the {@link Session#getInstance(Properties, Authenticator)}
     * call when the Session for sending emails is retrieved.
     */
    private class SmtpAuthenticator extends Authenticator {
        private PasswordAuthentication authentication = null;
        
        public SmtpAuthenticator() {
            String username = globalSettingDao.getString(GlobalSettingType.SMTP_USERNAME);
            String password = globalSettingDao.getString(GlobalSettingType.SMTP_PASSWORD);
            
            if (!StringUtils.isBlank(username) && !StringUtils.isBlank(password)) {
                log.debug("STMP username and password");
                authentication = new PasswordAuthentication(username, password);
            }
        }
        
        protected PasswordAuthentication getPasswordAuthentication() {
            return authentication;
        }
    }

    /**
     * Attempts to get the value of a GlobalSettings value that should exist.
     * @param type - the GlobalSettingType being retrieved
     * @return the string value associated with the given type
     * @throws MessagingException if no value is found for the given type
     */
    private String getRequiredGlobalSettingsString(GlobalSettingType type) throws MessagingException {
        String value = globalSettingDao.getString(type);

        if (value == null) {
            // if this occurs there is either an issue with the GlobalSettingsDao or no default is set in GlobalSetting enum.
            throw new MessagingException("No " + type.name() + " defined in the GlobalSettings table in the database.");
        }
        
        return value;
    }
    
    /**
     * Shortcut to get the Global Settings "From" address.
     * @return the from address, if it exists.
     * @throws AddressException if the retrieved address cannot be correctly parsed
     * @throws MessagingException if the mail from address is not defined in Global Settings
     */
    private InternetAddress getGlobalSettingsFromAddress() throws AddressException, MessagingException {
        return new InternetAddress(getRequiredGlobalSettingsString(GlobalSettingType.MAIL_FROM_ADDRESS));
    }
    
    /**
     * Return a MimeMessage populated with the data from the provided EmailMessageHolder
     * @param holder the EmailMessageHolder containing the data used to populate the message
     * @return a populated message ready to be sent
     * @throws MessagingException if a problem occurs while populating the message
     */
    private MimeMessage prepareMessage(EmailMessageHolder holder) throws MessagingException {
        MimeMessage message = new MimeMessage(getSession());
        
        message.setHeader("X-Mailer", "CannontechEmail");
        message.setFrom(getGlobalSettingsFromAddress());
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(holder.getRecipient()));
        message.setSubject(holder.getSubject());
        message.setSentDate(new Date());
        
        return message;
    }
}
