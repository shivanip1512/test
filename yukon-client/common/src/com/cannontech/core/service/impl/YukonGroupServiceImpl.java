package com.cannontech.core.service.impl;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.YukonGroupService;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.google.common.collect.Sets;

public class YukonGroupServiceImpl implements YukonGroupService {
    
    private RoleDao roleDao;
    private YukonUserDao yukonUserDao;
    
    @Override
    public LiteYukonGroup getGroupByYukonRoleAndUser(YukonRole yukonRole, int userId) {

        Map<YukonRole, LiteYukonGroup> rolesAndGroupsForUser = roleDao.getRolesAndGroupsForUser(userId);
        return rolesAndGroupsForUser.get(yukonRole);
        
    }
    
    @Override
    public void  addUserToGroup(int groupId, int userId){
 
        Map<YukonRole, LiteYukonGroup> currentRoleWithUsersGroups = roleDao.getRolesAndGroupsForUser(userId);
        
        Set<YukonRole> yukonRolesForNewGroup = roleDao.getRolesForGroup(groupId);
        for (YukonRole yukonRoleForNewGroup : yukonRolesForNewGroup)  {
            
            if (!currentRoleWithUsersGroups.containsKey(yukonRoleForNewGroup)) continue;
            
            LiteYukonGroup currentConflictingGroup = currentRoleWithUsersGroups.get(yukonRoleForNewGroup);
            yukonUserDao.removeUserFromGroup(userId, currentConflictingGroup.getGroupID());
            
        }
        
        // Add the new login group
        yukonUserDao.addUserToGroup(userId, groupId);
    }
    
    @Override
    public boolean addToGroupWillHaveConflicts(int userId, int groupId) {
        Map<YukonRole, LiteYukonGroup> currentGroupsAndRoles = roleDao.getRolesAndGroupsForUser(userId);
        Set<YukonRole> potentialRoles = roleDao.getRolesForGroup(groupId);
        Set<YukonRole> conflictingRoles = Sets.intersection(potentialRoles, currentGroupsAndRoles.keySet());
        if (!conflictingRoles.isEmpty()) return true;
        return false;
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