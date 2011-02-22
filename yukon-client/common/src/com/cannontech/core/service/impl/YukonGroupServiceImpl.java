package com.cannontech.core.service.impl;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.YukonGroupService;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;

public class YukonGroupServiceImpl implements YukonGroupService {
    
    private RoleDao roleDao;
    private YukonUserDao yukonUserDao;
    
    @Override
    public LiteYukonGroup getGroupByYukonRoleAndUser(YukonRole yukonRole, LiteYukonUser user) {

        Map<YukonRole, LiteYukonGroup> rolesAndGroupsForUser = roleDao.getRolesAndGroupsForUser(user);
        return rolesAndGroupsForUser.get(yukonRole);
        
    }
    
    @Override
    public void  addUserToGroup(LiteYukonGroup newUserGroup, LiteYukonUser user){
 
        Map<YukonRole, LiteYukonGroup> currentRoleWithUsersGroups = roleDao.getRolesAndGroupsForUser(user);
        
        Set<YukonRole> yukonRolesForNewGroup = roleDao.getRolesForGroup(newUserGroup);
        for (YukonRole yukonRoleForNewGroup : yukonRolesForNewGroup)  {
            
            if (!currentRoleWithUsersGroups.containsKey(yukonRoleForNewGroup)) continue;
            
            LiteYukonGroup currentConflictingGroup = currentRoleWithUsersGroups.get(yukonRoleForNewGroup);
            yukonUserDao.removeUserFromGroup(user, currentConflictingGroup);
            
        }
        
        // Add the new login group
        yukonUserDao.addUserToGroup(user, newUserGroup);
    }
    
    // DI Setter
    @Autowired
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    
}