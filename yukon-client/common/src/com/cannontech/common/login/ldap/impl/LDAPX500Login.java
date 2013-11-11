package com.cannontech.common.login.ldap.impl;

import java.io.IOException;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.login.ldap.LDAPLogin;
import com.cannontech.common.login.ldap.LDAPService;
import com.cannontech.system.GlobalSettingType;

public class LDAPX500Login extends LDAPLogin {
    private static Logger log = YukonLogManager.getLogger(LDAPX500Login.class);
    protected LDAPService ldapService;

    @Override
    public boolean doLoginAction(final String username, final String password) {
        String ldapDn = globalSettingDao.getString(GlobalSettingType.LDAP_DN);
        String ldapUserSuffix = globalSettingDao.getString(GlobalSettingType.LDAP_USER_SUFFIX);
        String ldapUserPrefix = globalSettingDao.getString(GlobalSettingType.LDAP_USER_PREFIX);
        String user = null;
        /**
         * User can connect to LDAP through any medium (domain,ldapUserPrefix,or existing functionality).
         */
        if ((ldapUserSuffix.endsWith("=") || ldapUserSuffix.isEmpty())
            && (ldapDn.endsWith("=") || ldapDn.isEmpty())) {
            if (ldapUserPrefix.contains("=")) {
                String[] UserPrefix = ldapUserPrefix.split(",|=");
                user = UserPrefix[1] + "\\" + username;
            } else {
                user = ldapUserPrefix + "\\" + username;
            }
        } else if ((ldapUserPrefix.isEmpty() || ldapUserPrefix.endsWith("="))
                   && (ldapUserSuffix.endsWith("=") || ldapUserSuffix.isEmpty()))
        {
            if (ldapDn.contains("=")) {
                String[] ldapDomain = ldapDn.split(",|=");
                user = ldapDomain[1] + "\\" + username;
            } else {
                user = ldapDn + "\\" + username;
            }
        } else
        {
            user = ldapUserPrefix + username + "," + ldapUserSuffix + "," + ldapDn;
        }
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

    public boolean connect(final String username, final String password) {
        String url = getConnectionURL();
        String timeout = getConnectionTimeout();
        String sslcheck = globalSettingDao.getString(GlobalSettingType.LDAP_SSL_ENABLED);
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
