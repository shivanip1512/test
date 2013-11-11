package com.cannontech.common.login.ldap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.authentication.service.AuthenticationProvider;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.dao.GlobalSettingDao;

public abstract class LDAPLogin implements AuthenticationProvider {
     @Autowired protected GlobalSettingDao globalSettingDao;
    
    public boolean login(final LiteYukonUser user, final String password) {
        if (user == null || StringUtils.isBlank(password)) return false;
        boolean result = doLoginAction(user.getUsername(), password);
        return result;
    }
    
    public abstract boolean doLoginAction(String username, String password);
    
    public abstract String getConnectionURL();
    
    public abstract String getConnectionTimeout();
}
