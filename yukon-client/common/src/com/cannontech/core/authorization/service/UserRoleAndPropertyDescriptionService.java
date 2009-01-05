package com.cannontech.core.authorization.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;

public class UserRoleAndPropertyDescriptionService extends RoleAndPropertyDescriptionService {

    private AuthDao authDao;
    
    @Override
    protected boolean checkRole(LiteYukonUser user, int intForFQN) {
        boolean hasRole = authDao.checkRole(user, intForFQN);
        return hasRole;
    }

    @Override
    protected boolean checkRoleProperty(final LiteYukonUser user, int intForFQN) {
        boolean hasProperty = authDao.checkRoleProperty(user, intForFQN);
        return hasProperty;
    }
    
    @Autowired
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
}
