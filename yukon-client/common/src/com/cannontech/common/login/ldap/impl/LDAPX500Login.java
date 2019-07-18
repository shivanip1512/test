package com.cannontech.common.login.ldap.impl;

import java.io.IOException;
import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.login.ldap.LDAPService;
import com.cannontech.common.login.ldap.LDAPEncryptionType;
import com.cannontech.core.authentication.service.AuthenticationProvider;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class LDAPX500Login implements AuthenticationProvider {
    @Autowired protected GlobalSettingDao globalSettingDao;
    private static Logger log = YukonLogManager.getLogger(LDAPX500Login.class);
    protected LDAPService ldapService;
    
   @Override
    public boolean login(final LiteYukonUser user, final String password) {
        if (user == null || StringUtils.isBlank(password)) return false;
        boolean result = doLoginAction(user.getUsername(), password);
        return result;
    }
    
 
    public boolean doLoginAction(final String username, final String password) {
        String ldapDn = globalSettingDao.getString(GlobalSettingType.LDAP_DN);
        String ldapUserSuffix = globalSettingDao.getString(GlobalSettingType.LDAP_USER_SUFFIX);
        String ldapUserPrefix = globalSettingDao.getString(GlobalSettingType.LDAP_USER_PREFIX);
        String user = getUserConnectionString(username, ldapUserSuffix, ldapUserPrefix, ldapDn);
        boolean result = connect(user, password);
        return result;
    }

    
    public String getConnectionURL() {
        String host = globalSettingDao.getString(GlobalSettingType.LDAP_SERVER_ADDRESS);
        String port = globalSettingDao.getString(GlobalSettingType.LDAP_SERVER_PORT);
        LDAPEncryptionType encryptionType = globalSettingDao.getEnum(GlobalSettingType.LDAP_SSL_ENABLED, LDAPEncryptionType.class);
        String url = encryptionType.getProtocol() + "://" + host + ":" + port;
        log.debug("LDAP Connection URL: {}", url);
        return url;
    }

    
    public String getConnectionTimeout() {
        int timeout = globalSettingDao.getInteger(GlobalSettingType.LDAP_SERVER_TIMEOUT);
        int timeoutInMillis = timeout * 1000;
        String result = Integer.toString(timeoutInMillis);
        return result;
    }

    public boolean connect(final String username, final String password) {
        String url = getConnectionURL();
        String timeout = getConnectionTimeout();
        LDAPEncryptionType encryptionType = globalSettingDao.getEnum(GlobalSettingType.LDAP_SSL_ENABLED, LDAPEncryptionType.class);
        Context ctx = null;
        try {
            if (encryptionType == LDAPEncryptionType.TLS) {
                ctx = ldapService.getTLSContext(url, username, password, timeout);
            } else {
                ctx = ldapService.getContext(url, username, password, timeout);
            }
            logContext(ctx);
            return true;
        } catch (NamingException e) {
            log.error("LDAP Login Failed", e);
            return false;
        } catch (IOException e) {
            log.error("SSL connection failed", e);
            return false;
        } finally {
            ldapService.close(ctx);
        }
    }

    /**
     * This method return user variable based on the LDAP Domain Name ,LDAP User Prefix that is
     * required for LDAP connection.
     * This method is building user based on the input from customer.it can be directly value of
     * ldapUserPrefix ,ldapDn(like AD implementation)
     * or it can be indirectly value (as CN=NAM or DC=NAM) from customer.It also build the user
     * based on all three parameters ldapUserPrefix,ldapUserSuffix
     * and ldapDn
     */
    private String getUserConnectionString(final String username, final String ldapUserSuffix,final String ldapUserPrefix, final String ldapDn) {
        String user = null;
        if ((ldapUserSuffix.endsWith("=") || ldapUserSuffix.isEmpty()) && (ldapDn.endsWith("=") || ldapDn.isEmpty())) {
            if (ldapUserPrefix.contains("=")) {
                String[] userPrefix = ldapUserPrefix.split(",|=");
                user = userPrefix[1] + "\\" + username;
            } else {
                user = ldapUserPrefix + "\\" + username;
            }
        } else if ((ldapUserPrefix.isEmpty() || ldapUserPrefix.endsWith("=")) && (ldapUserSuffix.endsWith("=") || ldapUserSuffix.isEmpty())) {
            if (ldapDn.contains("=")) {
                String[] ldapDomain = ldapDn.split(",|=");
                user = ldapDomain[1] + "\\" + username;
            } else {
                user = ldapDn + "\\" + username;
            }
        } else {
            user = ldapUserPrefix + username + "," + ldapUserSuffix + "," + ldapDn;
        }
        log.debug("LDAP User Connection String: {}", user);
        return user;
    }

    public void setLdapService(final LDAPService ldapService) {
        this.ldapService = ldapService;
    }
   
    public void logContext(Context ctx) {
      if (log.isDebugEnabled()) {
          try {
              for (Object key : ctx.getEnvironment().keySet()) {
                  log.debug("LDAP Context Key: {}   Value: {}", key,
                            key.equals(Context.SECURITY_CREDENTIALS) ? 
                                    "*****" : ctx.getEnvironment().get(key));   //hide the password
              }
          } catch (NamingException e) {
              log.warn("caught exception in log", e);
          }
      }
  }
}
