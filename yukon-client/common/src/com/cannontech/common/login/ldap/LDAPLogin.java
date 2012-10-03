package com.cannontech.common.login.ldap;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.authentication.service.AuthenticationProvider;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.dao.YukonSettingsDao;

public abstract class LDAPLogin implements AuthenticationProvider {
    private static Logger log = YukonLogManager.getLogger(LDAPLogin.class);
    protected LDAPService ldapService;
    @Autowired protected YukonSettingsDao yukonSettingsDao;
    
    public boolean login(final LiteYukonUser user, final String password) {
        if (user == null || StringUtils.isBlank(password)) return false;
        boolean result = doLoginAction(user.getUsername(), password);
        return result;
    }
    
    public abstract boolean doLoginAction(String username, String password);
    
    public abstract String getConnectionURL();
    
    public abstract String getConnectionTimeout();
    
    public boolean connect(final String username, final String password) {
        String url = getConnectionURL();
        String timeout = getConnectionTimeout();
        
        Context ctx = null;
        try{
            ctx = ldapService.getContext(url, username, password, timeout);
            return true;
        }
        catch(NamingException e){
            log.error("LDAP Login Failed", e);
            return false;
        } finally {
            ldapService.close(ctx);
        }    
    }
    
    public void setLdapService(final LDAPService ldapService) {
        this.ldapService = ldapService;
    }

}
