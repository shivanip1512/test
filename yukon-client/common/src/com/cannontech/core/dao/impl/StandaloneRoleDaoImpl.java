package com.cannontech.core.dao.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.StandaloneRoleDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;

public class StandaloneRoleDaoImpl implements StandaloneRoleDao {
    private RolePropertyDao rolePropertyDao;

    public String getGlobalPropertyValue(int rolePropertyId) {
        return rolePropertyDao.getPropertyStringValue(YukonRoleProperty.getForId(rolePropertyId), null);
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

}
