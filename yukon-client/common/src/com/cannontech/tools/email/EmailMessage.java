package com.cannontech.tools.email;

import java.util.List;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;

/**
 * Use this class if you are only sending PLAIN text body.
 * If you are including _any_ html formatting (href, for example), then use {@link EmailHtmlMessage}
 * If you want to add attachments, then use {@link EmailAttachmentMessage}
 */
public class EmailMessage {
    private final InternetAddress from;
    private final InternetAddress[] to;
    private final InternetAddress[] cc;
    private final InternetAddress[] bcc;
    private final String subject;
    private String body;
    
    public EmailMessage(InternetAddress from, InternetAddress[] to, InternetAddress[] cc, 
                               InternetAddress[] bcc, String subject, String body) throws MessagingException {
        if (from != null) {
            this.from = new InternetAddress(from.getAddress());
        } else {
            this.from = null;
        }
        if (to != null) {
            this.to = new InternetAddress[to.length];
            System.arraycopy(to, 0, this.to, 0, to.length);
        } else {
            this.to = null;
        }
        if (cc != null) {
            this.cc = new InternetAddress[cc.length];
            System.arraycopy(cc, 0, this.cc, 0, cc.length);
        } else {
            this.cc = null;
        }
        
        if (bcc != null) {
            this.bcc = new InternetAddress[bcc.length];
            System.arraycopy(bcc, 0, this.bcc, 0, bcc.length);
        } else {
            this.bcc = null;
        }
        
        this.subject = subject;
        
        this.body = body;
    }

    public EmailMessage(InternetAddress from, InternetAddress[] to, String subject, String body) throws MessagingException {
        this(from, to, null, null, subject, body);
    }

    public EmailMessage(InternetAddress[] to, String subject, String body) throws MessagingException {
        this(null, to, null, null, subject, body);
    }

    /**
     * Builds a complete email notification message. This will bcc all the recipients.
     * @throws MessagingException if an error occurs while building the EmailMessage.
     */
    public static EmailMessage newMessageBccOnly(String emailSubject, String emailBody, String sendFromAddress, List<String> receipientEmailAddressesBcc) 
            throws MessagingException {

        InternetAddress sender = new InternetAddress();
        sender.setAddress(sendFromAddress);
        
        InternetAddress[] bccRecipients = receipientEmailAddressesBcc.stream()
                                        .map(recipient -> {
                                            InternetAddress address = new InternetAddress();
                                            address.setAddress(recipient);
                                            return address;
                                        })
                                        .toArray(InternetAddress[]::new);
        return new EmailMessage(sender, null, null, bccRecipients, emailSubject, emailBody);
    }
    
    public InternetAddress getFrom() {
        return from;
    }

    public InternetAddress[] getTo() {
        return to;
    }

    public InternetAddress[] getCc() {
        return cc;
    }

    public InternetAddress[] getBcc() {
        return bcc;
    }

    public String getSubject() {
        return subject;
    }

    /**
     * @return the original body string.
     */
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    
    /**
     * Returns the content MimeMultipart consisting of plain text body
     * @throws MessagingException
     */
    public MimeMultipart createBodyContent() throws MessagingException {
        
        MimeMultipart multipart = new MimeMultipart();
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(body, "text/plain");
        multipart.addBodyPart(bodyPart);

        return multipart;
    }
    
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Recipients: ").append(InternetAddress.toString(to));
        str.append(" From: ").append(from.toString());
        str.append(" Subject: ").append(subject);
        str.append(" Message Body: ").append(StringUtils.abbreviate(body, 1000));
        return str.toString();
    }
}
