package com.cannontech.watchdogs.util;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.SmtpEncryptionType;
import com.cannontech.tools.smtp.SmtpMetadataConstants;

public class WatchdogEmailUtil {
    private static final Logger log = YukonLogManager.getLogger(WatchdogEmailUtil.class);
    private static final String SMTP_AUTH_PROPERTY_NAME = "mail.smtp.auth";
    private static final String SMTP_HOST = "mail.smtp.host";
    private static final String SMTP_PORT = "mail.smtp.port";
    private static final String RESOURCE_PATH = "\\common\\i18n\\en_US\\com\\cannontech\\yukon\\watchdog\\root";

    private static Map<String, String> metadataMap = null;

    public static void sendEmail() {
        try {
            metadataMap = WatchdogDatabaseFileUtil.readFromFile();
            if (StringUtils.isEmpty(metadataMap.get(SmtpMetadataConstants.SUBSCRIBER_EMAIL_IDS))) {
                log.warn("No user subscribed for notification for watchdog.");
                return;
            }
            Session session = getSession();
            MimeMessage message = getMessage(session);
            Transport transport = null;
            String port = metadataMap.get(SmtpMetadataConstants.SMTP_PORT);
            String host = metadataMap.get(SmtpMetadataConstants.SMTP_HOST);
            String userName = metadataMap.get(SmtpMetadataConstants.SMTP_USERNAME);
            String password = metadataMap.get(SmtpMetadataConstants.SMTP_PASSWORD);
            String protocol = SmtpEncryptionType.valueOf(metadataMap.get(SmtpMetadataConstants.SMTP_ENCRYPTION_TYPE)).getProtocol();
            
            transport = session.getTransport(protocol);
            if (getAuthenticator().getPasswordAuthentication() != null) {
                if (!StringUtils.isEmpty(port)) {
                    transport.connect(host, Integer.parseInt(port), userName, password);
                } else {
                    transport.connect(host, userName, password);
                }
                transport.sendMessage(message, message.getAllRecipients());
            } else {
                transport.connect();
                transport.sendMessage(message, message.getAllRecipients());
            }
        } catch (Exception e) {
            log.error("Unable to send Emial", e);
        }
    }

    private static MimeMessage getMessage(Session session) {
        MimeMessage message = new MimeMessage(session);
        try {
            message.setHeader("X-Mailer", "YukonEmail");

            // Setup message source for reading i18n messages.
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            String userDir = System.getProperty("user.dir");
            String resourceDir = userDir.substring(0, userDir.lastIndexOf("\\") + 1).concat(RESOURCE_PATH);
            messageSource.setBasename(new File(resourceDir).toURI().toString());

            message.setSubject(messageSource.getMessage("yukon.watchdog.notification.subject", null, Locale.ENGLISH));

            StringBuilder msgBuilder = new StringBuilder(
                    messageSource.getMessage("yukon.watchdog.notification.text", null, Locale.ENGLISH));
            msgBuilder.append("\n\n");
            msgBuilder.append(messageSource.getMessage("yukon.watchdog.notification.DATABASE", null, Locale.ENGLISH));

            MimeMultipart multipart = new MimeMultipart();
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(msgBuilder.toString(), "text/plain");
            multipart.addBodyPart(bodyPart);
            message.setContent(multipart);

            InternetAddress receipents[] = retrieveReceipents();
            message.addRecipients(Message.RecipientType.BCC, receipents);
            InternetAddress fromAddress = new InternetAddress(metadataMap.get(SmtpMetadataConstants.MAIL_FROM_ADDRESS));
            message.setFrom(fromAddress);
        } catch (Exception e) {
            log.error("Error occurred while creating message", e);
        }
        return message;
    }

    private static InternetAddress[] retrieveReceipents() {
        String commaSeparatedIds = metadataMap.get(SmtpMetadataConstants.SUBSCRIBER_EMAIL_IDS);
        return Arrays.asList(commaSeparatedIds.split("\\s*,\\s*"))
                     .stream()
                     .map(recipient -> {
                         InternetAddress address = new InternetAddress();
                         address.setAddress(recipient);
                         return address;
                         })
                     .toArray(InternetAddress[]::new);
    }

    private static Session getSession() {
        Session session = null;
        Properties properties = new Properties();
        properties.put(SMTP_HOST, metadataMap.get(SmtpMetadataConstants.SMTP_HOST));
        properties.put(SMTP_PORT, metadataMap.get(SmtpMetadataConstants.SMTP_PORT));
        SmtpAuthenticator authenticator = getAuthenticator();
        if (authenticator.getPasswordAuthentication() != null) {
            properties.put(SMTP_AUTH_PROPERTY_NAME, "true");
            session = Session.getInstance(properties, authenticator);
        } else {
            session = Session.getInstance(properties);
        }
        return session;
    }

    private static SmtpAuthenticator getAuthenticator() {
        return new SmtpAuthenticator(metadataMap.get(SmtpMetadataConstants.SMTP_USERNAME),
                metadataMap.get(SmtpMetadataConstants.SMTP_PASSWORD));
    }

    private static class SmtpAuthenticator extends Authenticator {
        private PasswordAuthentication authentication = null;

        public SmtpAuthenticator(String userName, String password) {
            if (!StringUtils.isBlank(userName) && !StringUtils.isBlank(password)) {
                authentication = new PasswordAuthentication(userName, password);
            }
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return authentication;
        }
    }
}
