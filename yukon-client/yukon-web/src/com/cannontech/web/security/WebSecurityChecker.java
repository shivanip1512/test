package com.cannontech.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.util.SpringWebUtil;

public class WebSecurityChecker {
    private AuthDao authDao;
    
    public void checkRole(int... roleIds) {
        final LiteYukonUser user = getYukonUser();
        
        for (final int roleId : roleIds) {
            boolean hasRole = authDao.checkRole(user, roleId);
            if (hasRole) return;
        }
        
        throw new NotAuthorizedException("User " + user + " is not authorized to access this page.");
    }
    
    public void checkRoleProperty(int... rolePropertyIds) {
        final LiteYukonUser user = getYukonUser();
        
        for (final int propertId : rolePropertyIds) {
            boolean hasRoleProperty = authDao.checkRoleProperty(user, propertId);
            if (hasRoleProperty) return;
        }
        
        throw new NotAuthorizedException("User " + user + " is not authorized to access this page.");
    }
    
    private LiteYukonUser getYukonUser() {
        try {
            LiteYukonUser user = SpringWebUtil.getYukonUser();
            return user;
        } catch (ServletRequestBindingException e) {
            throw new UnsupportedOperationException("Web security checks cannont be done outside of a DispatcherServlet request");
        }
    }
    
    @Autowired
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
    
}
