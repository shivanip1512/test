package com.cannontech.tools.email;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

/**
 * Use this class if you would like to include an (alternative) HTML version of body.
 * The body and htmlBody shall contain identical information, simply formatted for plain vs hmtl.
 * The email client will display whichever it has capabilities to do, with plain being default.
 */
public class EmailHtmlMessage extends EmailMessage {
    private String htmlBody = null;
    
    public EmailHtmlMessage(InternetAddress from, InternetAddress[] to, InternetAddress[] cc, 
                               InternetAddress[] bcc, String subject, String body, String htmlBody) throws MessagingException {
        super(from, to, cc, bcc, subject, body);
        this.htmlBody = htmlBody;
    }
    
    public EmailHtmlMessage(InternetAddress from, InternetAddress[] to, String subject, String body, String htmlBody) throws MessagingException {
        this(from, to, null, null, subject, body, htmlBody);
    }
    
    public EmailHtmlMessage(InternetAddress[] to, String subject, String body, String htmlBody) throws MessagingException {
        this(null, to, null, null, subject, body, htmlBody);
    }

    /**
     * @return the original html body string.
     */
    public String getHtmlBody() {
        return htmlBody;
    }

    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
    }
    
    @Override
    public MimeMultipart createBodyContent() throws MessagingException {
        MimeMultipart multipart = super.createBodyContent();
        if (htmlBody != null) {
            // alternative sub type is needed to indicate optional plain text vs html exist in the multipart.
            multipart.setSubType("alternative");
            MimeBodyPart htmlBodyPart = new MimeBodyPart();
            htmlBodyPart.setContent(htmlBody, "text/html");
            multipart.addBodyPart(htmlBodyPart);
        }
        return multipart;
    }
}
