package com.cannontech.watchdogs.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;

public class WatchdogEmailUtil {
    private static final String SMTP_AUTH_PROPERTY_NAME = "mail.smtp.auth";
    private static final String SMTP_HOST = "mail.smtp.host";
    private static final String SMTP_PORT = "mail.smtp.port";

    private static Map<String, String> metadataMap = null;

    public static void sendEmail() throws MessagingException {
        try {
            metadataMap = WatchdogDatabaseFileUtil.readFromFile();
            Session session = getSession();
            MimeMessage message = getMessage(session);
            Transport transport = null;
            String port = metadataMap.get(WatchdogDatabaseFileUtil.SMTP_PORT);
            String host = metadataMap.get(WatchdogDatabaseFileUtil.SMTP_HOST);
            String userName = metadataMap.get(WatchdogDatabaseFileUtil.SMTP_USERNAME);
            String password = metadataMap.get(WatchdogDatabaseFileUtil.SMTP_PASSWORD);
            String protocol = metadataMap.get(WatchdogDatabaseFileUtil.SMTP_ENCRYPTION_TYPE);
            
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
            transport.sendMessage(message, message.getAllRecipients());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static MimeMessage getMessage(Session session) throws MessagingException, IOException {
        MimeMessage message = new MimeMessage(session);
        message.setHeader("X-Mailer", "YukonEmail");
        //TODO: Fix me
        ResourceBundle bundle = ResourceBundle.getBundle("root.xml");
        message.setSubject(bundle.getString("yukon.watchdog.notification.subject"));

        String body = bundle.getString("yukon.watchdog.notification.db.text");
        MimeMultipart multipart = new MimeMultipart();
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(body, "text/plain");
        multipart.addBodyPart(bodyPart);
        message.setContent(multipart);

        InternetAddress receipents[] = retrieveReceipents();
        message.addRecipients(Message.RecipientType.BCC, receipents);
        InternetAddress fromAddress = new InternetAddress(metadataMap.get(WatchdogDatabaseFileUtil.MAIL_FROM_ADDRESS));
        message.setFrom(fromAddress);
        return message;
    }

    private static InternetAddress[] retrieveReceipents() {
        String commaSeparatedIds = metadataMap.get(WatchdogDatabaseFileUtil.SUBSCRIBER_EMAIL_IDS);
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
        properties.put(SMTP_HOST, metadataMap.get(WatchdogDatabaseFileUtil.SMTP_HOST));
        properties.put(SMTP_PORT, metadataMap.get(WatchdogDatabaseFileUtil.SMTP_PORT));
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
        return new SmtpAuthenticator(metadataMap.get(WatchdogDatabaseFileUtil.SMTP_USERNAME),
                metadataMap.get(WatchdogDatabaseFileUtil.SMTP_PASSWORD));
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
