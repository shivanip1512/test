package com.cannontech.core.users.service.impl;

import java.sql.SQLException;
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
    public void updateUserGroup(UserGroup userGroup) {
        try {
            userGroup.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TreeMultimap<LiteYukonGroup, LiteUserGroup> getAssociations(YukonRole yukonRole) {
        TreeMultimap<LiteYukonGroup, LiteUserGroup> userGroupAssociations = TreeMultimap.create();

        List<UserGroup> userGroups = getAllUserGroups();
        for (UserGroup userGroup : userGroups) {
            LiteUserGroup liteUserGroup = userGroup.getLiteUserGroup();
            
            if (userGroup.hasYukonRole(yukonRole)) {
                LiteYukonGroup liteYukonGroup = userGroup.getRolesToGroupMap().get(yukonRole);
                userGroupAssociations.put(liteYukonGroup, liteUserGroup);
            } else {
                userGroupAssociations.put(NON_EXISTANT_YUKON_GROUP, liteUserGroup);
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