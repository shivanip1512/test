package com.cannontech.common.config;

import com.cannontech.system.GlobalSettingType;

public enum SmtpPropertyType {
    HOST("host", GlobalSettingType.SMTP_HOST),
    PORT("port", GlobalSettingType.SMTP_PORT),
    START_TLS_ENABLED("starttls.enable", GlobalSettingType.SMTP_TLS_ENABLED);

    private String propertyName;
    private GlobalSettingType globalSettingType;

    public GlobalSettingType getGlobalSettingType() {
        return globalSettingType;
    }

    private SmtpPropertyType(String type, GlobalSettingType globalSettingType) {
        this.setPropertyName(type);
        this.globalSettingType = globalSettingType;
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

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
}
