package com.cannontech.common.login.ldap.impl;

import com.cannontech.common.login.ldap.LDAPLogin;
import com.cannontech.system.GlobalSettingType;

public class LDAPX500Login extends LDAPLogin {
    
    @Override
    public boolean doLoginAction(final String username, final String password) {
        String ldapDn = globalSettingDao.getString(GlobalSettingType.LDAP_DN);
        String ldapUserSuffix = globalSettingDao.getString(GlobalSettingType.LDAP_USER_SUFFIX);
        String ldapUserPrefix = globalSettingDao.getString(GlobalSettingType.LDAP_USER_PREFIX);
        String user = ldapUserPrefix + username + "," + ldapUserSuffix + "," + ldapDn;
        boolean result = connect(user, password);
        return result;
    }
    
    @Override
    public String getConnectionURL() {
        String host = globalSettingDao.getString(GlobalSettingType.LDAP_SERVER_ADDRESS);
        String port = globalSettingDao.getString(GlobalSettingType.LDAP_SERVER_PORT);
        String url = "ldap://" + host + ":" + port;
        return url;
    }
    
    @Override
    public String getConnectionTimeout() {
        int timeout = globalSettingDao.getInteger(GlobalSettingType.LDAP_SERVER_TIMEOUT);
        int timeoutInMillis = timeout * 1000;
        String result = Integer.toString(timeoutInMillis);
        return result;
    }
}
