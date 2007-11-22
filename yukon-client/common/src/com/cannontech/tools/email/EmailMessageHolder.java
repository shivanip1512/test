package com.cannontech.tools.email;


public interface EmailMessageHolder {
    public String getRecipient();
    public String getSubject();
    public String getBody();
    
    // only needs to be set if you plan on sending with EmailService.sendHTMLMessage()
    public String getHtmlBody();
    
}
