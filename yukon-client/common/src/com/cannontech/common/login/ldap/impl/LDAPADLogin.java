package com.cannontech.common.login.ldap.impl;

import com.cannontech.common.login.ldap.LDAPLogin;
import com.cannontech.system.YukonSetting;

public class LDAPADLogin extends LDAPLogin {
    
    @Override
    public boolean doLoginAction(final String username, final String password) {
        String domainName = yukonSettingsDao.getSettingStringValue(YukonSetting.AD_NTDOMAIN);
        String user = domainName + "\\" + username;
        boolean result = connect(user, password);
        return result;
    }
    
    @Override
    public String getConnectionURL() {
        String host = yukonSettingsDao.getSettingStringValue(YukonSetting.AD_SERVER_ADDRESS);
        String port = yukonSettingsDao.getSettingStringValue(YukonSetting.AD_SERVER_PORT);
        String url = "ldap://" + host + ":" + port;
        return url;
    }
    
    @Override
    public String getConnectionTimeout() {
        int timeout = yukonSettingsDao.getSettingIntegerValue(YukonSetting.AD_SERVER_TIMEOUT);
        int timeoutInMillis = timeout * 1000;
        String result = Integer.toString(timeoutInMillis);
        return result;
    }
}
