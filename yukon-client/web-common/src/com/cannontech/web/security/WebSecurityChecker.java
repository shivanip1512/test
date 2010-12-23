package com.cannontech.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.util.SpringWebUtil;

public class WebSecurityChecker {
    private RolePropertyDao rolePropertyDao;
    private ConfigurationSource configurationSource;
    
    public void checkDevelopmentMode() {
        final LiteYukonUser user = getYukonUser();
        boolean result = configurationSource.getBoolean(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE);
        if (!result) {
            throw new NotAuthorizedException("User " + user + " is not authorized to access this page.");
        }
    }
    
    public void checkRole(YukonRole... roles) {
        final LiteYukonUser user = getYukonUser();
        
        for (final YukonRole role : roles) {
            boolean hasRole = rolePropertyDao.checkRole(role, user);
            if (hasRole) return;
        }
        
        throw new NotAuthorizedException("User " + user + " is not authorized to access this page.");
    }
    
    public void checkRoleProperty(YukonRoleProperty... rolePropertyIds) {
        final LiteYukonUser user = getYukonUser();
        
        for (final YukonRoleProperty property : rolePropertyIds) {
            boolean hasRoleProperty = rolePropertyDao.checkProperty(property, user);
            if (hasRoleProperty) return;
        }
        
        throw new NotAuthorizedException("User " + user + " is not authorized to access this page.");
    }
    
    public void checkFalseRoleProperty(YukonRoleProperty... rolePropertyIds) {
        final LiteYukonUser user = getYukonUser();
        
        for (final YukonRoleProperty property : rolePropertyIds) {
            boolean hasRoleProperty = rolePropertyDao.checkProperty(property, user);
            if (hasRoleProperty) {
            	throw new NotAuthorizedException("User " + user + " is not authorized to access this page.");
            }
        }
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
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
    
}
