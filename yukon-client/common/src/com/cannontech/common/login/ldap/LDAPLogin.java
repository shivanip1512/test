package com.cannontech.common.login.ldap;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.authentication.service.AuthenticationProvider;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.data.lite.LiteYukonUser;

public abstract class LDAPLogin implements AuthenticationProvider {
    private static Logger log = YukonLogManager.getLogger(LDAPLogin.class);
    protected RoleDao roleDao;
    protected LDAPService ldapService;
    
    public boolean login(final LiteYukonUser user, final String password) {
        if (user == null || password == null) return false;
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
    
    public void setRoleDao(final RoleDao roleDao) {
        this.roleDao = roleDao;
    }
    
    public void setLdapService(final LDAPService ldapService) {
        this.ldapService = ldapService;
    }

}
