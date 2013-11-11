package com.cannontech.common.login.ldap.impl;

import java.io.IOException;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.login.ldap.LDAPLogin;
import com.cannontech.common.login.ldap.LDAPService;
import com.cannontech.system.GlobalSettingType;

public class LDAPADLogin extends LDAPLogin {
    private static Logger log = YukonLogManager.getLogger(LDAPADLogin.class);
    protected LDAPService ldapService;

    @Override
    public boolean doLoginAction(final String username, final String password) {
        String domainName = globalSettingDao.getString(GlobalSettingType.AD_NTDOMAIN);
        String user = domainName + "\\" + username;
        boolean result = connect(user, password);
        return result;
    }

    @Override
    public String getConnectionURL() {
        String host = globalSettingDao.getString(GlobalSettingType.AD_SERVER_ADDRESS);
        String port = globalSettingDao.getString(GlobalSettingType.AD_SERVER_PORT);
        String url = "ldap://" + host + ":" + port;
        return url;
    }

    @Override
    public String getConnectionTimeout() {
        int timeout = globalSettingDao.getInteger(GlobalSettingType.AD_SERVER_TIMEOUT);
        int timeoutInMillis = timeout * 1000;
        String result = Integer.toString(timeoutInMillis);
        return result;
    }

    public boolean connect(final String username, final String password) {
        String url = getConnectionURL();
        String timeout = getConnectionTimeout();
        String sslcheck = globalSettingDao.getString(GlobalSettingType.AD_SSL_ENABLED);
        Context ctx = null;
        try {
            if (Boolean.valueOf(sslcheck)) {
                ldapService.getSSLContext(url, username, password, timeout);
                return true;
            } else
                ctx = ldapService.getContext(url, username, password, timeout);
                return true;
        } catch (NamingException e) {
            log.error("LDAP Login Failed", e);
            return false;
        } catch (IOException e) {
            log.warn("LDAP Login Failed", e);
            return false;
        } finally {
            ldapService.close(ctx);
        }
    }

    public void setLdapService(final LDAPService ldapService) {
        this.ldapService = ldapService;
    }
}
