package com.cannontech.web.security;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.util.SpringWebUtil;

@Aspect
public class WebSecurityAdvice {
    private AuthDao authDao;
    
    @Pointcut("within(com.cannontech.web..*)")
    public void inWebLayer() {}
    
    @Before("inWebLayer() && @annotation(checkRole)")
    public void doHasCheckRole(CheckRole checkRole) throws Exception {
        final LiteYukonUser user = SpringWebUtil.getYukonUser();
        
        for (final int roleId : checkRole.roleId()) {
            boolean hasRole = authDao.checkRole(user, roleId);
            if (!hasRole) throw new NotAuthorizedException("User " + user + " is not authorized to access this page.");
        }
    }
    
    @Autowired
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
    
}
