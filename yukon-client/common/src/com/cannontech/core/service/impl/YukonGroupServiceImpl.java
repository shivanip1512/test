package com.cannontech.core.service.impl;

import java.util.Set;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.YukonGroupService;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.SetMultimap;

public class YukonGroupServiceImpl implements YukonGroupService {
    
    private RoleDao roleDao;
    private YukonUserDao yukonUserDao;
    
    @Override
    public LiteYukonGroup getGroupByYukonRoleAndUser(YukonRole yukonRole, LiteYukonUser user) {

        SetMultimap<LiteYukonGroup, YukonRole> groupsAndRolesForUser = roleDao.getGroupsAndRolesForUser(user);
        
        for (Entry<LiteYukonGroup, YukonRole> groupsAndRolesEntry :groupsAndRolesForUser.entries()) {
            YukonRole groupYukonRole = groupsAndRolesEntry.getValue();
            if (groupYukonRole.equals(yukonRole)) {
                LiteYukonGroup yukonRoleGroup = groupsAndRolesEntry.getKey();
                return yukonRoleGroup;
            }
        }
        
        return null;
    }
    
    @Override
    public void  addUserToGroup(LiteYukonGroup newUserGroup, LiteYukonUser user){
        SetMultimap<LiteYukonGroup, YukonRole> currentUsersGroupWithRoles = roleDao.getGroupsAndRolesForUser(user);
        Set<YukonRole> yukonRolesForNewGroup = roleDao.getRolesForGroup(newUserGroup);
        for (YukonRole yukonRoleForNewGroup : yukonRolesForNewGroup)  {
            
            // This role does not exist in the current user's role list.  Skip to the next new role.
            if (!currentUsersGroupWithRoles.containsValue(yukonRoleForNewGroup)) {
                continue;
            }
            
            // Remove any groups associated with the new group.
            for (Entry<LiteYukonGroup, YukonRole> groupWithRolesEntry  : currentUsersGroupWithRoles.entries()) {
                YukonRole yukonRole = groupWithRolesEntry.getValue();
                if (yukonRole.equals(yukonRoleForNewGroup)) {
                    yukonUserDao.removeUserFromGroup(user, groupWithRolesEntry.getKey());
                }
            }
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