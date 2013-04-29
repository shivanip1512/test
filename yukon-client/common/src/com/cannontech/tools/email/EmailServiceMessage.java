package com.cannontech.tools.email;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class EmailServiceMessage {
    private final InternetAddress from;
    private final InternetAddress[] to;
    private final InternetAddress[] cc;
    private final InternetAddress[] bcc;
    private final String subject;
    private final MimeMultipart body;
    
    public EmailServiceMessage(InternetAddress from, InternetAddress[] to, InternetAddress[] cc, 
                               InternetAddress[] bcc, String subject, MimeMultipart body) throws AddressException {
    	if (from != null) {
    		this.from = new InternetAddress(from.getAddress());
    	} else {
    		this.from = null;
    	}
        
        this.to = new InternetAddress[to.length];
        System.arraycopy(to, 0, this.to, 0, to.length);
        
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
    
    public EmailServiceMessage(InternetAddress from, InternetAddress[] to, String subject, String body) throws MessagingException {
        this(from, to, null, null, subject, createMultipartBody(body));
    }
    
    public EmailServiceMessage(InternetAddress[] to, String subject, String body) throws MessagingException {
        this(null, to, null, null, subject, createMultipartBody(body));
    }
    
    private static MimeMultipart createMultipartBody(String body) throws MessagingException {
        MimeMultipart multipart = new MimeMultipart();
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(body);
        multipart.addBodyPart(bodyPart);
        
        return multipart;
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
    
    public MimeMultipart getBody() {
        return body;
    }
}
