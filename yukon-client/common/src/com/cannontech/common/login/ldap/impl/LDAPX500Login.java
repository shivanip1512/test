package com.cannontech.common.login.ldap.impl;

import com.cannontech.common.login.ldap.LDAPLogin;
import com.cannontech.system.YukonSetting;

public class LDAPX500Login extends LDAPLogin {
    
    @Override
    public boolean doLoginAction(final String username, final String password) {
        String ldapDn = yukonSettingsDao.getSettingStringValue(YukonSetting.LDAP_DN);
        String ldapUserSuffix = yukonSettingsDao.getSettingStringValue(YukonSetting.LDAP_USER_SUFFIX);
        String ldapUserPrefix = yukonSettingsDao.getSettingStringValue(YukonSetting.LDAP_USER_PREFIX);
        String user = ldapUserPrefix + username + "," + ldapUserSuffix + "," + ldapDn;
        boolean result = connect(user, password);
        return result;
    }
    
    @Override
    public String getConnectionURL() {
        String host = yukonSettingsDao.getSettingStringValue(YukonSetting.LDAP_SERVER_ADDRESS);
        String port = yukonSettingsDao.getSettingStringValue(YukonSetting.LDAP_SERVER_PORT);
        String url = "ldap://" + host + ":" + port;
        return url;
    }
    
    @Override
    public String getConnectionTimeout() {
        int timeout = yukonSettingsDao.getSettingIntegerValue(YukonSetting.LDAP_SERVER_TIMEOUT);
        int timeoutInMillis = timeout * 1000;
        String result = Integer.toString(timeoutInMillis);
        return result;
    }
}
