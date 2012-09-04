package com.cannontech.core.users.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.core.users.service.UserGroupService;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.user.UserGroup;
import com.google.common.collect.Lists;
import com.google.common.collect.TreeMultimap;

public class UserGroupServiceImpl implements UserGroupService {

    @Autowired private UserGroupDao userGroupDao;

    public static LiteYukonGroup NON_EXISTANT_YUKON_GROUP = new LiteYukonGroup();
    {
        NON_EXISTANT_YUKON_GROUP.setGroupID(-1000);
        NON_EXISTANT_YUKON_GROUP.setGroupName("NONE");
    }

    @Override
    public TreeMultimap<LiteYukonGroup, com.cannontech.database.db.user.UserGroup> getAssociations(YukonRole yukonRole) {
        TreeMultimap<LiteYukonGroup, com.cannontech.database.db.user.UserGroup> userGroupAssociations = TreeMultimap.create();

        List<UserGroup> userGroups = getAllUserGroups();
        for (UserGroup userGroup : userGroups) {
            com.cannontech.database.db.user.UserGroup dbUserGroup = userGroup.getUserGroup();
            
            if (userGroup.hasYukonRole(yukonRole)) {
                LiteYukonGroup liteYukonGroup = userGroup.getRolesToGroupMap().get(yukonRole);
                userGroupAssociations.put(liteYukonGroup, dbUserGroup);
            } else {
                userGroupAssociations.put(NON_EXISTANT_YUKON_GROUP, dbUserGroup);
            }
        }
        
        return userGroupAssociations;
    }
    
    private List<UserGroup> getAllUserGroups() {
        List<UserGroup> userGroups = Lists.newArrayList();

        List<LiteUserGroup> allLiteUserGroups = userGroupDao.getAllLiteUserGroups();
        for (LiteUserGroup liteUserGroup : allLiteUserGroups) {
            UserGroup userGroup = userGroupDao.getUserGroup(liteUserGroup.getUserGroupId());
            userGroups.add(userGroup);
        }
        
        return userGroups;
    }
}