package com.cannontech.tools.email.impl;

import java.util.Date;
import java.util.Map;
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

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationLoader;
import com.cannontech.common.config.SmtpEncryptionType;
import com.cannontech.common.config.SmtpHelper;
import com.cannontech.common.config.SmtpPropertyType;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.tools.email.EmailService;
import com.cannontech.tools.email.EmailSettingsCacheService;
import com.cannontech.tools.email.SystemEmailSettingsType;

public class EmailServiceImpl implements EmailService {
    private static final Logger log = YukonLogManager.getLogger(EmailServiceImpl.class);
    private static final String smtpAuthPropertyName = "mail.smtp.auth";
    private static final String smtpConfigurationKeyAlias = "smtp";
    private static final String smtpHost = "mail.smtp.host";
    private static final String smtpPort = "mail.smtp.port";

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private SmtpHelper configurationSource;
    @Autowired private EmailSettingsCacheService emailSettingsCacheService;

    private SmtpEncryptionType encryptionType;
    private String username;
    private String password;
    private String host;
    private String port;
    private Map<String, String> smtpSettings = new HashedMap<String, String>();

    @Override
    public void sendMessage(EmailMessage data) throws MessagingException {
        populateEmailSettings();
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
     * Method to update SMTP metadata information.
     */
    private void populateEmailSettings() {
        try {
            encryptionType = globalSettingDao.getEnum(GlobalSettingType.SMTP_ENCRYPTION_TYPE, SmtpEncryptionType.class);
            username = globalSettingDao.getString(GlobalSettingType.SMTP_USERNAME);
            password = globalSettingDao.getString(GlobalSettingType.SMTP_PASSWORD);
            host = configurationSource.getCommonProperty(SmtpPropertyType.HOST);
            port = configurationSource.getCommonProperty(SmtpPropertyType.PORT);
            smtpSettings = configurationSource.getSmtpConfigSettings();
        } catch (Exception e) {
            //When database goes down on a running system, retrieve the configurations from cache.
            if(emailSettingsCacheService != null) {
                populateFromCache();
            } else {
                //When database is down and and try to start services, retrieve configurations from cache.
                populateFromFile();
            }
        }
    }

    /**
     * Retrieve the configurations from cache.
     */
    private void populateFromCache() {
        log.error("Error Retrieving data from Database. Populating old values from cache.");
        encryptionType = SmtpEncryptionType
                .valueOf(emailSettingsCacheService.getValue(SystemEmailSettingsType.SMTP_ENCRYPTION_TYPE));
        username = emailSettingsCacheService.getValue(SystemEmailSettingsType.SMTP_USERNAME);
        password = emailSettingsCacheService.getValue(SystemEmailSettingsType.SMTP_PASSWORD);
        host = emailSettingsCacheService.getValue(SystemEmailSettingsType.SMTP_HOST);
        port = emailSettingsCacheService.getValue(SystemEmailSettingsType.SMTP_PORT);
        smtpSettings.clear();
        Map<String, String> configs = getConfigurations();
        if (!CollectionUtils.isEmpty(configs)) {
            smtpSettings.putAll(configs);
        }
        smtpSettings.put(smtpHost, host);
        smtpSettings.put(smtpPort, port);
        SmtpEncryptionType encryptionType = SmtpEncryptionType
                .valueOf(emailSettingsCacheService.getValue(SystemEmailSettingsType.SMTP_ENCRYPTION_TYPE));
        if (encryptionType == SmtpEncryptionType.TLS) {
            smtpSettings.put(SmtpPropertyType.START_TLS_ENABLED.getKey(false), "true");
        } else {
            smtpSettings.remove(SmtpPropertyType.START_TLS_ENABLED.getKey(false));
        }
    }

    /**
     * Retrieve the configurations from file.
     */
    private void populateFromFile() {
        log.error("Error Retrieving data from Database. Populating old values from file.");
        Map<SystemEmailSettingsType, String> metadataMap = EmailSettingsCacheServiceImpl.readFromFile();
        encryptionType = SmtpEncryptionType.valueOf(metadataMap.get(SystemEmailSettingsType.SMTP_ENCRYPTION_TYPE));
        username = metadataMap.get(SystemEmailSettingsType.SMTP_USERNAME);
        password = metadataMap.get(SystemEmailSettingsType.SMTP_PASSWORD);
        host = metadataMap.get(SystemEmailSettingsType.SMTP_HOST);
        port = metadataMap.get(SystemEmailSettingsType.SMTP_PORT);
        smtpSettings.clear();
        Map<String, String> configs = getConfigurations();
        if (!CollectionUtils.isEmpty(configs)) {
            smtpSettings.putAll(configs);
        }
        smtpSettings.put(smtpHost, host);
        smtpSettings.put(smtpPort, port);
        SmtpEncryptionType encryptionType = SmtpEncryptionType
                .valueOf(metadataMap.get(SystemEmailSettingsType.SMTP_ENCRYPTION_TYPE));
        if (encryptionType == SmtpEncryptionType.TLS) {
            smtpSettings.put(SmtpPropertyType.START_TLS_ENABLED.getKey(false), "true");
        } else {
            smtpSettings.remove(SmtpPropertyType.START_TLS_ENABLED.getKey(false));
        }
    }

    /**
     * Retrieve additional configurations from configuration.properties file.
     */
    private Map<String, String> getConfigurations() {
        ConfigurationLoader loader = new ConfigurationLoader();
        return loader.getConfigSettings().get(smtpConfigurationKeyAlias);
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
        Transport transport = null;
        transport = session.getTransport(encryptionType.getProtocol());
        try {
            if (authentication != null) {
                String username = authentication.getUserName();
                String password = authentication.getPassword();
                if (!StringUtils.isEmpty(port)) {
                    transport.connect(host, Integer.parseInt(port), username, password);
                } else {
                    transport.connect(host, username, password);
                }
                // Doesn't seem to be necessary. API says it should be called to update headers, but it is
                // expensive so only call if needed.message.saveChanges();
                transport.sendMessage(message, message.getAllRecipients());
                log.debug("Message Sent: " + message.toString());
            } else {
                transport.connect();
                transport.sendMessage(message, message.getAllRecipients());
                log.debug("Message Sent: " + message.toString());
            }
        } catch (NumberFormatException ne) {
            log.error("Unable to send email message, SMTP port number is invalid");
        } finally {
            transport.close();
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
        if (smtpSettings != null) {
            smtpSettings.forEach((key, value) -> properties.put(key, value));
        }
        SmtpAuthenticator authenticator = new SmtpAuthenticator();
        if (authenticator.getPasswordAuthentication() != null) {
            // Make sure we use authentication.
            properties.put(smtpAuthPropertyName, "true");
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

}
