package com.cannontech.core.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.YukonGroupService;
import com.cannontech.database.data.lite.LiteYukonGroup;

public class YukonGroupServiceImpl implements YukonGroupService {
    
    @Autowired private RoleDao roleDao;
    
    @Override
    public LiteYukonGroup getGroupByYukonRoleAndUser(YukonRole yukonRole, int userId) {

        Map<YukonRole, LiteYukonGroup> rolesAndGroupsForUser = roleDao.getRolesAndGroupsForUser(userId);
        return rolesAndGroupsForUser.get(yukonRole);
        
    }
}