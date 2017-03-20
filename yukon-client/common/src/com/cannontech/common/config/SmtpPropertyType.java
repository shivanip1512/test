package com.cannontech.common.config;

import com.cannontech.system.GlobalSettingType;

public enum SmtpPropertyType {
    HOST("host", GlobalSettingType.SMTP_HOST),
    PORT("port", GlobalSettingType.SMTP_PORT),
    START_TLS_ENABLED("starttls.enable", GlobalSettingType.SMTP_TLS_ENABLED);

    private String propertyName;
    private GlobalSettingType globalSettingType;

    private SmtpPropertyType(String type, GlobalSettingType globalSettingType) {
        this.propertyName = type;
        this.globalSettingType = globalSettingType;
    }

    public GlobalSettingType getGlobalSettingType() {
        return globalSettingType;
    }

    public String getPropertyName() {
        return propertyName;
    }

    protected String getRegEx() {
        return "mail.smtp??." + getPropertyName();
    }

    public String getKey(boolean isSmpts) {
        if (isSmpts) {
            return "mail.smtps." + getPropertyName();
        } else {
            return "mail.smtp." + getPropertyName();
        }
    }

}
