package com.cannontech.tools.email;

import javax.mail.MessagingException;

public interface EmailMessageHolder {
    public void setRecipient(String address) throws MessagingException;
    public void setSubject(String subject) throws MessagingException;
    public void setBody(String body) throws MessagingException;
    
}
