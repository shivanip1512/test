package com.cannontech.tools.email;

public class DefaultEmailMessage implements EmailMessageHolder {
    private String body = "";
    private String htmlBody = null;
    private String recipient = "";
    private String subject = "";

    public DefaultEmailMessage(){}
    
    public DefaultEmailMessage(String recipient, String subject,String body){
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getHtmlBody() {
        return htmlBody;
    }

    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
    }

}
