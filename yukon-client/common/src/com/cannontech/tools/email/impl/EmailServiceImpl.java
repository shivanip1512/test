package com.cannontech.tools.email.impl;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;

public class EmailServiceImpl implements EmailService {
    private static final Logger log = YukonLogManager.getLogger(EmailServiceImpl.class);
    
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private ContactDao contactDao; 

    @Override
    public void sendMessage(EmailMessage data) throws MessagingException {
        Session session = getSession();
        MimeMessage message = new MimeMessage(session);
        message.setHeader("X-Mailer", "YukonEmail");
        
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
        
        message.setContent(data.createBodyContent());
        
        // Set the send date to now.
        message.setSentDate(new Date());
        
        // Ready to go, send the message.
        send(message, session);
    }
    
    /**
     * Attempts to get any authentication information required for the SMTP server and
     * send the email message. No modification is done to the message itself.
     * @param message - The email message being sent.
     * @throws MessagingException if a problem occurs while attempting to connect to the
     * SMTP server or send the message.
     */
    private void send(final MimeMessage message, Session session) throws MessagingException {
        SmtpAuthenticator authenticator = new SmtpAuthenticator();
        PasswordAuthentication authentication = authenticator.getPasswordAuthentication();
        
        if (authentication != null) {
            Transport transport = session.getTransport("smtps");
            String username = authentication.getUserName();
            String password = authentication.getPassword();

            String host = getRequiredGlobalSettingsString(GlobalSettingType.SMTP_HOST);
            Integer portNum = globalSettingDao.getNullableInteger(GlobalSettingType.SMTP_PORT);
            if (portNum != null) {
                transport.connect(host, portNum, username, password);
            } else {
                transport.connect(host, username, password);
            }
            
//            Doesn't seem to be necessary. API says it should be called to update headers, but it is expensive so only call if needed.
//            message.saveChanges();
            transport.sendMessage(message, message.getAllRecipients());
        } else {
            Transport.send(message);
        }
        log.debug("Message Sent: " + message.toString());
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
        
        Integer smtpPort = globalSettingDao.getNullableInteger(GlobalSettingType.SMTP_PORT);
        if (smtpPort != null) {
            properties.put("mail.smtp.port", smtpPort.toString());
        }
        
        boolean smtpTls = globalSettingDao.getBoolean(GlobalSettingType.SMTP_TLS_ENABLED);
        if (smtpTls) {
            properties.put("mail.smtp.starttls.enable", "true");
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
                log.debug("SMTP username and password");
                authentication = new PasswordAuthentication(username, password);
            }
        }
        
        @Override
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

        if (StringUtils.isBlank(value)) {
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

    @Override
    public String getUserEmail(YukonUserContext userContext) {
        LiteContact contact = yukonUserDao.getLiteContact(userContext.getYukonUser().getUserID());
        String email = "";
        if (contact != null) {
            String[] allEmailAddresses = contactDao.getAllEmailAddresses(contact.getContactID());
            if (allEmailAddresses.length > 0) {
                email = allEmailAddresses[0];
            }
        }
        return email;
    }

    @Override
    public boolean isSmtpConfigured() {
        boolean isSmtpConfigured = true;
        String smtpHost = globalSettingDao.getString(GlobalSettingType.SMTP_HOST);   
        if(StringUtils.isBlank(smtpHost)){
            isSmtpConfigured = false;
        }
        return isSmtpConfigured;
    }
}
