package com.cannontech.common.login.ldap;

import com.cannontech.common.i18n.DisplayableEnum;

public enum LDAPEncryptionType implements DisplayableEnum {
    NONE("ldap"),
    SSL("ldaps"),
    TLS("ldap");

    private String protocol;

    private LDAPEncryptionType(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }

    @Override
    public String getFormatKey() {
        // this could be it's own key, but might as well reuse the smtp one
        return "yukon.common.mailEncryptionType." + name();
    }
    
}