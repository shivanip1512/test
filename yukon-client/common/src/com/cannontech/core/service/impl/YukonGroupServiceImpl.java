package com.cannontech.core.service.impl;

import java.util.Iterator;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.YukonGroupService;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.google.common.collect.Multimap;

public class YukonGroupServiceImpl implements YukonGroupService {
    
    @Autowired private RoleDao roleDao;
    
    @Override
    public LiteYukonGroup getGroupByYukonRoleAndUser(YukonRole yukonRole, int userId) {
        Validate.notNull(yukonRole);
        Multimap<YukonRole, LiteYukonGroup> rolesAndGroupsForUser = roleDao.getRolesAndGroupsForUser(userId);
        
        // Although this returns a collection this call will always only have one or no results..
        Iterator<LiteYukonGroup> iterator = rolesAndGroupsForUser.get(yukonRole).iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        
        return null;
    }
}