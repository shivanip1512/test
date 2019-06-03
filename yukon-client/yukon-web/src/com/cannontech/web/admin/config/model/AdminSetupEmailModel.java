package com.cannontech.web.admin.config.model;

public class AdminSetupEmailModel {
    private String from;
    private String to;

    public AdminSetupEmailModel()
    {
        from = "yukon@Eaton.com";
        to = "testTo@Eaton.com";
    }
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
}
