package com.cannontech.common.login.ldap.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.login.ldap.LDAPService;
import com.cannontech.core.authentication.service.AuthenticationProvider;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class LDAPADLogin  implements AuthenticationProvider {
    @Autowired protected GlobalSettingDao globalSettingDao;
    private static Logger log = YukonLogManager.getLogger(LDAPADLogin.class);
    protected LDAPService ldapService;
    
    @Override
    public boolean login(final LiteYukonUser user, final String password) {
        if (user == null || StringUtils.isBlank(password)) return false;
        boolean result = doLoginAction(user.getUsername(), password);
        return result;
    }
   
    public boolean doLoginAction(final String username, final String password) {
        String domainName = globalSettingDao.getString(GlobalSettingType.AD_NTDOMAIN);
        String user = domainName + "\\" + username;
        boolean result = connect(user, password);
        return result;
    }

    public String getConnectionURL() {
        String hosts = globalSettingDao.getString(GlobalSettingType.AD_SERVER_ADDRESS);
        String ports = globalSettingDao.getString(GlobalSettingType.AD_SERVER_PORT);

        List<String> hostList = buildListForUrlInfo(hosts);
        List<String> portList = buildListForUrlInfo(ports);

        return getURLs(hostList, portList);

    }

    /**
     * Returns a string that contains space separated list of urls and also provides a default port if
     * required
     */

    private String getURLs(List<String> hostList, List<String> portList) {
        StringBuilder urls = new StringBuilder();
        int index = 0;
        for (String host : hostList) {

            if (index > portList.size() - 1) {
                portList.add(index, (String) GlobalSettingType.AD_SERVER_PORT.getDefaultValue());
            }

            urls.append("ldap://" + host + ":" + portList.get(index) + " ");
            index++;
        }
        return urls.toString();
    }

    /**
     * Builds and returns a list of url information (host,port) using space delimiter.
     */

    private List<String> buildListForUrlInfo(String urlInfo) {

        String[] urlInfoArr = urlInfo.split(" ");

        List<String> urlInfoList = new ArrayList<String>(urlInfoArr.length);
        Collections.addAll(urlInfoList, urlInfoArr);

        return urlInfoList;
    }
   
    public String getConnectionTimeout() {
        int timeout = globalSettingDao.getInteger(GlobalSettingType.AD_SERVER_TIMEOUT);
        int timeoutInMillis = timeout * 1000;
        String result = Integer.toString(timeoutInMillis);
        return result;
    }

    public boolean connect(final String username, final String password) {
        String url = getConnectionURL();
        String timeout = getConnectionTimeout();
        boolean sslcheck = globalSettingDao.getBoolean(GlobalSettingType.AD_SSL_ENABLED);
        Context ctx = null;
        try {
            if (sslcheck) {
                ctx = ldapService.getSSLContext(url, username, password, timeout);
            } else {
                ctx = ldapService.getContext(url, username, password, timeout);
            }
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

    public void setLdapService(final LDAPService ldapService) {
        this.ldapService = ldapService;
    }
}
