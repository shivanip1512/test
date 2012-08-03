package com.cannontech.core.users.service;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.user.UserGroup;
import com.google.common.collect.TreeMultimap;

/**
 * These methods return a UserGroup object that contains the Yukon user and Yukon group information attached to the UserGroup.  
 * If you are looking to update the information or just see the UserGroup table information use the UserGroupDao instead.
 */
public interface UserGroupService {

    /**
     * 
     */
    public void updateUserGroup(UserGroup userGroup);

    /**
     * 
     */
    public TreeMultimap<LiteYukonGroup, LiteUserGroup> getAssociations(YukonRole yukonRole);

}