package com.cannontech.tools.email;

import java.util.ArrayList;
import java.util.List;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;

public class EmailAttachmentMessage extends EmailHtmlMessage {

    private List<DataSource> attachments = new ArrayList<DataSource>();

    public EmailAttachmentMessage(InternetAddress from, InternetAddress[] to, InternetAddress[] cc, 
                               InternetAddress[] bcc, String subject, String body, String htmlBody) throws MessagingException {
        super(from, to, cc, bcc, subject, body, htmlBody);
    }
    public EmailAttachmentMessage(InternetAddress from, InternetAddress[] to, String subject, String body, String htmlBody) throws MessagingException {
        super(from, to, null, null, subject, body, htmlBody);
    }

    public EmailAttachmentMessage(InternetAddress from, InternetAddress[] to, String subject, String body) throws MessagingException {
        this(from, to, subject, body, null);
    }

    public EmailAttachmentMessage(InternetAddress[] to, String subject, String body) throws MessagingException {
        this(null, to, subject, body, null);
    }

    public List<DataSource> getAttachments() {
        return attachments;
    }

    public void addAttachment(DataSource dataSource) {
        attachments.add(dataSource);
    }

    @Override
    public MimeMultipart createBodyContent() throws MessagingException {
        MimeMultipart multipart = super.createBodyContent();
        for (DataSource dataSource : getAttachments()) {
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(new DataHandler(dataSource));
            messageBodyPart.setFileName(dataSource.getName());
            multipart.addBodyPart(messageBodyPart);
        }
        return multipart;
    }
}