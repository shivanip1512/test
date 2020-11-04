package com.cannontech.tools.email;

public enum SystemEmailSettingsType {
    WATCHDOG_SUBSCRIBER_EMAILS("watchdog_subscriber_emails"),
    SMTP_USERNAME("smtp_username"),
    SMTP_PASSWORD("smtp_password"),
    MAIL_FROM_ADDRESS("mail_from_address"),
    SMTP_PROTOCOL("smtp_protocol");

    private String key;

    SystemEmailSettingsType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
