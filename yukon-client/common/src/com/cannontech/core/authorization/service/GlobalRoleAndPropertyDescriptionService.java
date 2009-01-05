package com.cannontech.core.authorization.service;

import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.data.lite.LiteYukonUser;

public class GlobalRoleAndPropertyDescriptionService extends RoleAndPropertyDescriptionService {
    
    private RoleDao roleDao;

    @Override
    protected boolean checkRole(LiteYukonUser user, int intForFQN) {
        boolean hasRole = roleDao.checkRole(intForFQN);
        return hasRole;
    }

    @Override
    protected boolean checkRoleProperty(LiteYukonUser user, int intForFQN) {
        boolean propertyValue = roleDao.checkGlobalRoleProperty(intForFQN);
        return propertyValue;
    }
    
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

}
