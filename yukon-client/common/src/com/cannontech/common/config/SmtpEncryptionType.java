package com.cannontech.common.config;

import com.cannontech.common.i18n.DisplayableEnum;

public enum SmtpEncryptionType implements DisplayableEnum {
    NONE("smtp"),
    SSL("smtps"),
    TLS("smtp");

    private String protocol;

    private SmtpEncryptionType(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.mailEncryptionType." + name();
    }
    
}
